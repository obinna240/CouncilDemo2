package com.pcg.search.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.pcg.search.api.beans.PaginatorBean;

public class PaginationUtils 
{
		
	/**
	 * returns the initial PaginatorBean for building the paginator
	 * @param paginatorValue
	 * @return
	 */
	public static PaginatorBean checkPagination(Integer paginatorValue)
	{
		PaginatorBean paginatorBean = new PaginatorBean();
		
		
		
		if(paginatorValue!=null)
		{
			paginatorBean.setPage(paginatorValue); //sets the current page
		}
		
		
		return paginatorBean;
	}
	
	/**
	 * normally pass value of check Pagination into this method
	 * @param paginatorBean
	 */
	public static Map<String,Object> getQueryParams(PaginatorBean paginatorBean)
	{
		PaginationImpl paginationImpl = new PaginationImpl();
		PaginatorBean pBean = paginationImpl.doSet(paginatorBean);
		
		Integer queryStart = (pBean.getPage()-1)*pBean.getPageSize();
		Integer queryRows = pBean.getPageSize();
		
		Map<String,Object> mapOfParams = new LinkedHashMap<String, Object>();
		
		mapOfParams.put("queryStart", queryStart);
		mapOfParams.put("queryRows", queryRows);
		mapOfParams.put("originalPaginatorBean", paginatorBean);
		
		return mapOfParams;
	}
	
	
	/**
	 * 
	 * @param resultsFound
	 * @param pBean
	 * @return PaginatorBean
	 */
	public static PaginatorBean doPaginationTest(Integer resultsFound, PaginatorBean pBean)
	{
		PaginationImpl paginationImpl = new PaginationImpl();
		PaginatorBean newPaginatorBean =  paginationImpl.doPagination(resultsFound, pBean);
		newPaginatorBean.setPage(pBean.getPage());
		return newPaginatorBean;
	}
	

}

