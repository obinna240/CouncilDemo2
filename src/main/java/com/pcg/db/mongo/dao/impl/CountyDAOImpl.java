package com.pcg.db.mongo.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;

import com.pcg.db.mongo.dao.CountyDAOCustom;
import com.pcg.db.mongo.model.County;


public class CountyDAOImpl extends CustomDAOImpl<County, Long> implements CountyDAOCustom {
	
	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;
	
	
	private static Log m_log = LogFactory.getLog(CountyDAOImpl.class);
	
	public CountyDAOImpl() {
        super(County.class);
    }
}
