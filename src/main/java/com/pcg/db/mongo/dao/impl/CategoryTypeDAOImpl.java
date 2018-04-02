package com.pcg.db.mongo.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import com.pcg.db.mongo.dao.CategoryTypeDAOCustom;
import com.pcg.db.mongo.model.CategoryType;

public class CategoryTypeDAOImpl extends CustomDAOImpl<CategoryType, String> implements CategoryTypeDAOCustom {
	
	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;
	private static Log m_log = LogFactory.getLog(CategoryTypeDAOImpl.class);
	
	public CategoryTypeDAOImpl() {
        super(CategoryType.class);
    }

	@Override
	public CategoryType findByName(String name) {
		if (name != null){
			
			Query query = null;
			try {
				if (name.indexOf("'") != -1) {
					name = name.replaceAll("'", "\'");
				}
				query = new Query(where("name").is(name)); 
				return mongoOps.findOne(query, CategoryType.class);
			}
			catch (Exception e){
				m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
			}
		}
		
		return null;
		
	}
	
	@Override
	public List<CategoryType> findApplicableToCMS() {
		Query query = null;
		try {
			query = new Query(where("applicableToCMS").is(true)); 
			query.with(new Sort(Sort.Direction.ASC, "_id"));	
			//query.sort().on("_id", Order.ASCENDING);
			return mongoOps.find(query, CategoryType.class);
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
		
		return null;
	}
	
}
