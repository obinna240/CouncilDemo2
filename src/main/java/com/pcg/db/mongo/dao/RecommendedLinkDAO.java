package com.pcg.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.pcg.db.mongo.model.RecommendedLink;

public interface RecommendedLinkDAO extends CrudRepository<RecommendedLink, Long>, RecommendedLinkDAOCustom {

}
