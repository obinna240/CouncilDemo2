package com.pcg.db.mongo.dao.impl;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;

import com.pcg.db.mongo.dao.SearchLoggerDAOCustom;
import com.pcg.db.mongo.dao.SequenceDAO;
import com.pcg.db.mongo.model.SearchLogger;

public class SearchLoggerDAOImpl extends CustomDAOImpl<SearchLogger, Long> implements SearchLoggerDAOCustom {
	
	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;
	@Autowired SequenceDAO sequenceDAO;
	
	private static Log m_log = LogFactory.getLog(SearchLoggerDAOImpl.class);

	public SearchLoggerDAOImpl() {
        super(SearchLogger.class);
    }
	
	
	@Override
	public void logSearch(String searchText, String searchType, Long resultCount, Long web, Long user, Long carer, Long broker) {
		Long nextId = sequenceDAO.getNextId(SearchLogger.class);
		
		SearchLogger sl = new SearchLogger();
		sl.setId(nextId);
		sl.setSearchText(searchText);
		sl.setSearchType(searchType);
		sl.setResultCount(resultCount);
		sl.setWeb(web);
		sl.setUser(user);
		sl.setCarer(carer);
		sl.setBroker(broker);
		
		Date now = new GregorianCalendar().getTime();
		sl.setLogDate(now);
		
		mongoOps.save(sl);
		
	}


	
}
