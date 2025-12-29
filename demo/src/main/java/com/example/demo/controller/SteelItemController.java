package com.example.demo.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.model.SteelItem;
import com.example.demo.model.SteelItemQuery;
import com.example.demo.service.SteelItemService;

@RestController
@RequestMapping("/api/steel-items")
@Validated
@CrossOrigin
public class SteelItemController {

    private final SteelItemService service;

    public SteelItemController(SteelItemService service) {
        this.service = service;
    }

    @GetMapping
    public Page<SteelItem> search(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "updatedAt") String sortBy,
            @RequestParam(name = "direction", defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "model", required = false) String model,
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "material", required = false) String material,
            @RequestParam(name = "origin", required = false) String origin,
            @RequestParam(name = "spec1", required = false) String spec1,
            @RequestParam(name = "standard", required = false) String standard,
            @RequestParam(name = "province", required = false) String province,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "district", required = false) String district,
            @RequestParam(name = "spec4", required = false) String spec4,
            @RequestParam(name = "calcMode", required = false) String calcMode,
            @RequestParam(name = "visible", required = false) String visible,
            @RequestParam(name = "minPrice1", required = false) String minPrice1,
            @RequestParam(name = "maxPrice1", required = false) String maxPrice1,
            @RequestParam(name = "minSupplyPrice", required = false) String minSupplyPrice,
            @RequestParam(name = "maxSupplyPrice", required = false) String maxSupplyPrice) {

        SteelItemQuery filter = new SteelItemQuery();
        filter.setId(id);
        filter.setCategory(category);
        filter.setProductName(productName);
        filter.setModel(model);
        filter.setBrand(brand);
        filter.setMaterial(material);
        filter.setOrigin(origin);
        filter.setSpec1(spec1);
        filter.setStandard(standard);
        filter.setProvince(province);
        filter.setCity(city);
        filter.setDistrict(district);
        filter.setSpec4(spec4);
        filter.setCalcMode(calcMode);
        if (visible != null && !visible.isBlank()) {
            filter.setVisible(Boolean.parseBoolean(visible));
        }
        if (minPrice1 != null && !minPrice1.isBlank()) {
            filter.setMinPrice1(new java.math.BigDecimal(minPrice1));
        }
        if (maxPrice1 != null && !maxPrice1.isBlank()) {
            filter.setMaxPrice1(new java.math.BigDecimal(maxPrice1));
        }
        if (minSupplyPrice != null && !minSupplyPrice.isBlank()) {
            filter.setMinSupplyPrice(new java.math.BigDecimal(minSupplyPrice));
        }
        if (maxSupplyPrice != null && !maxSupplyPrice.isBlank()) {
            filter.setMaxSupplyPrice(new java.math.BigDecimal(maxSupplyPrice));
        }

        return service.search(filter, page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public SteelItem getById(@PathVariable String id) {
        return service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Steel item not found"));
    }

    @PostMapping
    public SteelItem create(@RequestBody SteelItem item) {
        return service.create(item);
    }

    @PutMapping("/{id}")
    public SteelItem update(@PathVariable String id, @RequestBody SteelItem item) {
        return service.update(id, item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PostMapping(path = "/upload", consumes = {"multipart/form-data"})
    public UploadResponse uploadExcel(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请上传有效的 Excel 文件");
        }
        try {
            String filename = file.getOriginalFilename();
            boolean isCsv = filename != null && filename.toLowerCase().endsWith(".csv");
            int imported = isCsv
                    ? service.importFromCsv(file.getInputStream())
                    : service.importFromExcel(file.getInputStream());
            return new UploadResponse(imported);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "读取文件失败: " + e.getMessage(), e);
        }
    }

    public static class UploadResponse {
        private int importedCount;

        public UploadResponse(int importedCount) {
            this.importedCount = importedCount;
        }

        public int getImportedCount() {
            return importedCount;
        }

        public void setImportedCount(int importedCount) {
            this.importedCount = importedCount;
        }
    }
}
