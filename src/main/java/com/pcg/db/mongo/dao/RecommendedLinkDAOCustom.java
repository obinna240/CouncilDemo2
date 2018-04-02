package com.pcg.db.mongo.dao;

import java.util.List;

import com.pcg.db.mongo.model.RecommendedLink;

public interface RecommendedLinkDAOCustom extends CustomDAO<RecommendedLink, Long> {
	List<RecommendedLink> findByKeyword(final String keyword);
	List<RecommendedLink> findByKeywords(final String[] keywords);
	RecommendedLink findById(String id);
}
