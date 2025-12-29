package com.example.demo.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.model.SteelItem;
import com.example.demo.repository.SteelItemRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(SteelItemRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            SteelItem item1 = createItem("型材", "工字钢", "10", new BigDecimal("50.62"), 6000,
                    "8-10", null, null, null, null, "吨", "普通", "百信", "百信", new BigDecimal("3000"));
            SteelItem item2 = createItem("型材", "工字钢", "12", new BigDecimal("75.78"), 6000,
                    "8-10", null, null, null, null, "吨", "普通", "百信", "百信", new BigDecimal("3300"));
            SteelItem item3 = createItem("型材", "工字钢", "14", new BigDecimal("112-225"), 9000,
                    "18-20", null, null, null, null, "吨", "普通", "百信", "百信", new BigDecimal("3280"));
            SteelItem item4 = createItem("型材", "工字钢", "18", new BigDecimal("138-155"), 9000,
                    "8-10", null, null, null, null, "吨", "普通", "百信", "百信", new BigDecimal("3280"));
            SteelItem item5 = createItem("型材", "槽钢", "6.3", new BigDecimal("23-24"), 6000,
                    "8-10", null, null, null, null, "吨", "普通", "百信", "百信", new BigDecimal("3340"));

            repository.saveAll(List.of(item1, item2, item3, item4, item5));
        };
    }

    private SteelItem createItem(String category, String productName, String model, BigDecimal weight, Integer length,
                                 String spec1, String spec2, String spec3, String spec4, String spec5,
                                 String unit, String material, String brand, String origin, BigDecimal price1) {
        SteelItem item = new SteelItem();
        item.setCategory(category);
        item.setProductName(productName);
        item.setModel(model);
        item.setWeightPerMeter(weight);
        item.setLengthMm(length);
        item.setSpec1(spec1);
        item.setSpec2(spec2);
        item.setSpec3(spec3);
        item.setSpec4(spec4);
        item.setSpec5(spec5);
        item.setUnit(unit);
        item.setMaterial(material);
        item.setBrand(brand);
        item.setOrigin(origin);
        item.setProvince("天津");
        item.setCity("天津");
        item.setDistrict("天津");
        item.setStandard("GB/T 6728-2017");
        item.setPrice1(price1);
        item.setCalcMode("理计");
        item.setInventory(0);
        item.setSupplyPrice(price1);
        item.setDiffPrice(BigDecimal.ZERO);
        item.setRemark("过磅");
        item.setVisible(Boolean.TRUE);
                LocalDateTime now = LocalDateTime.now();
                item.setCreatedAt(now);
                item.setUpdatedAt(now);
        return item;
    }
}
