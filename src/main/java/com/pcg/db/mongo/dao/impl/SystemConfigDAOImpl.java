package com.pcg.db.mongo.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import com.pcg.db.mongo.dao.SystemConfigDAOCustom;
import com.pcg.db.mongo.model.SystemConfig;
import com.sa.assist.config.SASpringContext;
import com.sa.assist.utils.CacheHandler;
import com.sa.assist.utils.CacheHandler.CacheName;

/**
 * Maintain the sequence collection, which tracks the next id value for a collection
 */
public class SystemConfigDAOImpl extends CustomDAOImpl<SystemConfig, Long> implements SystemConfigDAOCustom {

	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;
	private static Log m_log = LogFactory.getLog(SystemConfig.class);

	public SystemConfigDAOImpl() {
		super(SystemConfig.class);
	}

	@Override
	public SystemConfig getDefaultSystemConfig() {

		Query query = null;
		try {
			String context = SASpringContext.getAppContext();

			//First look in an in-memory cache
			String configId = context + "SystemInfo";
			SystemConfig config = (SystemConfig) CacheHandler.getCachedVal(CacheName.STATICINFO, configId);

			if (config == null) {
				query = new Query(where("_id").is(context));
				config = mongoOps.findOne(query, SystemConfig.class);
				CacheHandler.cacheVal(CacheName.STATICINFO, configId, config);
			}

			return config;
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}

		return null;
	}

	@Override
	public SystemConfig getWritableDefaultSystemConfig() {

		Query query = null;
		try {
			String context = SASpringContext.getAppContext();
			query = new Query(where("_id").is(context));
			SystemConfig config = mongoOps.findOne(query, SystemConfig.class);
			return config;
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}

		return null;
	}

	@Override
	public SystemConfig getSystemConfigForContext(String context) {
		Query query = null;
		try {
			
			query = new Query(where("_id").is(context));
			SystemConfig config = mongoOps.findOne(query, SystemConfig.class);
			
			return config;
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}

		return null;
	}
}
