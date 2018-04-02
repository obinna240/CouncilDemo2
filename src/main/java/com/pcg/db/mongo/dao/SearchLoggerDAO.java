package com.pcg.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.pcg.db.mongo.model.SearchLogger;

public interface SearchLoggerDAO extends CrudRepository<SearchLogger, Long>, SearchLoggerDAOCustom {
}
