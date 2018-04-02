package com.pcg.search.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pcg.db.mongo.dao.SystemConfigDAO;
import com.pcg.db.mongo.model.SystemConfig;
import com.pcg.search.api.beans.CmsDocument;
import com.sa.assist.search.SolrConfig;

/**
 * Controlling class for performing index operations 
 */
public class IndexManager {

	@Autowired
	@Qualifier("MainSolrIndex")
	private SolrConfig solrConfig;

	private static final String SOLR_DATE_PATTERN = "yyyy-MM-dd'T'kk:mm:ss'Z'";

	@Autowired private SystemConfigDAO systemConfigDAO;	

	@Autowired private CmsDocumentIndexer cmsDocumentIndexer;

	private static Log m_log = LogFactory.getLog(IndexManager.class);

	public IndexManager(){
	}

	public void indexCmsDocument(CmsDocument doc) {
		List<CmsDocument> docList = new ArrayList<CmsDocument>();
		docList.add(doc);
		indexCmsDocuments(docList, false);
	}

	public void indexCmsDocumentList(List<CmsDocument> docList) {
		indexCmsDocuments(docList, false);
	}


	private SystemConfig getSystemConfig(){
		SystemConfig config = systemConfigDAO.getDefaultSystemConfig();
		return config;
	}


	public void indexAllCms(){
		SystemConfig config = getSystemConfig();
		String url = "XXX";//config.getCmsConfig().getCmsUrl() + config.getCmsConfig().getCmsReIndexUrl();
		HttpMethod  get = new GetMethod(url);
		HttpClient httpClient = new HttpClient();
		int response;
		try {
			m_log.debug("Reindexing all CMS content.");
			response = httpClient.executeMethod(get);
			m_log.info("CMS content reindex response : "+response);
		} catch (HttpException e) {
			m_log.error(e);
		} catch (IOException e) {
			m_log.error(e);
		}

	}

	public void removeCmsContent(CmsDocument cmsDoc) {
		m_log.debug("Removing CMS content based on unpublish event.");
		cmsDocumentIndexer.init(solrConfig, null, false);
		cmsDocumentIndexer.removeCmsContent(cmsDoc);
	}

	private synchronized void indexCmsDocuments(Iterable<CmsDocument> cmsDocs, boolean bDeleteOthers){

		if (cmsDocs == null){
			return;
		}

		cmsDocumentIndexer.init(solrConfig, cmsDocs, bDeleteOthers);
		cmsDocumentIndexer.startIndexing();
	}

	public void setSolrConfig(SolrConfig solrConfig) {
		this.solrConfig = solrConfig;
	}

	public SolrConfig getSolrConfig() {
		return solrConfig;
	}
}
