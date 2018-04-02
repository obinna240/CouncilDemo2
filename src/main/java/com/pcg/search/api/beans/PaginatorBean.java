package com.pcg.search.api.beans;

public class PaginatorBean 
{
	private Integer firstPage;
	
	private Integer lastPage;
	
	private Integer totalResult;
	
	private Integer totalPages;
	
	private Integer defaultPageSize = 20;
	
	private Integer pagesInView = 20;
	
	private Integer page;      
	
	private Integer pageSize;  
	
	private String paginatorUrl;
	
	public Integer getFirstPage() {
		return firstPage;
	}
	
	public void setFirstPage(Integer firstPage) {
		this.firstPage = firstPage;
	}
	
	public Integer getLastPage() {
		return lastPage;
	}
	
	public void setLastPage(Integer lastPage) {
		this.lastPage = lastPage;
	}
	
	public Integer getTotalResult() {
		return totalResult;
	}
	
	public void setTotalResult(Integer totalResult) {
		this.totalResult = totalResult;
	}
	public Integer getTotalPages() {
		return totalPages;
	}
	
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	
	public Integer getDefaultPageSize() {
		return defaultPageSize;
	}
	public void setDefaultPageSize(Integer defaultPageSize) {
		this.defaultPageSize = defaultPageSize;
	}
	public Integer getPagesInView() {
		return pagesInView;
	}
	public void setPagesInView(Integer pagesInView) {
		this.pagesInView = pagesInView;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public String getPaginatorUrl() {
		return paginatorUrl;
	}
	public void setPaginatorUrl(String paginatorUrl) {
		this.paginatorUrl = paginatorUrl;
	}

	
	
}

