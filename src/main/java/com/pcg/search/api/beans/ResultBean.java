package com.pcg.search.api.beans;

/**
 * 
 * @author oonyimadu
 *
 */
public class ResultBean 
{
	private Integer totalNumberOfResults;
	private Integer pageNumber;
	private Integer start;
	private CareHomeBeans careHomeBeans;
	private QueryBean queryBean;
	
	public Integer getTotalNumberOfResults() {
		return totalNumberOfResults;
	}
	public void setTotalNumberOfResults(Integer totalNumberOfResults) {
		this.totalNumberOfResults = totalNumberOfResults;
	}
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	
	public CareHomeBeans getCareHomeBeans() {
		return careHomeBeans;
	}
	public void setCareHomeBeans(CareHomeBeans careHomeBeans) {
		this.careHomeBeans = careHomeBeans;
	}
	public QueryBean getQueryBean() {
		return queryBean;
	}
	public void setQueryBean(QueryBean queryBean) {
		this.queryBean = queryBean;
	}
	
	//private QueryBeans queryBeans;
	
	
	
}
