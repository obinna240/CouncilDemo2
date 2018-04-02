package com.pcg.db.mongo.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.pcg.db.mongo.dao.RecommendedLinkDAOCustom;
import com.pcg.db.mongo.model.RecommendedLink;

public class RecommendedLinkDAOImpl extends
		CustomDAOImpl<RecommendedLink, Long> implements
		RecommendedLinkDAOCustom {
	
private static Log m_log = LogFactory.getLog(RecommendedLinkDAOImpl.class);
	
	@Autowired	@Qualifier("mongoTemplate") MongoOperations mongoOps;
	
	public RecommendedLinkDAOImpl() {
        super(RecommendedLink.class);
    }


	@Override
	public List<RecommendedLink> findByKeyword(String keywords) {
		
		List<RecommendedLink> resultList = new ArrayList<RecommendedLink>();
		
		if (StringUtils.isNotBlank(keywords)){
			
			String[] words = keywords.split(" ");
			
			// elog 26043 must contain all words
			// i.e AND search not OR
			List<Criteria> criterias = new ArrayList<Criteria>();
			for (String word : words) {
				//criterias.add(Criteria.where("keyword").regex(word, "i"));
				String regex = ".*\\b" +  word + "\\b.*";
				criterias.add(Criteria.where("keyword").regex(regex, "i")); 
			}
			
			Query query  = new Query(new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()])));
		
			resultList = mongoOps.find(query, RecommendedLink.class);
			
//			StringBuffer regex = new StringBuffer();
//			for (String word : words) {
//				
//				if (StringUtils.isBlank(word)){
//					continue;
//				}
//				
//				word = StringUtils.trim(word);
//				
//				if (regex.length() > 0){
//					regex.append("|");
//				}
//				
//				regex.append("\\b");
//				regex.append(word);
//				regex.append("\\b");
//			}
//			
//			//Use a case insensitive regex to do word match on the keyword field
//			resultList = mongoOps.find(new Query(where("keyword").regex(regex.toString(), "i")), RecommendedLink.class);

		}

		
		return resultList;
	}
	
	@Override
	public List<RecommendedLink> findByKeywords(String[] keywords) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public RecommendedLink findById(String id) {
		return mongoOps.findById(id, RecommendedLink.class);
	}
}
