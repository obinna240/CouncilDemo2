package com.pcg.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.pcg.db.mongo.model.County;

public interface CountyDAO extends CrudRepository<County, Long>, CountyDAOCustom {
}
