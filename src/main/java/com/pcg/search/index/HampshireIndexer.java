package com.pcg.search.index;

import java.io.IOException;
import java.util.Date;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

/**
 * 
 * @author oonyimadu
 *
 */
public class HampshireIndexer 
{
	public static void main(String[] args) throws SolrServerException, IOException
	{
		
		String urlString = "http://localhost:8984/solr/live";
		SolrClient solr = new HttpSolrClient(urlString);
		
		SolrInputDocument document = new SolrInputDocument();
		
		document.addField("id", "1");
		document.addField("name", "110 West Street Care Home");
		
		document.addField("website", "www.hants.gov.uk/hcc-homes");
		document.addField("description", "Referrals for the service must come via HCC.");
		
		document.addField("address", "110 West Street");
		
		
		document.addField("town", "Havant");
		document.addField("full_postcode", "PO9 1LN");
		document.addField("postcode1", "PO9");
		document.addField("postcode2", "1LN");
		
		document.addField("location", "51.123226,-2.752695");
		document.addField("publicEmail", "kevin.boxall@hants.gov.uk");
		
		document.addField("phone", "023 9249 8333");

		document.addField("careProvided", "ld");
		document.addField("homeType", "wn");
			
		document.addField("dateOfIndex", new Date());
		
		solr.add(document);
		
		
		document = new SolrInputDocument();
		
		document.addField("id", "2");
		document.addField("name", "19 Chilgrove Road");
		
		document.addField("url", "www.southernhealth.nhs.uk");
		
		
		document.addField("address", "Drayton");
		
		
		document.addField("town", "Portsmouth");
		document.addField("full_postcode", "PO6 2ER");
		document.addField("postcode1", "PO6");
		document.addField("postcode2", "2ER");
		
		document.addField("location", "50.845165,-1.047100");
		
		
		document.addField("phone", "023 9221 0602");
		
		
		document.addField("careProvided", "ld");
		document.addField("homeType", "wtn");
		
		document.addField("docType", "Care Home");
		
		document.addField("localAuthority", "Hampshire");
		document.addField("linkToCQCRating", "http://www.cqc.org.uk/location/RW1J2");
		
		document.addField("lastReviewed", "August 2015");
		document.addField("numberOfBeds", 4);
		document.addField("typeOfHome", "NHS");
		document.addField("dateOfIndex", new Date());
		
		solr.add(document);
		
		document = new SolrInputDocument();
		
		document.addField("id", "3");
		document.addField("name", "19 Forest Road");
		
		document.addField("url", "www.omegaelifar.com");
		
		
		document.addField("address", "19 Forest Road");
	
		
		document.addField("town", "Bordon");
		document.addField("full_postcode", "GU35 0BJ");
		document.addField("postcode1", "GU35");
		document.addField("postcode2", "OBJ");
		
		document.addField("location", "51.110626,-0.859603");
		
		
		document.addField("phone", "01420 488428");

		document.addField("careProvided", "ld");
		document.addField("admissions", "idc");
		
		document.addField("homeType", "wtn");
		
		document.addField("docType", "Care Home");
		
		document.addField("localAuthority", "Hampshire");
		document.addField("linkToCQCRating", "http://www.cqc.org.uk/location/1-1535127571");
		
		document.addField("lastReviewed", "August 2015");
		document.addField("numberOfBeds", 6);
		document.addField("typeOfHome", "Private");
		document.addField("dateOfIndex", new Date());
		
		solr.add(document);
		
		document = new SolrInputDocument();
		
		document.addField("id", "4");
		document.addField("name", "31 Whitwell Road");
		
		document.addField("url", "");
		
		
		document.addField("address", "32 Whitwell Road");
		
		
		document.addField("town", "SOUTHSEA");
		document.addField("full_postcode", "PO4 0QP");
		document.addField("postcode1", "PO4");
		document.addField("postcode2", "OQP");
		
		document.addField("location", "50.781284,-1.075763");
		
		
		document.addField("phone", "023 9279 3941");
		
		document.addField("vacancies","");
		
		document.addField("careProvided", "ld");
		//document.addField("careProvided", "mhc");
		
		document.addField("admissions", "fdc");
		
		document.addField("homeType", "wtn");
		
		document.addField("docType", "Care Home");
		
		document.addField("localAuthority", "Hampshire");
		document.addField("linkToCQCRating", "http://www.cqc.org.uk/location/1-148164209");
		
		document.addField("lastReviewed", "August 2015");
		document.addField("numberOfBeds", 9);
		document.addField("typeOfHome", "Private");
		
		document.addField("dateOfIndex", new Date());
		
		document.addField("hca", "");
		document.addField("rnha", "");
		document.addField("nca", "");
		document.addField("ce", "");
		document.addField("ncf", "");
		document.addField("description", "");
		solr.add(document);
		
		
		document = new SolrInputDocument();
		
		document.addField("id", "5");
		document.addField("name", "374-376 Winchester Road");
		
		document.addField("url", "www.lifeways.co.uk");
		
		document.addField("description", "Support for people with Autism, complex needs and challenging behaviour.");
		document.addField("address", "374-376 Winchester Road");
	
		
		document.addField("town", "Southampton");
		document.addField("full_postcode", "SO16 6TW");
		document.addField("postcode1", "SO16");
		document.addField("postcode2", "6TW");
		
		document.addField("location", "50.934246,-1.419169");
		
		
		document.addField("phone", "023 8078 9786");
		
		document.addField("vacancies","");
		
		document.addField("careProvided", "ld");
		document.addField("careProvided", "mhc");
		
		document.addField("admissions", "ss");
		document.addField("admissions", "fdc");
		document.addField("admissions", "idc");
		
		document.addField("homeType", "wtn");
		
		document.addField("docType", "Care Home");
		
		document.addField("localAuthority", "Hampshire");
		document.addField("linkToCQCRating", "http://www.cqc.org.uk/location/1-162779051 ");
		
		document.addField("lastReviewed", "August 2015");
		document.addField("numberOfBeds", 8);
		document.addField("typeOfHome", "Private");
		
		document.addField("dateOfIndex", new Date());
		
		document.addField("hca", "YES");
		document.addField("rnha", "");
		document.addField("nca", "");
		document.addField("ce", "");
		document.addField("ncf", "");
		
		solr.add(document);
		
		
		document = new SolrInputDocument();
		
		document.addField("id", "6");
		document.addField("name", "41 Birdwood Grove");
		
		document.addField("url", "www.tqtwentyone.org");
		
		document.addField("description", "41 Birdwood Grove is registered to provide accommodation with personal care for up to three people with learning disabilities.");
		document.addField("address", "42 Birdwood Grove");
	
		
		document.addField("town", "Fareham");
		
		document.addField("full_postcode", "PO16 8AJ");
		document.addField("postcode1", "PO16");
		document.addField("postcode2", "8AJ");
		
		document.addField("location", "50.848534,-1.155394");
		
		
		document.addField("phone", "01329 221623");
		
		document.addField("vacancies","");
		
		document.addField("careProvided", "ld");
		
		
		
		
		document.addField("homeType", "wtn");
		
		document.addField("docType", "Care Home");
		
		document.addField("localAuthority", "Hampshire");
		document.addField("linkToCQCRating", "http://www.cqc.org.uk/location/RW1K2");
		
		document.addField("lastReviewed", "August 2015");
		document.addField("numberOfBeds", 3);
		document.addField("typeOfHome", "NHS");
		
		document.addField("dateOfIndex", new Date());
		
		
		
		solr.add(document);
		
		solr.commit();
		solr.close();
		
		System.out.println("completed");
	}
		//System.out.println("completed");}
}
