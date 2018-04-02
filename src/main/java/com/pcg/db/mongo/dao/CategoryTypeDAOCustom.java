package com.pcg.db.mongo.dao;

import java.util.List;

import com.pcg.db.mongo.model.CategoryType;

public interface CategoryTypeDAOCustom extends CustomDAO<CategoryType, String> {
	public CategoryType findByName(String name);
	public List<CategoryType> findApplicableToCMS();
	
}
