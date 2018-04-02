package com.pcg.search.api.beans;

import java.io.Serializable;

import com.sa.assist.controller.api.cms.json.packets.MetaDataPacket;

/**
 * 
 */
public class CmsDocument implements Serializable {
	
	private Integer umbNodeId;
	private String pageTitle;
	private String pageContent;
	private String pageUrl;
	private String docType;
//	private MetaDataPacket metaData;
	private Boolean smartSuggest;
//	private String context;

	public CmsDocument() {
	}
	
	public Integer getUmbNodeId() {
		return umbNodeId;
	}
	public void setUmbNodeId(Integer umbNodeId) {
		this.umbNodeId = umbNodeId;
	}
	public String getPageTitle() {
		return pageTitle;
	}
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	public String getPageContent() {
		return pageContent;
	}
	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
//	public MetaDataPacket getMetaData() {
//		return metaData;
//	}
//	public void setMetaData(MetaDataPacket metaData) {
//		this.metaData = metaData;
//	}
	public Boolean getSmartSuggest() {
		return smartSuggest;
	}
	public void setSmartSuggest(Boolean smartSuggest) {
		this.smartSuggest = smartSuggest;
	}
//	public String getContext() {
//		return context;
//	}
//	public void setContext(String context) {
//		this.context = context;
//	}

	public CmsDocument(Integer umbNodeId, String pageTitle, String pageContent, String docType, String pageUrl) {
		super();
		this.umbNodeId = umbNodeId;
		this.pageTitle = pageTitle;
		this.pageContent = pageContent;
		this.docType = docType;
		this.pageUrl = pageUrl;
//		this.context = context;
	}
	
}
