package com.pcg.db.mongo.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;

import com.pcg.db.mongo.dao.ScheduleLogDAOCustom;
import com.pcg.db.mongo.model.ScheduleLog;

/**
 * DAO for managing scheduled tasks in Assist  
 */
public class ScheduleLogDAOImpl extends CustomDAOImpl<ScheduleLog, Long> implements ScheduleLogDAOCustom {
	
	@Autowired @Qualifier("mongoTemplate") MongoOperations mongoOps;
	private static Log m_log = LogFactory.getLog(ScheduleLogDAOImpl.class);
	
	public ScheduleLogDAOImpl() {
        super(ScheduleLog.class);
    }
	
	
}
