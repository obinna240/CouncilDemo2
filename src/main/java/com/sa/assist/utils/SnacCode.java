package com.sa.assist.utils;

import org.apache.commons.lang.StringUtils;

/**
 * Helper class for dealing with SNAC codes, which are used as authority identifiers.
 * SNAC code is another term for ONS code
 * See http://en.wikipedia.org/wiki/ONS_coding_system 
 */
public class SnacCode {

	/**
	 * Returns just the main authority part of the code
	 * So a county, district or ward SNAC will return just the county part
	 * and a unitary will return itself
	 */
	public static String getMainAuthoritySNAC(String snacCode){
		if (isUnitary(snacCode) || isCounty(snacCode)){
			return snacCode;
		}
		else {
			return StringUtils.substring(snacCode, 0, 2);
		}
	}
	
	/**
	 * In the case of a unitary authority (including metropolitan and London boroughs) the first two digits were 00.
	 * For example, 00AL for Greenwich (London Borough) or 00EC for Middlesbrough.
	 */
	public static boolean isUnitary(String snacCode){
		return (StringUtils.startsWith(snacCode, "00"));
	}
	
	/**
	 * A two-character code represented an administrative county.
	 * For example, 12 for Cambridgeshire.
	 */
	public static boolean isCounty(String snacCode){
		return (StringUtils.length(snacCode) == 2);
	}

	/**
	 * A four-character code represented a district, so that the first two characters showed the county in which the district was placed.
	 * For example, 12UB for Cambridge district or 12UD for Fenland.
	 */
	public static boolean isDistrict(String snacCode){
		return (!isUnitary(snacCode) && StringUtils.length(snacCode) == 4);
	}

	/**
	 * Local Government wards were given a two-letter code within their local authority.
	 * For example 12UBGA for Petersfield Ward within Cambridge district.
	 */
	public static boolean isWard(String snacCode){
		return (StringUtils.length(snacCode) == 6);
	}
}
