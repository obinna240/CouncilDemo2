package com.pcg.utli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import com.pcg.search.api.beans.CareHomeBean;

/**
 * 
 * @author oonyimadu
 *
 */
public class Excel2Object
{
	static Object checkCell(HSSFCell cell)
	{
		Object obj = null;
		
			switch (cell.getCellType()) 
	        {
	            case Cell.CELL_TYPE_NUMERIC:
	               // System.out.print(cell.getNumericCellValue() + "t");
	                obj = cell.getNumericCellValue();
	                break;
	            case Cell.CELL_TYPE_STRING:
	              //  System.out.print(cell.getStringCellValue() + "t");
	                obj = cell.getStringCellValue();
	                break;
	        }
		
		return obj;
	}
	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws SolrServerException
	 */
	public static void main(String[] args) throws IOException, SolrServerException 
	{
		
		FileInputStream file = new FileInputStream(new File("correct_excel.xls"));
		POIFSFileSystem fs = new POIFSFileSystem(file);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(0);
		
        for(int i=591;i<=630;i++)
        {
        	HSSFRow row = sheet.getRow(i);
        	if(row!=null)
        	{
        		CareHomeBean cbean = new CareHomeBean();
        		
        		HSSFCell cell = row.getCell(1);
        		String name = (String) checkCell(cell);
        		
        		String urlString = "http://localhost:8984/solr/live";
        		SolrClient solr = new HttpSolrClient(urlString);
        		SolrInputDocument document = new SolrInputDocument();
        		
        		
        		cbean.setId(Integer.toString(i-1));
        		document.addField("id", Integer.toString(i-1));
        		
        		cbean.setName(name);
        		document.addField("name", name);
        		
        		cell = row.getCell(2);
        		if(cell!=null)
        		{
        			String address = (String) checkCell(cell);
        			address = StringUtils.normalizeSpace(address);
        			cbean.setAddress(address);
        			document.addField("address", address);
        		}
        		
        		String pcode1 = "";
        		String pcode2 = "";
        		String fullPostCode = "";
        		
        		cell = row.getCell(3);
        		if(cell!=null)
        		{
        			pcode1 = (String) checkCell(cell);
        			pcode1 = StringUtils.normalizeSpace(pcode1);
        			cbean.setPostcode1(pcode1);
        			document.addField("postcode1", pcode1);
        			fullPostCode = pcode1+" ";
        			
        			
        		}
        		
        		cell = row.getCell(4);
        		if(cell!=null)
        		{
        			pcode2 = (String) checkCell(cell);
        			pcode2 = StringUtils.normalizeSpace(pcode2);
        			cbean.setPostcode2(pcode2);
        			document.addField("postcode2", pcode2);
        			fullPostCode = fullPostCode+pcode2;
        		}
        		
        		if(StringUtils.isNotBlank(fullPostCode))
        		{
        			cbean.setFull_postcode(fullPostCode);
        			document.addField("full_postcode", fullPostCode );
        		}
        		
        		cell = row.getCell(5);
        		if(cell!=null)
        		{
        			String phone = (String) checkCell(cell);
        			phone = StringUtils.normalizeSpace(phone);
        			cbean.setPhone(phone);
        			document.addField("phone", phone);
        		}
        		
        		cell = row.getCell(6);
        		if(cell!=null)
        		{
        			String website = (String) checkCell(cell);
        		
        			cbean.setWebsite(website);
        			document.addField("website", website);
        		}
        		
        		cell = row.getCell(7);
        		if(cell!=null)
        		{
        			String publicEmail = (String) checkCell(cell);
        		
        			cbean.setPublicEmail(publicEmail);
        			document.addField("publicEmail", publicEmail);
        		}
        		
        		cell = row.getCell(8);
        		if(cell!=null)
        		{
        			String homeType = (String) checkCell(cell);
        			homeType = StringUtils.normalizeSpace(homeType);
        			if(homeType.equalsIgnoreCase("Care Home without nursing"))
        			{
        				homeType = "chwtn";
        				cbean.setHomeType(homeType);
        			}
        			else if(homeType.equalsIgnoreCase("Care Home with nursing"))
        			{
        				homeType = "chwn";
        				cbean.setHomeType(homeType);
        			}
        			else if(homeType.equalsIgnoreCase("Care Home offering both types of care"))
        			{
        				homeType = "chb";
        				cbean.setHomeType(homeType);
        			}
        				
        			document.addField("homeType", homeType);
        		}
        		List<String> admissions = new ArrayList<String>();
        		String shortStay = null;
        		String formalDayCare = null;
        		String informalDayCare = null;
        		String longStay = null;
        		
        		cell = row.getCell(9);
        		if(cell!=null)
        		{
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
	        			val = StringUtils.normalizeSpace(val);		
	        			if(val.equalsIgnoreCase("YES"))
	        			{
	        				shortStay = "ss";
	        				admissions.add(shortStay);
	        			}
        			}
        		}
        		
        		cell = row.getCell(10);
        		if(cell!=null)
        		{
        			
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
        			val = StringUtils.normalizeSpace(val);		
        			if(val.equalsIgnoreCase("YES"))
        			{
        				formalDayCare = "fd";
        				admissions.add(formalDayCare);
        			}
        			}
        		}
        		
        		cell = row.getCell(11);
        		if(cell!=null)
        		{
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
        			val = StringUtils.normalizeSpace(val);		
        			if(val.equalsIgnoreCase("YES"))
        			{
        				informalDayCare = "ifd";
        				admissions.add(informalDayCare);
        			}	
        			}
        		}
        		
        		cell = row.getCell(12);
        		if(cell!=null)
        		{
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
        			val = StringUtils.normalizeSpace(val);		
        			if(val.equalsIgnoreCase("YES"))
        			{
        				longStay = "ls";
        				admissions.add(longStay);
        			}		
        			}
        		}
        		
        		
        	
        		
        		
        		
        	
        		cbean.setAdmissions(admissions);
        		document.addField("admissions", admissions);
        		
        		String dementia = null;
        		String mentalHealthConditions = null;
        		String learningDisabilities = null;
        		String oldAge = null;
        		String physicalDisabilities = null;
        		String sensoryImpairment = null;
        		String ppAlcoholDependence = null;
        		String ppDrugDependence = null;
        		List<String> careProvided = new ArrayList<String>();
        		
        		cell = row.getCell(13);
        		if(cell!=null)
        		{
        				
        			
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
        			val = StringUtils.normalizeSpace(val);		
        			if(val.equalsIgnoreCase("YES"))
        			{
        				dementia = "de";
        				careProvided.add(dementia);
        			}
        			}
        		}
        		
        		cell = row.getCell(14);
        		if(cell!=null)
        		{
        	
        			
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
        			val = StringUtils.normalizeSpace(val);		
        			if(val.equalsIgnoreCase("YES"))
        			{
        				mentalHealthConditions = "mhc";
        				careProvided.add(mentalHealthConditions );
        			}	
        			}
        		}
        		
        		cell = row.getCell(15);
        		if(cell!=null)
        		{
        	
        			
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
        			val = StringUtils.normalizeSpace(val);		
        			if(val.equalsIgnoreCase("YES"))
        			{
        				learningDisabilities = "ld";
        				careProvided.add(learningDisabilities );
        			}	
        			}
        		}
        		
        		cell = row.getCell(16);
        		if(cell!=null)
        		{
        	
        			
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
        			val = StringUtils.normalizeSpace(val);		
        			if(val.equalsIgnoreCase("YES"))
        			{
        				oldAge = "oa";
        				careProvided.add(oldAge);
        			}	
        			}
        		}
        		
        		cell = row.getCell(16);
        		if(cell!=null)
        		{
        	
        			
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
        			val = StringUtils.normalizeSpace(val);		
        			if(val.equalsIgnoreCase("YES"))
        			{
        				physicalDisabilities = "pd";
        				careProvided.add(physicalDisabilities);
        			}	
        			}
        		}
        		
        		cell = row.getCell(17);
        		if(cell!=null)
        		{
        	
        			
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
        			val = StringUtils.normalizeSpace(val);		
        			if(val.equalsIgnoreCase("YES"))
        			{
        				sensoryImpairment = "si";
        				careProvided.add(sensoryImpairment);
        			}	
        			}
        		}
        		
        		cell = row.getCell(18);
        		if(cell!=null)
        		{
        	
        			
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
        			val = StringUtils.normalizeSpace(val);		
        			if(val.equalsIgnoreCase("YES"))
        			{
        				ppAlcoholDependence = "ppa";
        				careProvided.add(ppAlcoholDependence);
        			}	
        			}
        		}
        		
        		cell = row.getCell(19);
        		if(cell!=null)
        		{
        	
        			
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
        			val = StringUtils.normalizeSpace(val);		
        			if(val.equalsIgnoreCase("YES"))
        			{
        				ppDrugDependence = "ppd";
        				careProvided.add(ppDrugDependence);
        			}	
        			}
        		}
        				
        		
        		
        		       	
        		
        		cbean.setCareProvided(careProvided);
        		document.addField("careProvided", careProvided);
        		
        		String location1 = "";
        		String location2 = "";
        		String location = "";
        		
        		cell = row.getCell(22);
        		if(cell!=null)
        		{
        	
        			
        			Double val = (Double) checkCell(cell);
        				
        			if(val!=null)
        			{
        				location1 = val.toString();
        				location1 = location1+",";
        			}	
        		}
        		
        		cell = row.getCell(23);
        		if(cell!=null)
        		{
        	
        			
        			Double val = (Double) checkCell(cell);
        				
        			if(val!=null)
        			{
        				location2 = val.toString();
        				
        			}	
        		}
        		
        		location = location1+location2;
        		cbean.setLocation(location);
        		document.addField("location", location);
        		
        		cell = row.getCell(25);
        		if(cell!=null)
        		{
        	
        			
        			String val = (String) checkCell(cell);
        			if(StringUtils.isNotBlank(val))
        			{
        			val = StringUtils.normalizeSpace(val);		
        			
        			cbean.setTown(val);
        			document.addField("town", val);
        			}
        		}
        	
        		
        		document.addField("dateOfIndex",new Date());
        		
        		solr.add(document);
        		solr.commit();
        		solr.close();
        		
        		
        		/**
        		 switch (cell.getCellType()) 
                 {
                     case Cell.CELL_TYPE_NUMERIC:
                         System.out.print(cell.getNumericCellValue() + "t");
                         break;
                     case Cell.CELL_TYPE_STRING:
                         System.out.print(cell.getStringCellValue() + "t");
                         break;
                 }
                 */
        	}
        }
        
      //Iterate through each rows one by one
      //  Iterator<Row> rowIterator = sheet.iterator();
      //  while (rowIterator.hasNext()) 
      //  {
       // 	 Row row = rowIterator.next();
       // 	 Iterator<Cell> cellIterator = row.cellIterator();
             
        //     while(cellIterator.hasNext())
       //      {
        //    	 Cell cell = cellIterator.next();
       //     	 cell.
       //      }
       // }
		
	    
	  
	   
	      
	   // try {
	      //  FileOutputStream out =  new FileOutputStream(new File("formulaDemo.xlsx"));
	       // workbook.write(out);
	     //   out.close();
	      //  System.out.println("Excel with foumula cells written successfully");
	          
	 //   } catch (FileNotFoundException e) {
	  //      e.printStackTrace();
	 //   } catch (IOException e) {
	  //      e.printStackTrace();
	 //   }
	}
}