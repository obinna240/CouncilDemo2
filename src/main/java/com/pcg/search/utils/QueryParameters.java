package com.pcg.search.utils;

import java.util.HashMap;
import java.util.List;




/**
 * 
 * @author oonyimadu
 *
 */
public class QueryParameters 
{

	private HashMap<String,List<String>> query;
	private String solrQueryUrl;
	private String solrSmartSuggestUrl;
	private HashMap<String, String> coordinates;
	
	/**
	 * @return the query
	 */
	public HashMap<String,List<String>> getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(HashMap<String,List<String>> query) {
		this.query = query;
	}

	/**
	 * @return the solrQueryUrl
	 */
	public String getSolrQueryUrl() {
		return solrQueryUrl;
	}

	/**
	 * @param solrQueryUrl the solrQueryUrl to set
	 */
	public void setSolrQueryUrl(String solrQueryUrl) {
		this.solrQueryUrl = solrQueryUrl;
	}

	/**
	 * @return the solrSmartSuggestUrl
	 */
	public String getSolrSmartSuggestUrl() {
		return solrSmartSuggestUrl;
	}

	/**
	 * @param solrSmartSuggestUrl the solrSmartSuggestUrl to set
	 */
	public void setSolrSmartSuggestUrl(String solrSmartSuggestUrl) {
		this.solrSmartSuggestUrl = solrSmartSuggestUrl;
	}

	public HashMap<String, String> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(HashMap<String, String> coordinates) {
		this.coordinates = coordinates;
	}

	

	

}

