package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.SteelItem;

public interface SteelItemRepository extends MongoRepository<SteelItem, String> {
}
