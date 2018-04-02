package com.pcg.search.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pcg.db.mongo.dao.CategoryDAO;
import com.pcg.db.mongo.model.Category;
import com.pcg.search.api.beans.CmsDocument;
import com.sa.assist.controller.api.cms.json.packets.MetaDataPacket;
import com.sa.assist.search.SolrConfig;

@Component
class CmsDocumentIndexer extends BaseIndexer {
	
	private static Log m_log = LogFactory.getLog(CmsDocumentIndexer.class);
	private Iterable<CmsDocument> cmsDocs;
	private Map<String, Long> contextList = new HashMap<String, Long>();
	
	@Autowired private CategoryDAO categoryDAO;
//	@Autowired private ContextDAO contextDAO;
	
	public CmsDocumentIndexer(){
	}
	
	public void init(SolrConfig solrConfig, Iterable<CmsDocument> cmsDocs, boolean bDeleteOthers){
		super.init(solrConfig, bDeleteOthers);
		this.cmsDocs = cmsDocs;
	}
	
	public void run() {
		
//		List<Context> contexts = contextDAO.findAllEnabled();
//		for(Context c : contexts) {
//			contextList.put(c.getName(), c.getId());
//		}
		
		for (CmsDocument cmsDoc : cmsDocs) {
			try {
			
				String cmsDocType = StringUtils.isEmpty(cmsDoc.getDocType()) ? "CMS_Generic" : cmsDoc.getDocType();
				
				m_log.debug("Indexing cmsDoc " + cmsDocType + cmsDoc.getUmbNodeId());
				
				HashMap<String,Object> docData = new HashMap<String,Object>();
	
				docData.put("id", cmsDocType + cmsDoc.getUmbNodeId().toString());
				docData.put("type", cmsDocType);
				docData.put("enabled", "true");
				
				String title = deHTML(cmsDoc.getPageTitle());
				docData.put("title", title);
				
				String description = deHTML(cmsDoc.getPageContent());
				docData.put("description", description);
				
				String pageUrl = cmsDoc.getPageUrl();
				docData.put("pageUrl", pageUrl);
				
//				docData.put("smartsuggest", cmsDoc.getSmartSuggest().toString());
				
//				if (contextList != null && contextList.size() > 0) {
//					Long contextId = contextList.get(cmsDoc.getContext().toLowerCase());
//					if (contextId != null) {
//						docData.put("contextgroup", contextId.toString());
//					}
//				}
				
//				List<Long> catIDs = new ArrayList<Long>();
//				List<Long> conditionCatIDs = new ArrayList<Long>();
				
//				MetaDataPacket metaData = cmsDoc.getMetaData();
				
//				if (metaData!=null) {
//					
//					if (!StringUtils.isEmpty(metaData.getConditions())) {
//						for (String catId : metaData.getConditions().split(",")) {
//							// Add immediate children until tree control developed for CMS
//							addChildren(conditionCatIDs, Long.valueOf(catId));
//							conditionCatIDs.add(Long.valueOf(catId));
//						}
//					}
//					
//					if (!StringUtils.isEmpty(metaData.getNeeds())) {
//						for (String catId : metaData.getNeeds().split(",")) {
//							// Add immediate children until tree control developed for CMS
//							if (!StringUtils.isEmpty(catId)) {
//								addChildren(catIDs, Long.valueOf(catId));
//								catIDs.add(Long.valueOf(catId));
//							}
//						}
//					}				
//					
//					if (!StringUtils.isEmpty(metaData.getProducts())) {
//						for (String catId : metaData.getProducts().split(",")) {
//							// Add immediate children until tree control developed for CMS
//							if (!StringUtils.isEmpty(catId)) {
//								addChildren(catIDs, Long.valueOf(catId));
//								catIDs.add(Long.valueOf(catId));
//							}
//						}
//					}				
//					
//					if (!StringUtils.isEmpty(metaData.getServices())) {
//						for (String catId : metaData.getServices().split(",")) {
//							// Add immediate children until tree control developed for CMS
//							if (!StringUtils.isEmpty(catId)) {
//								addChildren(catIDs, Long.valueOf(catId));
//								catIDs.add(Long.valueOf(catId));
//							}
//						}
//					}
//					
//					if (!StringUtils.isEmpty(metaData.getCategories())) {
//						for (String catId : metaData.getCategories().split(",")) {
//							// Add immediate children until tree control developed for CMS
//							if (!StringUtils.isEmpty(catId)) {
//								addChildren(catIDs, Long.valueOf(catId));
//								catIDs.add(Long.valueOf(catId));
//							}
//						}
//					}
//					
//				}			
//				
//				// now add categories to the doc
//				addCategories("categorytree", catIDs, docData);
//				addCategories("condition", conditionCatIDs, docData);			
				
				addDocToIndex(docData);
			}
			catch(Exception e) {
				m_log.error("Error indexing CMS document " + cmsDoc.getUmbNodeId());
			}
		}
		
		if (bDeleteOthers){
			deleteOldDocuments("+type:CMS_*");
		}
		
		m_log.info("Indexing complete");
	}

	private void addChildren(List<Long> catIDs, Long parentID) {
		
		Category category = categoryDAO.findOne(parentID);
		if (category!=null) {
			List<Category> childCategories = categoryDAO.getChildCategories(category);
			if (childCategories!=null) {
			
				for (Category childCat : childCategories) {
					
					catIDs.add(childCat.getId());
					
				}
			
			}
		}
	}
	
	public void removeCmsContent(CmsDocument cmsDoc) {
		
		String cmsDocType = StringUtils.isEmpty(cmsDoc.getDocType()) ? "CMS_Generic" : cmsDoc.getDocType();
		String solrId = cmsDocType + cmsDoc.getUmbNodeId().toString();
		
		String deleteQuery = "id:" + solrId;
		try {
			m_log.info("Deleting CMS content - " + deleteQuery );
			SolrClient solrClient = solrConfig.getSolrClient();
			solrClient.deleteByQuery(deleteQuery);
			solrClient.commit();
		} catch (Exception e) {
			m_log.error("Failure executing delete query " + deleteQuery, e);
		} 
		
	}
}
