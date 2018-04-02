package com.pcg.db.mongo.dao;

import java.util.List;

import com.pcg.db.mongo.model.Category;
import com.pcg.db.mongo.model.CategoryType;


public interface CategoryDAOCustom extends CustomDAO<Category, Long> {

	Category findOne(Long id);
	
	List<Category> findAllChildren(Category rootCategory, boolean leafNodesOnly);

	List<Category> findAssignableCategories(CategoryType catType);

	//List<Category> getServiceLandingCategories();
	//List<Category> getProductLandingCategories();
	
	List<Category> getChildCategories(Category category);
	List<Category> getChildCategoriesLeafOnly(Category category);
	List<Category> getChildCategories(Category category, boolean keepOrder);
	
	List<Category> findAllRootCategories();
	
	//Category findByName(String name);
	Category findByName(Category parentCategory, String name);
}
