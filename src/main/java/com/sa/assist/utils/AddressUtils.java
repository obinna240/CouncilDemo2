package com.sa.assist.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AddressUtils {

	private static Log m_log = LogFactory.getLog(AddressUtils.class);

	
	
	public static String getPostcodeOutbound(String postcode) {
    	String pc  = postcode.trim().toUpperCase();
		
		if (postcode.length() >= 5) {
			return pc.substring(0, pc.length() -3).trim(); 
		}
		else {
			// assume postcode is just the outbound part
			return pc;
		}
		
	}
	

}
