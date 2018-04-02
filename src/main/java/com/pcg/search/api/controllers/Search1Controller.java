package com.pcg.search.api.controllers;

//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pcg.search.utils.PaginationUtils;
import com.pcg.search.utils.SolrQueryUtils;
import com.pcg.search.api.beans.*;


@RestController
@RequestMapping("/api/query")
public class Search1Controller 
{
	@Autowired
	SolrQueryUtils solrQueryUtils;
	private static Log log = LogFactory.getLog(Search1Controller.class);	
	
	/**
	 * @param cp care provided
	 * @param ad admissions
	 * @param tc type of care
	 * @param pp page number
	 */
	@RequestMapping(value="/generic", method=RequestMethod.GET)
	public ResponseEntity<ResultBean> searchGeneric(
			@RequestParam(required=false) String cp,     
			@RequestParam(required=false) String ad, 
			@RequestParam(required=false) String tc,
			@RequestParam(required=false) String pp)//cp=dm_mn_ld_od_ph_se_pa_pd&ad=sv_as_ae&tc=all
	{
		log.info("====== New Generic Search Query = "+"care provided "+cp + ", admissions "+ad+", type of care "+tc+", page "+pp+" ======");
		PaginatorBean paginatorBean = null;
		Integer pageNumber = null;
				
		if(pp==null)
		{
			paginatorBean = PaginationUtils.checkPagination(null);
		}
		else
		{
			pageNumber =  Integer.parseInt(pp);
			paginatorBean = PaginationUtils.checkPagination(pageNumber);
		}
		
		Map<String,Object> qParamStartRow = PaginationUtils.getQueryParams(paginatorBean);
		
		ResultBean resultBean = solrQueryUtils.doGenericSearch(cp,ad,tc, qParamStartRow);
		
		if(resultBean!=null)
		{
			resultBean.setPageNumber(paginatorBean.getPage());
		
		}
		else
		{
			resultBean = new ResultBean();
			
			resultBean.setCareHomeBeans(null);
			Integer qStart = (Integer)qParamStartRow.get("queryStart");
			resultBean.setStart(qStart);
			
			resultBean.setTotalNumberOfResults(0);
		}
		
		QueryBean qBean = new QueryBean();
		
		qBean.setQueryType("generic");
		qBean.setNameQuery("");
		qBean.setAdmissions(ad);
		qBean.setTypeOfCareHome(tc);
		qBean.setCareProvided(cp);
		qBean.setTown("");
		qBean.setPostcode("");
		qBean.setRadius(null);
		
		resultBean.setQueryBean(qBean);
		
		return new ResponseEntity<ResultBean> (resultBean,HttpStatus.OK);
	}
	
	@RequestMapping(value="/cname", method=RequestMethod.GET)
	public ResponseEntity<ResultBean> searchName(
			@RequestParam String name,     
			@RequestParam(required=false) String town, 
			@RequestParam(required=false) String pp)
	{
		log.info("====== New Name Search Query = "+"Name "+name+ ", town "+town+", page "+pp+" ======");
		PaginatorBean paginatorBean = doGetPaginatorBean(pp);
		
		Map<String,Object> qParamStartRow = PaginationUtils.getQueryParams(paginatorBean);
		
		ResultBean resultBean = solrQueryUtils.doNameSearch(name, town, qParamStartRow);
		
		if(resultBean!=null)
		{
			resultBean.setPageNumber(paginatorBean.getPage());
		
		}
		else
		{
			resultBean = new ResultBean();
			
			resultBean.setCareHomeBeans(null);
			Integer qStart = (Integer)qParamStartRow.get("queryStart");
			resultBean.setStart(qStart);
			
			resultBean.setTotalNumberOfResults(0);
		}
		
		QueryBean qBean = new QueryBean();
		
		qBean.setQueryType("name");
		qBean.setNameQuery(name);
		qBean.setAdmissions(null);
		qBean.setTypeOfCareHome(null);
		qBean.setCareProvided(null);
		qBean.setTown(town);
		qBean.setPostcode(null);
		qBean.setRadius(null);
		
		resultBean.setQueryBean(qBean);
		
		return new ResponseEntity<ResultBean> (resultBean,HttpStatus.OK);
	}
	
	@RequestMapping(value="/pc", method=RequestMethod.GET)
	public ResponseEntity<ResultBean> searchPostCode(@RequestParam String postcode,  
						@RequestParam(required=false) String radius,
						@RequestParam(required=false) String cp,     
						@RequestParam(required=false) String ad, 
						@RequestParam(required=false) String tc,
						@RequestParam(required=false) String pp)
	{
		log.info("====== New Post code search Query = "+"postcode "+postcode+ ", radius "+radius+" care provided "+cp+", page "+pp+", admissions "+ad+ ", type of care "+tc+"+ ======");
		
		PaginatorBean paginatorBean = doGetPaginatorBean(pp);
		
		if(StringUtils.isBlank(radius))
		{
			radius = "5";
		}
		
		Map<String,Object> qParamStartRow = PaginationUtils.getQueryParams(paginatorBean);
		
		ResultBean resultBean = solrQueryUtils.doLocationSearch("postcode", postcode, radius, cp, ad, tc, qParamStartRow);
		
		if(resultBean!=null)
		{
			resultBean.setPageNumber(paginatorBean.getPage());
		
		}
		else
		{
			resultBean = new ResultBean();
			
			resultBean.setCareHomeBeans(null);
			Integer qStart = (Integer)qParamStartRow.get("queryStart");
			resultBean.setStart(qStart);
			
			resultBean.setTotalNumberOfResults(0);
		}
		
		QueryBean qBean = new QueryBean();
		
		qBean.setQueryType("postCode");
		qBean.setNameQuery("");
		qBean.setAdmissions(ad);
		qBean.setTypeOfCareHome(tc);
		qBean.setCareProvided(cp);
		qBean.setTown(null);
		qBean.setPostcode(postcode);
		qBean.setRadius(Integer.parseInt(radius));
		
		resultBean.setQueryBean(qBean);
		
		return new ResponseEntity<ResultBean> (resultBean,HttpStatus.OK);
	}
	
	/**
	 * 
	 * @param town
	 * @param radius
	 * @param cp
	 * @param ad
	 * @param tc
	 * @param pp
	 * @return ResponseEntity<ResultBean>
	 */
	@RequestMapping(value="/twn", method=RequestMethod.GET)
	public ResponseEntity<ResultBean> searchTown(@RequestParam String town,
			@RequestParam(required=false) String radius,
			@RequestParam(required=false) String cp,     
			@RequestParam(required=false) String ad, 
			@RequestParam(required=false) String tc,
			@RequestParam(required=false) String pp)
	{
		PaginatorBean paginatorBean = doGetPaginatorBean(pp);
		
		Map<String,Object> qParamStartRow = PaginationUtils.getQueryParams(paginatorBean);
		
		if(StringUtils.isBlank(radius))
		{
			radius = "5";
		}
		ResultBean resultBean = solrQueryUtils.doLocationSearch("town", town, radius, cp, ad, tc, qParamStartRow);
		
		if(resultBean!=null)
		{
			resultBean.setPageNumber(paginatorBean.getPage());
		
		}
		else
		{
			resultBean = new ResultBean();
			
			resultBean.setCareHomeBeans(null);
			Integer qStart = (Integer)qParamStartRow.get("queryStart");
			resultBean.setStart(qStart);
			
			resultBean.setTotalNumberOfResults(0);
		}
		
		QueryBean qBean = new QueryBean();
		
		qBean.setQueryType("town");
		qBean.setNameQuery("");
		qBean.setAdmissions(ad);
		qBean.setTypeOfCareHome(tc);
		qBean.setCareProvided(cp);
		qBean.setTown(town);
		qBean.setPostcode(null);
		qBean.setRadius(Integer.parseInt(radius));
		
		resultBean.setQueryBean(qBean);
		
		return new ResponseEntity<ResultBean> (resultBean,HttpStatus.OK);
		
	}
	/**
	 * 
	 * @param pp
	 * @return PaginatorBean 
	 */
	private PaginatorBean doGetPaginatorBean(String pp)
	{
		PaginatorBean paginatorBean = null;
		Integer pageNumber = null;
				
		if(pp==null)
		{
			paginatorBean = PaginationUtils.checkPagination(null);
		}
		else
		{
			pageNumber =  Integer.parseInt(pp);
			paginatorBean = PaginationUtils.checkPagination(pageNumber);
		}
		return paginatorBean;
	}
	
}
