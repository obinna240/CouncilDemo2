package com.pcg.search.index;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.pcg.db.mongo.dao.CategoryDAO;
import com.pcg.db.mongo.dao.SystemConfigDAO;
import com.pcg.db.mongo.model.Address;
import com.pcg.db.mongo.model.Category;
import com.pcg.db.mongo.model.DirectoryInfo;
import com.pcg.db.mongo.model.SystemConfig;
import com.sa.assist.search.SolrConfig;

/**
 * Shared indexing functionality  
 */
public class BaseIndexer implements Runnable {
	
	Thread runner;
	boolean bDeleteOthers;
	SolrConfig solrConfig;
	String indexDateTime;
	
	@Autowired private CategoryDAO categoryDAO;
//	@Autowired private UserDAO userDAO;
	@Autowired private SystemConfigDAO systemConfigDAO;
//	@Autowired private ContextDAO contextyDAO;
	
	protected Map<String, Boolean> directoriesMap;
	
	private static Log m_log = LogFactory.getLog(BaseIndexer.class);
	
	/**
	 * This should be overidden in the subclass to do the actual indexing 
	 */
	@Override
	public void run() {
	}
	
	/**
	 * This should be called on the subclass instance to initiate the indexing thread
	 */
	public void startIndexing(){

		//We flag everything in a particular index run with the same timestamp
		indexDateTime = getSolrDateAndTime(new Date());
		runner = new Thread(this);
		runner.start();
	}
	
	public Thread getThread() {
		return runner;
	}
	
	void init(SolrConfig solrConfig, boolean bDeleteOthers){
		this.bDeleteOthers = bDeleteOthers;
		this.solrConfig = solrConfig;
		SystemConfig config = systemConfigDAO.getDefaultSystemConfig();
//		List<DirectoryInfo> dirs = config.getDirectoryInfos();
//		this.directoriesMap = new HashMap<String, Boolean>();
//		for (DirectoryInfo d:dirs){
//			// Do not check enabled here as might be called form families or adults which will have different 
//			// directory infos enabled 
//			// added new flag removeFromIndex which has default false
//			// TO_DO need a different way to do this
//			// possibly have new entry in system config to say which directory types to include in the index
//			// entry should be a solr properties class so we can add extra fields
//			if (!d.isRemoveFromIndex()){
//				this.directoriesMap.put(d.getType().toUpperCase(), true);
//			}
//		}
	}

	/**
	 * Add latitude & longitude data as a single comma separated field
	 */
	void indexAddressLocation(Map<String, Object> docData, Address address){
		if (address != null) {
			if (address.getLatitude() != null && address.getLongitude() != null){
				docData.put("location", address.getLatitude().toString() + "," + address.getLongitude().toString());
			}
				
			//TODO do we need to index some extra info if the vendor has specified a radius?
			//Double rad = v.getCoveredRadius();
		}
	}
	
	/**
	 * Adds a generic map of name-value pairs to the index as a single document  
	 */
	void addDocToIndex(Map<String, Object> docData){
		
		try {
			SolrClient solrClient = solrConfig.getSolrClient();
			
			docData.put("indextime", indexDateTime);
			
			SolrInputDocument doc = new SolrInputDocument();
			Set<String> keySet = docData.keySet();
			Object fieldVal = null;
			
			for (String fieldName : keySet) {
				
				//Multi-valued items are expected in the keyset with names like 
				//name.1, name.2 etc
				
				if (fieldName.endsWith(".1")){
					fieldName = StringUtils.substringBefore(fieldName, ".");
					
					for (int i = 1; i < 1000; i++){
						String checkFieldName = fieldName + "." + Integer.toString(i);
						fieldVal = docData.get(checkFieldName);
						
						if (fieldVal != null){
							doc.addField(fieldName, fieldVal);
						}
						else {
							break;
						}
					}
				}
				else if (StringUtils.contains(fieldName, ".")){
					//do nothing - field.2, field.3 etc will already have been taken care of above  
				}
				else {
					fieldVal = docData.get(fieldName);
					doc.addField(fieldName, fieldVal);
				}
			}
			
			UpdateResponse response;

			m_log.debug("Sending doc to Solr..");
			response = solrClient.add(doc);
			m_log.debug("Server response = " + response.getStatus());

			softCommit();
			
			//Add in small pause so that we don't overwhelm the server(s)
			Thread.sleep(50);
			
		} catch (Exception e) {
			m_log.error("Failed to add documents to index ", e);
		} 
	}
	
	
	
	/**
	 * Full commit to disk (possibly slow)
	 */
	private void commit() {
		SolrClient solrClient = solrConfig.getSolrClient();
		try {
			solrClient.commit();
		} catch (Exception e) {
			m_log.error("Failed to commit updates to index ", e);
		}
	}
	
	/**
	 * Soft commit - immediately visible from searches, but not necessarily written to disk at the Solr server
	 * Solr server will be configured to auto commit to disk after a time period
	 */
	private void softCommit() {
		SolrClient solrClient = solrConfig.getSolrClient();
		try {
			solrClient.commit(false, true, true);
		} catch (Exception e) {
			m_log.error("Failed to soft commit updates to index ", e);
		}
	}
	
	String getSolrDateAndTime(Date d){
		SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'");
		return sdfOut.format(d); 
	}
	
	String getSolrDateNoTime(Date d){
		SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'");
		return sdfOut.format(d); 
	}
	
	String getSolrDateNoTimeEnd(Date d){
		SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd'T'23:59:59'Z'");
		return sdfOut.format(d); 
	}
	
	/**
	 * Basic HTML tag removal 
	 */
	String deHTML(String html){
		if (html != null) {
			Document doc = Jsoup.parse(html);
			return doc.text();
		
			//String deHTMLdString = html.replaceAll("\\<.*?>","");
			//deHTMLdString = deHTMLdString.replaceAll("\\r\\n", "");
			//return deHTMLdString;
		} else {
			return "";
		}
	}
	
	/**
	 * This whitelist allows a full range of text and structural body HTML: a, b, blockquote, br, 
	 * caption, cite, code, col, colgroup, dd, div, dl, dt, em, h1, h2, h3, h4, h5, h6, i, img, li, ol, p,
	 *  pre, q, small, span, strike, strong, sub, sup, table, tbody, td, tfoot, th, thead, tr, u, ul 
     * 

	 *//*
	String deHTMLCMSContent(String html){
		if (html != null) {
			 return Jsoup.clean(html,Whitelist.basic().addEnforcedAttribute("a", "class", "inactiveLinks").addTags("span"));
		} else {
			return "";
		}
	}*/
	
	/**
	 * Removes any documents which were indexed prior to the current index batch.
	 * Additional doc query can be used to apply this to a specific document type only 
	 */
	void deleteOldDocuments(String docQuery) {
		
		String deleteQuery = "";
		try {
			m_log.info("Deleting old items...");
			deleteQuery = String.format(docQuery + " +indextime:{* TO %s}", indexDateTime);
			SolrClient solrClient = solrConfig.getSolrClient();
			solrClient.deleteByQuery(deleteQuery);
		} catch (Exception e) {
			m_log.error("Failure executing delete query " + deleteQuery, e);
		} 
	}
	
	/**
	 * Takes a list of category IDs and adds them to the index data, along with all parent category IDs
	 * Also include category id 0, to represent the root category, which everything should belong to 
	 */
	void addCategories(String catField, List<Long> catIDs, HashMap<String, Object> docData) throws Exception {
		String catPrefix = catField + ".";
		if (catIDs == null) {
			docData.put(catPrefix + "1", "0"); //Add category 0 (= 'All') in for everything
		}
		else {
			
//			List<Context> allEnabledContexts = contextyDAO.findAllEnabled();
			
			docData.put(catPrefix + "1", "0"); //Add category 0 (= 'All') in for everything
		
			Map<Long, Category> categoriesToAdd = new HashMap<Long, Category>();
			for (Long catID : catIDs){
				Category cat = categoryDAO.findOne(catID);
				if (cat != null){
					
					Category catRoot = cat.getCategoryRootId() != null ? categoryDAO.findOne(cat.getCategoryRootId()):null;
					if (catRoot != null){
						// Only add if facetable?
						while (!cat.equals(catRoot) && cat.isEnabled() && cat.isFacetable()){
							categoriesToAdd.put(cat.getId(), cat);
							cat = categoryDAO.findOne(cat.getCategoryParentId());
						}
					}
				}
			}
		
			int suffix = 2;
			for (Category category : categoriesToAdd.values()){
			
				String catId = category.getId().toString();

				Map<Long, Long> contextIds = new HashMap<Long, Long>();
					
				Category cat = category;
				while (cat != null){
					List<Long> conIds = cat.getContextIds();
					for (Long conId : conIds) {
						contextIds.put(conId, conId);
					}
					if (cat.getCategoryParentId() != null) {
						cat = categoryDAO.findOne(cat.getCategoryParentId());
					} else {
						cat = null;
					}
				}
				
//				if (allEnabledContexts != null && allEnabledContexts.size() > 0) {
//					if (contextIds != null && contextIds.size() != 0 && contextIds.size() < allEnabledContexts.size()) {
//						for (Long contextId : contextIds.values()) {
//							docData.put(catPrefix + suffix, contextId + "_" + catId);	
//							suffix++;
//						}
//					} else {
//						//docData.put(catPrefix + suffix, "0_" + catId);	
//						docData.put(catPrefix + suffix, catId);	
//						suffix++;
//					}
//				} else {
					docData.put(catPrefix + suffix, catId);	
					suffix++;
//				}
			}
			
		}
	}
	
//	public User getUserFromVendor(Vendor v){
//		List<VendorUser> vendorUserList = v.getVendorUserList();
//		for (VendorUser vendorUser : vendorUserList) {
//			if (vendorUser.isPrimaryUser() == true) {
//				return userDAO.findOne(vendorUser.getUserId());
//			}
//		}
//		
//		return null;
//	}
}
