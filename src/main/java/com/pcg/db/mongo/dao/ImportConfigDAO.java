package com.pcg.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.pcg.db.mongo.model.ImportConfig;

public interface ImportConfigDAO extends CrudRepository<ImportConfig, String>{
}
