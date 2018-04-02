package com.pcg.db.mongo.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import com.pcg.db.mongo.dao.CategoryDAOCustom;
import com.pcg.db.mongo.dao.CategoryTypeDAO;
import com.pcg.db.mongo.model.Category;
import com.pcg.db.mongo.model.CategoryType;
import com.sa.assist.utils.CacheHandler;
import com.sa.assist.utils.CacheHandler.CacheName;


public class CategoryDAOImpl extends CustomDAOImpl<Category, Long> implements CategoryDAOCustom {
	
	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;
	
	@Autowired private CategoryTypeDAO categoryTypeDAO;
	
	
	private static Log m_log = LogFactory.getLog(CategoryDAOImpl.class);
	
	public CategoryDAOImpl() {
        super(Category.class);
    }
	
	@Override
	public Category findOne(Long id) {
		try {
			//First look in an in-memory cache
			String categoryKey = "category" + id.toString();
			Category cat = (Category) CacheHandler.getCachedVal(CacheName.CATEGORIES, categoryKey);
			if (cat == null) {
				cat = mongoOps.findById(id, Category.class);
				CacheHandler.cacheVal(CacheName.CATEGORIES, categoryKey, cat);
			}
			
			return cat;
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : findOne", e);
		}
		return null;			
	}

//	@Override
//	public List<Category> findAllChildren(CategoryType catType, boolean leafNodesOnly) {
//		Category categoryRoot = findByName(catType.getName());
//		return findAllChildren(categoryRoot, leafNodesOnly);
//	}

	@Override
	public List<Category> findAllRootCategories() {
		Query query = null;
		try {
			query = new Query(where("categoryRootId").is(null));
			query.with(new Sort(Sort.Direction.ASC, "name"));		
			//query.sort().on("name", Order.ASCENDING);
			return mongoOps.find(query, Category.class);
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
		return null;			
	}
	
	@Override
	public List<Category> findAllChildren(Category rootCategory, boolean leafNodesOnly) {
		
		if (rootCategory == null) {
			return new ArrayList<Category>();
		}
		
		Query query = null;
		try {
			query = new Query(where("categoryRootId").is(rootCategory.getId()));
			query.with(new Sort(Sort.Direction.ASC, "name"));	
			//query.sort().on("name", Order.ASCENDING);
			List<Category> childList = mongoOps.find(query, Category.class);
				
			if (leafNodesOnly){
				//Return only categories which aren't a parent of any other category
				List<Category> leafCategoryList = new ArrayList<Category>();
				
				Query parentQuery = new Query(where("categoryRootId").is(rootCategory.getId()).and("categoryParentId").exists(true));
				query.with(new Sort(Sort.Direction.ASC, "name"));	
				//query.sort().on("name", Order.ASCENDING);
				List<Category> parentCats = mongoOps.find(parentQuery, Category.class);

				HashSet<Long> parentIDs = new HashSet<Long>();
				for (Category cat : parentCats) {
					parentIDs.add(cat.getCategoryParentId());
				}
				
				for (Category cat : childList) {
					if (!parentIDs.contains(cat.getId())){
						leafCategoryList.add(cat);
					}
				}
				
				return leafCategoryList;
			}
			else {
				return childList;
			}
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
		return null;
	}

	@Override
	public List<Category> findAssignableCategories(CategoryType catType) {
		Category rootCat = this.findOne(catType.getCategoryId());
		return findAllChildren(rootCat, true);
	}

//	@Override
//	public List<Category> getServiceLandingCategories() {
//		//Return top-level service categories
//		CategoryType categoryType = categoryTypeDAO.findOne("SERVICE");
//		Category rootCat =  this.findOne(categoryType.getCategoryId());
//		return getChildCategories(rootCat);
//	}
//
//	@Override
//	public List<Category> getProductLandingCategories() {
//		//Return top-level product categories
//		CategoryType categoryType = categoryTypeDAO.findOne("PRODUCT");
//		Category rootCat = this.findOne(categoryType.getCategoryId());
//		return getChildCategories(rootCat);
//
//	}
	

	@Override
	public List<Category> getChildCategoriesLeafOnly(Category category) {
		
		Query query = null;
		
		try {
			query = new Query(where("categoryParentId").is(category.getId())); 
			query.with(new Sort(Sort.Direction.ASC, "name"));	
			//query.sort().on("name", Order.ASCENDING);
			List<Category> childList = mongoOps.find(query, Category.class);
			
			//Return only categories which aren't a parent of any other category
			List<Category> leafCategoryList = new ArrayList<Category>();
			
			
			for (Category cat : childList) {
				query = new Query(where("categoryParentId").is(cat.getId())); 
				List<Category> subList = mongoOps.find(query, Category.class);
				if (subList.size() == 0){
					leafCategoryList.add(cat);
				}
			}
			
			return leafCategoryList;
			
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
		
		return new ArrayList<Category>();
	}
	
	@Override
	public List<Category> getChildCategories(Category category) {
		
		Query query = null;
		
		try {
			query = new Query(where("categoryParentId").is(category.getId())); 
			query.with(new Sort(Sort.Direction.ASC, "name"));	
			//query.sort().on("name", Order.ASCENDING);
			return mongoOps.find(query, Category.class);
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
		
		return new ArrayList<Category>();
	}
	
	@Override
	public List<Category> getChildCategories(Category category, boolean keepOrder) {
		
		Query query = null;
		
		try {
			query = new Query(where("categoryParentId").is(category.getId())); 
			if (!keepOrder) {
				query.with(new Sort(Sort.Direction.ASC, "name"));	
				//query.sort().on("name", Order.ASCENDING);
			}
			return mongoOps.find(query, Category.class);
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
		
		return new ArrayList<Category>();
	}
	
	

//	@Override
//	public Category findByName(String name) {
//		Query query = null;
//		try {
//			query = new Query(where("name").regex("^" + name + "$", "i")); 
//			return mongoOps.findOne(query, Category.class);
//		}
//		catch (Exception e){
//			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
//		}
//		return null;
//	}

	@Override
	public Category findByName(Category parentCategory, String name) {
		if (name != null){
			
			Query query = null;
			try {
				if (name.indexOf("'") != -1) {
					name = name.replaceAll("'", "\'");
				}
				query = new Query(where("categoryParentId").is(parentCategory.getId()).and("name").is(name)); 
				return mongoOps.findOne(query, Category.class);
			}
			catch (Exception e){
				m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
			}
		}
		return null;
	}
}
