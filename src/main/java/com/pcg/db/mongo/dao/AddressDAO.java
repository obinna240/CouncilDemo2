package com.pcg.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.pcg.db.mongo.model.Address;

public interface AddressDAO extends CrudRepository<Address, Long>,AddressDAOCustom{
}
