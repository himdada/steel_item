package com.example.demo.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.model.SteelItem;
import com.example.demo.model.SteelItemQuery;
import com.example.demo.repository.SteelItemRepository;

@Service
public class SteelItemService {

    private final SteelItemRepository repository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public SteelItemService(SteelItemRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public Page<SteelItem> search(SteelItemQuery filter, int page, int size, String sortBy, Sort.Direction direction) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (filter.getId() != null && !filter.getId().isBlank()) {
            criteriaList.add(Criteria.where("id").is(filter.getId().trim()));
        }
        if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
            criteriaList.add(Criteria.where("category").regex(filter.getCategory(), "i"));
        }
        if (filter.getProductName() != null && !filter.getProductName().isBlank()) {
            criteriaList.add(Criteria.where("productName").regex(filter.getProductName(), "i"));
        }
        if (filter.getModel() != null && !filter.getModel().isBlank()) {
            criteriaList.add(Criteria.where("model").regex(Pattern.compile(Pattern.quote(filter.getModel().trim()), Pattern.CASE_INSENSITIVE)));
        }
        if (filter.getBrand() != null && !filter.getBrand().isBlank()) {
            criteriaList.add(Criteria.where("brand").regex(filter.getBrand(), "i"));
        }
        if (filter.getMaterial() != null && !filter.getMaterial().isBlank()) {
            criteriaList.add(Criteria.where("material").regex(filter.getMaterial(), "i"));
        }
        if (filter.getOrigin() != null && !filter.getOrigin().isBlank()) {
            criteriaList.add(Criteria.where("origin").regex(filter.getOrigin(), "i"));
        }
        if (filter.getSpec1() != null && !filter.getSpec1().isBlank()) {
            criteriaList.add(Criteria.where("spec1").regex(filter.getSpec1(), "i"));
        }
        if (filter.getStandard() != null && !filter.getStandard().isBlank()) {
            criteriaList.add(Criteria.where("standard").regex(filter.getStandard(), "i"));
        }
                if (filter.getSpec4() != null && !filter.getSpec4().isBlank()) {
                    criteriaList.add(Criteria.where("spec4").regex(filter.getSpec4(), "i"));
                }
        if (filter.getProvince() != null && !filter.getProvince().isBlank()) {
            criteriaList.add(Criteria.where("province").regex(filter.getProvince(), "i"));
        }
        if (filter.getCity() != null && !filter.getCity().isBlank()) {
            criteriaList.add(Criteria.where("city").regex(filter.getCity(), "i"));
        }
        if (filter.getDistrict() != null && !filter.getDistrict().isBlank()) {
            criteriaList.add(Criteria.where("district").regex(filter.getDistrict(), "i"));
        }
        if (filter.getCalcMode() != null && !filter.getCalcMode().isBlank()) {
            criteriaList.add(Criteria.where("calcMode").regex(filter.getCalcMode(), "i"));
        }
        if (filter.getVisible() != null) {
            criteriaList.add(Criteria.where("visible").is(filter.getVisible()));
        }
        if (filter.getMinPrice1() != null) {
            criteriaList.add(Criteria.where("price1").gte(filter.getMinPrice1()));
        }
        if (filter.getMaxPrice1() != null) {
            criteriaList.add(Criteria.where("price1").lte(filter.getMaxPrice1()));
        }
        if (filter.getMinSupplyPrice() != null) {
            criteriaList.add(Criteria.where("supplyPrice").gte(filter.getMinSupplyPrice()));
        }
        if (filter.getMaxSupplyPrice() != null) {
            criteriaList.add(Criteria.where("supplyPrice").lte(filter.getMaxSupplyPrice()));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        Sort sort = Sort.by(direction, sortBy == null || sortBy.isBlank() ? "updatedAt" : sortBy);
        PageRequest pageable = PageRequest.of(page, size, sort);
        query.with(pageable);

        List<SteelItem> items = mongoTemplate.find(query, SteelItem.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), SteelItem.class);
        return new PageImpl<>(items, pageable, total);
    }

    public SteelItem create(SteelItem item) {
        LocalDateTime now = LocalDateTime.now();
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
        return repository.save(item);
    }

    public SteelItem update(String id, SteelItem payload) {
        SteelItem existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Steel item not found"));
        payload.setId(id);
        payload.setCreatedAt(existing.getCreatedAt());
        payload.setUpdatedAt(LocalDateTime.now());
        return repository.save(payload);
    }

    public Optional<SteelItem> findById(String id) {
        return repository.findById(id);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public int importFromCsv(InputStream inputStream) {
        final int batchSize = 500;
        int total = 0;
        List<SteelItem> batch = new ArrayList<>(batchSize);

        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreEmptyLines()
                     .parse(reader)) {

            Map<String, String> headerToField = new HashMap<>();
            for (String header : parser.getHeaderMap().keySet()) {
                if (header != null && !header.isBlank()) {
                    headerToField.put(header, normalizeHeader(header));
                }
            }

            for (CSVRecord record : parser) {
                SteelItem item = new SteelItem();
                for (Map.Entry<String, String> entry : headerToField.entrySet()) {
                    String raw = record.get(entry.getKey());
                    applyStringToItem(item, entry.getValue(), raw);
                }

                // 跳过整行为空（全部字段为空白/空值）的记录，避免导入空白条目
                if (isEffectivelyEmpty(item)) {
                    continue;
                }

                LocalDateTime now = LocalDateTime.now();
                item.setCreatedAt(now);
                item.setUpdatedAt(now);
                batch.add(item);
                total++;

                if (batch.size() >= batchSize) {
                    repository.saveAll(batch);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                repository.saveAll(batch);
            }
            return total;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "解析 CSV 失败: " + ex.getMessage(), ex);
        }
    }

    public int importFromExcel(InputStream inputStream) {
        final int batchSize = 200; // Excel rows are typically larger; keep batch smaller to control memory
        int total = 0;
        List<SteelItem> batch = new ArrayList<>(batchSize);

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Excel 文件没有可用工作表");
            }

            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Excel 缺少表头行");
            }

            Map<Integer, String> indexToField = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String header = readCellString(headerRow.getCell(i));
                if (header != null && !header.isBlank()) {
                    indexToField.put(i, normalizeHeader(header));
                }
            }

            for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                SteelItem item = new SteelItem();

                for (Map.Entry<Integer, String> entry : indexToField.entrySet()) {
                    int idx = entry.getKey();
                    String field = entry.getValue();
                    Cell cell = row.getCell(idx);
                    applyCellToItem(item, field, cell);
                }

                // 跳过整行为空（全部字段为空白/空值）的记录，避免导入空白条目
                if (isEffectivelyEmpty(item)) {
                    continue;
                }

                LocalDateTime now = LocalDateTime.now();
                item.setCreatedAt(now);
                item.setUpdatedAt(now);
                batch.add(item);
                total++;

                if (batch.size() >= batchSize) {
                    repository.saveAll(batch);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                repository.saveAll(batch);
            }
            return total;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "解析 Excel 失败: " + ex.getMessage(), ex);
        }
    }

    private static String normalizeHeader(String header) {
        String h = header.trim();

        return switch (h) {
            case "id", "ID" -> "id";
            case "类别", "category", "类型", "品类" -> "category";
            case "品名", "productName" -> "productName";
            case "型号", "model" -> "model";
            case "每米重量", "weightPerMeter" -> "weightPerMeter";
            case "长度mm", "lengthMm" -> "lengthMm";
            case "规格1", "spec1" -> "spec1";
            case "规格2", "spec2" -> "spec2";
            case "规格3", "spec3" -> "spec3";
            case "规格4", "spec4" -> "spec4";
            case "规格5", "spec5" -> "spec5";
            case "单位", "unit" -> "unit";
            case "材质", "material" -> "material";
            case "标准", "standard", "执行标准" -> "standard";
            case "品牌", "brand", "品牌/厂家" -> "brand";
            case "产地", "origin" -> "origin";
            case "提货地/省", "省", "省份" -> "province";
            case "提货地/市", "市", "城市" -> "city";
            case "提货地/区", "区", "区域" -> "district";
            case "价格1", "price1", "默认价格/元/吨", "默认价格" -> "price1";
            case "价格2", "price2", "二等价格/元/吨", "二等价格" -> "price2";
            case "价格3", "price3", "三等价格/元/吨", "三等价格" -> "price3";
            case "价格4", "price4", "四等价格/元/吨", "四等价格" -> "price4";
            case "价格5", "price5", "五等价格/元/吨", "五等价格" -> "price5";
            case "计算方式", "calcMode", "过磅/理计" -> "calcMode";
            case "库存", "inventory" -> "inventory";
            case "预测变化", "forecastChange" -> "forecastChange";
            case "联系人", "contact", "供应商/联系方式", "联系方式" -> "contact";
            case "供货价", "supplyPrice", "供货价/吨" -> "supplyPrice";
            case "差价", "diffPrice", "差价/元" -> "diffPrice";
            case "备注", "remark" -> "remark";
            case "是否显示", "visible" -> "visible";
            default -> h; 
        };
    }

    private static String readCellString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> new BigDecimal(String.valueOf(cell.getNumericCellValue())).stripTrailingZeros().toPlainString();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> null;
        };
    }

    private static BigDecimal readCellBigDecimal(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING -> {
                String s = cell.getStringCellValue();
                if (s == null || s.isBlank()) yield null;
                try { yield new BigDecimal(s.trim()); } catch (Exception e) { yield null; }
            }
            default -> null;
        };
    }

    private static Integer readCellInteger(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> (int) Math.round(cell.getNumericCellValue());
            case STRING -> {
                String s = cell.getStringCellValue();
                if (s == null || s.isBlank()) yield null;
                try { yield Integer.parseInt(s.trim()); } catch (Exception e) { yield null; }
            }
            default -> null;
        };
    }

    private static Boolean readCellBoolean(Cell cell) {
        String text = readCellString(cell);
        if (text == null) return null;
        String normalized = text.trim().toLowerCase();
        return switch (normalized) {
            case "true", "1", "yes", "y", "是", "显示" -> true;
            case "false", "0", "no", "n", "否", "隐藏" -> false;
            default -> null;
        };
    }

    private static BigDecimal parseBigDecimal(String s) {
        if (s == null || s.isBlank()) return null;
        try { return new BigDecimal(s.trim()); } catch (Exception e) { return null; }
    }

    private static Integer parseInteger(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return null; }
    }

    private static Boolean parseBoolean(String s) {
        if (s == null) return null;
        String normalized = s.trim().toLowerCase();
        return switch (normalized) {
            case "true", "1", "yes", "y", "是", "显示" -> true;
            case "false", "0", "no", "n", "否", "隐藏" -> false;
            default -> null;
        };
    }

    private static void applyCellToItem(SteelItem item, String field, Cell cell) {
        switch (field) {
            case "category" -> item.setCategory(trimToNull(readCellString(cell)));
            case "productName" -> item.setProductName(trimToNull(readCellString(cell)));
            case "model" -> item.setModel(trimToNull(readCellString(cell)));
            case "weightPerMeter" -> item.setWeightPerMeter(readCellBigDecimal(cell));
            case "lengthMm" -> item.setLengthMm(readCellInteger(cell));
            case "spec1" -> item.setSpec1(trimToNull(readCellString(cell)));
            case "spec2" -> item.setSpec2(trimToNull(readCellString(cell)));
            case "spec3" -> item.setSpec3(trimToNull(readCellString(cell)));
            case "spec4" -> item.setSpec4(trimToNull(readCellString(cell)));
            case "spec5" -> item.setSpec5(trimToNull(readCellString(cell)));
            case "unit" -> item.setUnit(trimToNull(readCellString(cell)));
            case "material" -> item.setMaterial(trimToNull(readCellString(cell)));
            case "standard" -> item.setStandard(trimToNull(readCellString(cell)));
            case "brand" -> item.setBrand(trimToNull(readCellString(cell)));
            case "origin" -> item.setOrigin(trimToNull(readCellString(cell)));
            case "province" -> item.setProvince(trimToNull(readCellString(cell)));
            case "city" -> item.setCity(trimToNull(readCellString(cell)));
            case "district" -> item.setDistrict(trimToNull(readCellString(cell)));
            case "price1" -> item.setPrice1(readCellBigDecimal(cell));
            case "price2" -> item.setPrice2(readCellBigDecimal(cell));
            case "price3" -> item.setPrice3(readCellBigDecimal(cell));
            case "price4" -> item.setPrice4(readCellBigDecimal(cell));
            case "price5" -> item.setPrice5(readCellBigDecimal(cell));
            case "calcMode" -> item.setCalcMode(trimToNull(readCellString(cell)));
            case "inventory" -> item.setInventory(readCellInteger(cell));
            case "forecastChange" -> item.setForecastChange(readCellBigDecimal(cell));
            case "contact" -> item.setContact(trimToNull(readCellString(cell)));
            case "supplyPrice" -> item.setSupplyPrice(readCellBigDecimal(cell));
            case "diffPrice" -> item.setDiffPrice(readCellBigDecimal(cell));
            case "remark" -> item.setRemark(trimToNull(readCellString(cell)));
            case "visible" -> item.setVisible(readCellBoolean(cell));
            default -> {
                // ignore unknown columns
            }
        }
    }

    private static void applyStringToItem(SteelItem item, String field, String raw) {
        if (field == null) return;
        switch (field) {
            case "category" -> item.setCategory(trimToNull(raw));
            case "productName" -> item.setProductName(trimToNull(raw));
            case "model" -> item.setModel(trimToNull(raw));
            case "weightPerMeter" -> item.setWeightPerMeter(parseBigDecimal(raw));
            case "lengthMm" -> item.setLengthMm(parseInteger(raw));
            case "spec1" -> item.setSpec1(trimToNull(raw));
            case "spec2" -> item.setSpec2(trimToNull(raw));
            case "spec3" -> item.setSpec3(trimToNull(raw));
            case "spec4" -> item.setSpec4(trimToNull(raw));
            case "spec5" -> item.setSpec5(trimToNull(raw));
            case "unit" -> item.setUnit(trimToNull(raw));
            case "material" -> item.setMaterial(trimToNull(raw));
            case "standard" -> item.setStandard(trimToNull(raw));
            case "brand" -> item.setBrand(trimToNull(raw));
            case "origin" -> item.setOrigin(trimToNull(raw));
            case "province" -> item.setProvince(trimToNull(raw));
            case "city" -> item.setCity(trimToNull(raw));
            case "district" -> item.setDistrict(trimToNull(raw));
            case "price1" -> item.setPrice1(parseBigDecimal(raw));
            case "price2" -> item.setPrice2(parseBigDecimal(raw));
            case "price3" -> item.setPrice3(parseBigDecimal(raw));
            case "price4" -> item.setPrice4(parseBigDecimal(raw));
            case "price5" -> item.setPrice5(parseBigDecimal(raw));
            case "calcMode" -> item.setCalcMode(trimToNull(raw));
            case "inventory" -> item.setInventory(parseInteger(raw));
            case "forecastChange" -> item.setForecastChange(parseBigDecimal(raw));
            case "contact" -> item.setContact(trimToNull(raw));
            case "supplyPrice" -> item.setSupplyPrice(parseBigDecimal(raw));
            case "diffPrice" -> item.setDiffPrice(parseBigDecimal(raw));
            case "remark" -> item.setRemark(trimToNull(raw));
            case "visible" -> item.setVisible(parseBoolean(raw));
            default -> {
                // ignore unknown columns
            }
        }
    }

    // 将空白字符串统一为 null
    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // 判断该条目是否有效（至少有一个关键字段有值），用于过滤空行
    private static boolean isEffectivelyEmpty(SteelItem item) {
        boolean hasString =
                notBlank(item.getCategory()) ||
                notBlank(item.getProductName()) ||
                notBlank(item.getModel()) ||
                notBlank(item.getSpec1()) ||
                notBlank(item.getSpec2()) ||
                notBlank(item.getSpec3()) ||
                notBlank(item.getSpec4()) ||
                notBlank(item.getSpec5()) ||
                notBlank(item.getUnit()) ||
                notBlank(item.getMaterial()) ||
                notBlank(item.getStandard()) ||
                notBlank(item.getBrand()) ||
                notBlank(item.getOrigin()) ||
                notBlank(item.getProvince()) ||
                notBlank(item.getCity()) ||
                notBlank(item.getDistrict()) ||
                notBlank(item.getCalcMode()) ||
                notBlank(item.getContact()) ||
                notBlank(item.getRemark());

        boolean hasNumber =
                item.getWeightPerMeter() != null ||
                item.getLengthMm() != null ||
                item.getPrice1() != null ||
                item.getPrice2() != null ||
                item.getPrice3() != null ||
                item.getPrice4() != null ||
                item.getPrice5() != null ||
                item.getInventory() != null ||
                item.getForecastChange() != null ||
                item.getSupplyPrice() != null ||
                item.getDiffPrice() != null;

        boolean hasVisible = item.getVisible() != null; // 单独设置可见性不算有效数据，但参与判断

        // 如果所有字段都为空且没有任何数值，视为空行
        return !(hasString || hasNumber || hasVisible);
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
