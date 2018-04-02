package com.pcg.search.api.beans;

import java.util.List;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * @author oonyimadu
 *
 */
@XStreamAlias("root")
public class CareHomeBeans 
{
	

	private List<CareHomeBean> careHomeBean;
	private PaginatorBean paginatorBean;
	
	public List<CareHomeBean> getCareHomeBean() {
		return careHomeBean;
	}

	public void setCareHomeBean(List<CareHomeBean> careHomeBean) {
		this.careHomeBean = careHomeBean;
	}
	public PaginatorBean getPaginatorBean() {
		return paginatorBean;
	}

	public void setPaginatorBean(PaginatorBean paginatorBean) {
		this.paginatorBean = paginatorBean;
	}
	
}


