package com.pcg.db.mongo.dao;

import com.pcg.db.mongo.model.SearchLogger;


public interface SearchLoggerDAOCustom extends CustomDAO<SearchLogger, Long> {
	public void logSearch(String searchText, String searchType, Long resultCount, Long web, Long user, Long carer, Long broker);

}
