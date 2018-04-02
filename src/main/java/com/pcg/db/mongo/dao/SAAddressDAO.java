package com.pcg.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.pcg.db.mongo.model.SAAddress;

public interface SAAddressDAO extends CrudRepository<SAAddress, Long>, SAAddressDAOCustom {
}
