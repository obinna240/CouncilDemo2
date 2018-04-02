package com.pcg.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.pcg.db.mongo.model.SystemConfig;

public interface SystemConfigDAO extends CrudRepository<SystemConfig, Long>, SystemConfigDAOCustom {

}
