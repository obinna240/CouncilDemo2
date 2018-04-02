package com.sa.assist.controller.api.cms.json;

import java.util.List;

import com.sa.assist.search.SearchResult;
import com.sa.assist.tree.TreeNode;

public class CMSSearchResult {
	private SearchResult searchResult;
	private List<TreeNode> categoryList;
	private List<TreeNode> conditionList;
	private String jsonMapPins;

	public SearchResult getSearchResult() {
		return searchResult;
	}
	
	public void setSearchResult(SearchResult searchResult) {
		this.searchResult = searchResult;
	}
	
	public List<TreeNode> getCategoryList() {
		return categoryList;
	}
	
	public void setCategoryList(List<TreeNode> categoryList) {
		this.categoryList = categoryList;
	}
	
	public List<TreeNode> getConditionList() {
		return conditionList;
	}
	
	public void setConditionList(List<TreeNode> conditionList) {
		this.conditionList = conditionList;
	}

	public String getJsonMapPins() {
		return jsonMapPins;
	}

	public void setJsonMapPins(String jsonMapPins) {
		this.jsonMapPins = jsonMapPins;
	}
	
	
	
	
}

