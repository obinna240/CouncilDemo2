package com.pcg.db.mongo.model;

import java.io.Serializable;


/**
 * Data bean for holding system-wide configuration data
 */
public class SystemConfig implements Serializable, IAssistConfig {

	private String id;
		private String solrServer;
		
		public String getSolrServer() {
			return solrServer;
		}
		public void setSolrServer(String solrServer) {
			this.solrServer = solrServer;
		}
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
	}
