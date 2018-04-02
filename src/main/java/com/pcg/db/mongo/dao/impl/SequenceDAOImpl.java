package com.pcg.db.mongo.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.pcg.db.mongo.dao.SequenceDAO;

/**
 * Maintain the sequence collection, which tracks the next id value for a collection
 */
public class SequenceDAOImpl implements SequenceDAO {

	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;
	private static Log m_log = LogFactory.getLog(SequenceDAOImpl.class);
	
	/**
	 * This will return the next available id for the class in question, and increment the sequence counter ready for the next call
	 */
	@Override
	public Long getNextId(Class objClass) {

		//Collection id = class name with lowercased first letter 
		String className = StringUtils.substringAfterLast(objClass.getName(), ".");
		String startChar = className.substring(0,1);
		className = StringUtils.replaceOnce(className, startChar, startChar.toLowerCase());

		try {
		
			BasicDBObject query = new BasicDBObject().append("_id", className);
			BasicDBObject update = new BasicDBObject().append("$inc", new BasicDBObject().append("sequenceId", 1));
			
			//This is an atomic operation, so there will be no conflicts when the id number increments
			DBObject result = mongoOps.getCollection("sequence").findAndModify(query, update);
			
			return (Long)result.get("sequenceId");
		}
		catch (Exception e){
			m_log.error("Error executing sequence update: ", e);
		}
	
		try {
			BasicDBObject update = new BasicDBObject().append("_id", className).append("sequenceId", new Long(2L));
		
			mongoOps.getCollection("sequence").insert(update, WriteConcern.SAFE);
			return 1L;
		}
		catch (Exception e){
			m_log.error("Error executing sequence update: ", e);
		}
		return null;
	}

	
	@Override
	/**
	 * Resets sequence Id to 1 
	 */
	public void resetId(Class objClass) {

		//Collection id = class name with lowercased first letter 
		String className = StringUtils.substringAfterLast(objClass.getName(), ".");
		String startChar = className.substring(0,1);
		className = StringUtils.replaceOnce(className, startChar, startChar.toLowerCase());

		try {
			BasicDBObject query = new BasicDBObject().append("_id", className);
			BasicDBObject update = new BasicDBObject().append("_id", className).append("sequenceId", new Long(1L));
			mongoOps.getCollection("sequence").update(query, update);
		}
		catch (Exception e){
			m_log.error("Error executing sequence update: ", e);
		}
	}
}
