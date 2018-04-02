package com.pcg.search.utils;

import com.pcg.search.api.beans.PaginatorBean;

/**
 * 
 * @author obiPC
 *
 */
public class PaginationImpl 
{

	PaginatorBean pgBean;
		
	public PaginatorBean getPgBean() 
	{
		return pgBean;
	}

	public void setPgBean(PaginatorBean pgBean) 
	{
		this.pgBean = pgBean;
	}
	
	/**
	 * 
	 * @param pgBean
	 * @return PaginatorBean
	 */
	public PaginatorBean doSet(PaginatorBean pgBean)
	{
		//here we set the start
		if(pgBean.getPage() == null || pgBean.getPage() <1)
		{
			pgBean.setPage(1);
		}
		if (pgBean.getPageSize() == null || pgBean.getPageSize() < 1) 
		{
            pgBean.setPageSize(pgBean.getDefaultPageSize());
		}
		
		return pgBean;
	}
	
	/**
	 * 
	 * @param totalResult
	 * @param entriesPerPage
	 */
	public PaginatorBean doPagination(int totalResult, PaginatorBean pgBean)
	{
		int pageSize =  pgBean.getDefaultPageSize();
		if (totalResult != 0)
		{
			int totalPages = totalResult/pageSize + (totalResult % pageSize >0 ?1:0);
			
			int firstPage = 0;
			int lastPage = 0;
			
			
			if(pgBean.getPage() >= totalPages - (pgBean.getPagesInView()/2))
			{
				lastPage = totalPages;
			}
			else if(pgBean.getPage() <= (pgBean.getPagesInView()/2))
			{
				lastPage = Math.min(totalPages, pgBean.getPagesInView());
			}
			else
			{
				lastPage = pgBean.getPage() + (pgBean.getPagesInView()/2);
			}
			
			
			if (pgBean.getPage() <= (pgBean.getPagesInView()/2)) 
			{
                firstPage = 1;
			}
			else if(pgBean.getPage() > totalPages - (pgBean.getPagesInView()/2)) 
			{
                firstPage = Math.max((totalPages - pgBean.getPagesInView()) + 1, 1);
			}
			else 
			{
                firstPage = pgBean.getPage() - (pgBean.getPagesInView()/2);
			}
			
			pgBean.setFirstPage(firstPage);
			pgBean.setLastPage(lastPage);
			pgBean.setTotalPages(totalPages);
			pgBean.setTotalResult(totalResult);
		}
		return pgBean;
	}
	 
}

