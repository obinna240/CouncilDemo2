package com.pcg.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.pcg.db.mongo.model.CategoryType;;

public interface CategoryTypeDAO extends CrudRepository<CategoryType, String>, CategoryTypeDAOCustom {
	
}
