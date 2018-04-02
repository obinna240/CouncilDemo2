package com.pcg.search.api.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pcg.search.api.beans.CmsDocument;
import com.pcg.search.api.beans.APIStatusResponse;
import com.pcg.search.index.IndexManager;

import flexjson.JSONDeserializer;

@RestController
@RequestMapping("/api/index/**")
public class IndexController {
	
	private static Log m_log = LogFactory.getLog(IndexController.class);
	
	@Autowired
	private IndexManager indexManager;
	
	@RequestMapping(method=RequestMethod.POST, value = "/addDoc")
	public APIStatusResponse addDoc(@RequestBody String json) {
		
		APIStatusResponse response;
		
		m_log.debug("JSON input : " + json);
		
		try {
			CmsDocument doc = new JSONDeserializer<CmsDocument>().use(null, CmsDocument.class).deserialize(json);
			indexManager.indexCmsDocument(doc);
			response = new APIStatusResponse(APIStatusResponse.StatusCode.OK, "Document added", null);
		} catch (Exception e) {
			m_log.error(e.getMessage());
			response = new APIStatusResponse(APIStatusResponse.StatusCode.ERROR, e.getMessage(), null);
		}
		
		return response;
	}
	
	@RequestMapping(method=RequestMethod.POST, value = "/addDocs")
	public APIStatusResponse addDocs(@RequestBody String json) {
		return new APIStatusResponse(APIStatusResponse.StatusCode.OK, "NOT YET IMPLEMENTED", null);
	}
	
	@RequestMapping(value = "/removeById")
	public APIStatusResponse removeDocById(@RequestParam(value = "id", required = true) String docId) {
		return new APIStatusResponse(APIStatusResponse.StatusCode.OK, "NOT YET IMPLEMENTED", null);
	}
	
	@RequestMapping(value = "/removeByUrl")
	public APIStatusResponse removeDocByUrl(@RequestParam(value = "url", required = true) String url) {
		return new APIStatusResponse(APIStatusResponse.StatusCode.OK, "NOT YET IMPLEMENTED", null);
	}
	
}
