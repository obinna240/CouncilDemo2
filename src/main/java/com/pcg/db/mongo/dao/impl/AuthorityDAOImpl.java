package com.pcg.db.mongo.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import com.pcg.db.mongo.dao.AuthorityDAOCustom;
import com.pcg.db.mongo.model.Authority;

public class AuthorityDAOImpl extends CustomDAOImpl<Authority, Long> implements AuthorityDAOCustom {
	
	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;
	
	private static Log m_log = LogFactory.getLog(AuthorityDAOImpl.class);
	
	public AuthorityDAOImpl() {
        super(Authority.class);
    }
	
	@Override
	public List<Authority> getEnabledAuthorities() {
		
		Query query = null;
		try {
			query = new Query(where("enabled").is(true));
			query.with(new Sort(Sort.Direction.ASC, "description"));		
			//query.sort().on("description", Order.ASCENDING);
			List<Authority> list = mongoOps.find(query, Authority.class);
			return list;
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + query.toString(), e);
			return null;
		}
	}
	
	@Override
	public List<Authority> getDisabledAuthorities() {
		
		Query query = null;
		try {
			query = new Query(where("enabled").is(false));
			query.with(new Sort(Sort.Direction.ASC, "description"));		
			//query.sort().on("description", Order.ASCENDING);
			List<Authority> list = mongoOps.find(query, Authority.class);
			return list;
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + query.toString(), e);
			return null;
		}
	}

	@Override
	public void createAuthorities(Authority auth) {
		if (!mongoOps.collectionExists(Authority.class)) {
			mongoOps.createCollection(Authority.class);
			}
		
		mongoOps.insert(auth, "authority");
		
	}
}
