package com.pcg.search.api.beans;

import java.util.List;

public class APIQueryResponse {

	private String searchText;
	private String searchType;
	private String searchCategory;
	private String searchPostcode;
	private List<SearchResult> searchResults;
//	private String view;

//	private Map<String, Long> searchTypeFacets = new HashMap<String, Long>();
//	private Map<Long, Long> categoryFacets = new HashMap<Long, Long>();
//	private Map<String, Long> atozFacets = new HashMap<String, Long>();
	
//	private String sortOption="Relevancy";
//	private String otherSearchType;
//	private int otherResultCount;

    //Pagination stats
    private int totalResults;
    private int pageSize;
    
    private int currentPage;
    private int lastPage;

    private int firstNavPage;
    private int lastNavPage;
    
    private int firstResult;
    private int lastResult;
    
    private String randomSortID;
    
    private String didYouMean;
    
    private List<SearchResult> recommendedLinks;

	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}	
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public String getSearchCategory() {
		return searchCategory;
	}
	public void setSearchCategory(String searchCategory) {
		this.searchCategory = searchCategory;
	}
	public String getSearchPostcode() {
		return searchPostcode;
	}
	public void setSearchPostcode(String searchPostcode) {
		this.searchPostcode = searchPostcode;
	}
//	public String getView() {
//		return view;
//	}
//	public void setView(String view) {
//		this.view = view;
//	}
//	public Map<String, Long> getSearchTypeFacets() {
//		return searchTypeFacets;
//	}
//	public void setSearchTypeFacets(Map<String, Long> searchTypeFacets) {
//		this.searchTypeFacets = searchTypeFacets;
//	}
//	public void setCategoryFacets(Map<Long, Long> categoryFacets) {
//		this.categoryFacets = categoryFacets;
//	}
//	public Map<Long, Long> getCategoryFacets() {
//		return categoryFacets;
//	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getFirstNavPage() {
		return firstNavPage;
	}
	public void setFirstNavPage(int firstNavPage) {
		this.firstNavPage = firstNavPage;
	}
	public int getLastPage() {
		return lastPage;
	}
	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}
	public int getLastNavPage() {
		return lastNavPage;
	}
	public void setLastNavPage(int lastNavPage) {
		this.lastNavPage = lastNavPage;
	}
	public int getFirstResult() {
		return firstResult;
	}
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}
	public int getLastResult() {
		return lastResult;
	}
	public void setLastResult(int lastResult) {
		this.lastResult = lastResult;
	}
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}
	public int getTotalResults() {
		return this.totalResults;
	}
//	public void setSortOption(String sortOption) {
//		this.sortOption = sortOption;
//	}
//	public String getSortOption() {
//		return sortOption;
//	}
//	public void setOtherSearchType(String otherSearchType) {
//		this.otherSearchType = otherSearchType;
//	}
//	public String getOtherSearchType() {
//		return otherSearchType;
//	}
//	public void setOtherResultCount(int otherResultCount) {
//		this.otherResultCount = otherResultCount;
//	}
//	public int getOtherResultCount() {
//		return otherResultCount;
//	}
	public void setRandomSortID(String randomSortID) {
		this.randomSortID = randomSortID;
	}
	public String getRandomSortID() {
		return randomSortID;
	}
//	public void setAtozFacets(Map<String, Long> atozFacets) {
//		this.atozFacets = atozFacets;
//	}
//	public Map<String, Long> getAtozFacets() {
//		return atozFacets;
//	}
	public String getDidYouMean() {
		return didYouMean;
	}
	public void setDidYouMean(String didYouMean) {
		this.didYouMean = didYouMean;
	}
	public List<SearchResult> getRecommendedLinks() {
		return recommendedLinks;
	}
	public void setRecommendedLinks(List<SearchResult> recommendedLinks) {
		this.recommendedLinks = recommendedLinks;
	}
	public List<SearchResult> getSearchResults() {
		return searchResults;
	}
	public void setSearchResults(List<SearchResult> searchResults) {
		this.searchResults = searchResults;
	}
}
