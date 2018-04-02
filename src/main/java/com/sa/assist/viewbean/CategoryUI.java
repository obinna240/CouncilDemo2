package com.sa.assist.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pcg.db.mongo.model.Category;
import com.sa.assist.service.MongoUIDaoService;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Wrapper class for view purposes
 */

public class CategoryUI extends Category {

	//private List<Category> categoryAliases = new ArrayList<Category>();
	private List<Category> relatedCategories = new ArrayList<Category>();
	private List<CategoryUI> childCategoryUIs = new ArrayList<CategoryUI>();;
	private String path;
	private String searchLinkUrl;
//	private List<Context> contexts = new ArrayList<Context>();;
	
	private static Log m_log = LogFactory.getLog(CategoryUI.class);

	public CategoryUI(Category c, MongoUIDaoService mongoUIDaoService) {
		try {
			ConvertUtils.register(new DateConverter(null), Date.class);
			BeanUtils.copyProperties(this, c);
					
//			List<Long> aliasIds = this.getAliases();
//			if (aliasIds != null) {
//				for (Long alias : aliasIds) {
//					Category catAlias = mongoUIDaoService.categoryDAO.findOne(alias);
//					categoryAliases.add(catAlias);
//				}
//			}
			
			List<Long> relatedsIds = this.getRelatedTerms();
			if (relatedsIds != null) {
				for (Long related : relatedsIds) {
					Category catRelated = mongoUIDaoService.categoryDAO.findOne(related);
					relatedCategories.add(catRelated);
				}
			}
			
			
			List<Category> childCategories = mongoUIDaoService.categoryDAO.getChildCategories(this);
		    for (Category cat : childCategories) {
		    	CategoryUI categoryUI = new CategoryUI(cat, mongoUIDaoService);
		    	childCategoryUIs.add(categoryUI);
		    }
		    
		    // path
		    path = this.getName();
			
			Long parentCatId = this.getCategoryParentId();
			while (parentCatId != null) {
				Category parent = mongoUIDaoService.categoryDAO.findOne(parentCatId);
				if (parent != null) {
					parentCatId = parent.getCategoryParentId();
					path = parent.getName() + " -> " + path;
				} else {
					parentCatId = null;
				}
			}
			
//			// contexts
//			List<Long> contextIds = this.getContextIds();
//			if (contextIds != null) {
//				for (Long contextId : contextIds) {
//					Context context = mongoUIDaoService.contextDAO.findOne(contextId);
//					if (context != null) {
//						contexts.add(context);
//					}
//				}
//			}
		
		} catch (Exception e) {
			m_log.error("Error initialising view bean ", e);
		}
	}
	
//	public List<Category> getCategoryAliases() {
//		return categoryAliases;
//	}
	
	public List<Category> getRelatedCategories() {
		return relatedCategories;
	}
	
	public List<CategoryUI> getChildCategories() {
	    return childCategoryUIs;
	}
	
	
	public String getPath() {
		return path;
	}

	public String getSearchLinkUrl() {
		return searchLinkUrl;
	}

	public void setSearchLinkUrl(String searchLinkUrl) {
		this.searchLinkUrl = searchLinkUrl;
	}

//	public List<Context> getContexts() {
//		return contexts;
//	}
//
//	public void setContexts(List<Context> contexts) {
//		this.contexts = contexts;
//	}

	public String toJson() {
        return new JSONSerializer().exclude("*.class").deepSerialize(this);
    }

	public static CategoryUI fromJsonToCategoryUI(String json) {
        return new JSONDeserializer<CategoryUI>().use(null, CategoryUI.class).deserialize(json);
    }

	public static String toJsonArrayCategoryUIs(Collection<CategoryUI> collection) {
        return new JSONSerializer().exclude("*.class").deepSerialize(collection);
    }

	public static Collection<CategoryUI> fromJsonArrayToCategoryUIs(String json) {
        return new JSONDeserializer<List<CategoryUI>>().use(null, ArrayList.class).use("values", CategoryUI.class).deserialize(json);
    }
}
