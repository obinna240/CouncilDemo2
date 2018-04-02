package com.pcg.search.api;

import static org.junit.Assert.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pcg.search.api.beans.CmsDocument;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class IndexControllerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddPost() {
		
		int statusCode = 0;
		
		try {
			CmsDocument doc = new CmsDocument(42, "A test page", "The quick brown fox jumped over the lazy dog", "CMSARTICLE", "http://somedomain.com/testpage");
			String jsonDoc = new JSONSerializer().exclude("*.class").serialize(doc);
			
			HttpClient httpClient = new HttpClient();
			StringRequestEntity requestEntity = new StringRequestEntity(jsonDoc, "application/json","UTF-8");
			PostMethod postMethod = new PostMethod("http://localhost:8080/search/api/index/add");
			
			postMethod.setRequestEntity(requestEntity);
			statusCode = httpClient.executeMethod(postMethod);
			byte[] responseBody = postMethod.getResponseBody();
			
			System.out.println("RESPONSE: " + new String(responseBody));
		}
		catch (Exception e){
			System.out.println("ERROR: " + e.getMessage());
		}
		
    	assertTrue(statusCode == 200);
	}

}
