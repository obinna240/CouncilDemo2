package com.pcg.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.pcg.db.mongo.model.Category;

public interface CategoryDAO extends CrudRepository<Category, Long>, CategoryDAOCustom {
}
