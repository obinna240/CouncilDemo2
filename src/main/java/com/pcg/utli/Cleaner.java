package com.pcg.utli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;


public class Cleaner 
{
	public static String[] splitUnderscore(String string)
	{
		String[] splitString = string.split("_");
		return splitString;
	}
	
	public static List<String> mergeListForFiler(String filterType, String[] array)
	{
		List<String> mergerList = null;
		if(array.length>0)
		{
			mergerList = new ArrayList<String>();
			for(String arr:array)
			{
				arr = StringUtils.normalizeSpace(arr);
				arr = filterType+":"+arr;
				mergerList.add(arr);
			}
		}
		return mergerList;
		
	}
	
	/**
	 * 
	 * @param filters
	 * @return String 
	 */
	public static String createFilterParam(List<String> filters)
	{
		String filterParam = null;
		if(CollectionUtils.isNotEmpty(filters))
		{
			int fsize = filters.size();
			if(fsize == 1)
			{
				filterParam = filters.get(0);
			}
			else if(fsize > 1)
			{
				filterParam = "";
				for(int i=0;i<fsize;i++)
				{
					if(i!=fsize-1)
					{
						filterParam = filterParam +filters.get(i)+" AND ";
					}
					else if(i==fsize-1)
					{
						filterParam = filterParam+filters.get(i);
					}
				}
			}
		}
		return filterParam;
	}
	
	/**
	public static void main(String[] args)
	{
		//String s = "de_mhc_ld_oa_pd_si_ppad_ppdd";
		String s = "de";
		String[] a = Cleaner.splitUnderscore(s);
		System.out.println(a.length);
		for(String ss:a)
		{
			System.out.println(ss);
		}
			
	}
	*/
	
}
