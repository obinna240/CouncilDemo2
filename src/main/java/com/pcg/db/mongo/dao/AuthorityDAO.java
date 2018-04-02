package com.pcg.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.pcg.db.mongo.model.Authority;

public interface AuthorityDAO extends CrudRepository<Authority, Long>, AuthorityDAOCustom {
	public Authority findBySnacCode(String snacCode);
}
