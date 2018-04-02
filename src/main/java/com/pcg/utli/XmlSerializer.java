package com.pcg.utli;

import java.io.File;

import com.pcg.test.CareHomeObject;
import com.pcg.test.CareHomeObjects;
import com.thoughtworks.xstream.XStream;

/**
 * See sample testXml
 * Converts xml to java objects
 * @author oonyimadu
 *
 */
public class XmlSerializer
{
	public static XStream initialize()
	{
		XStream xstream = new XStream();
		xstream.alias("CareHomeObjects", CareHomeObjects.class); 
		xstream.alias("careHomeObject", CareHomeObject.class); 
		xstream.useAttributeFor(CareHomeObject.class, "id"); 
		xstream.addImplicitCollection(CareHomeObjects.class, "careHomeObject"); 
		xstream.addImplicitCollection(CareHomeObject.class,"careProvided",String.class);
		xstream.addImplicitCollection(CareHomeObject.class,"admissions",String.class);
		xstream.addImplicitCollection(CareHomeObject.class,"homeType",String.class);
		return xstream;
	}
	
/**
	public static CareHomeObject serializeXml(String string)
	{
		XStream xstream = initialize();
	
		//if (StringUtils.)
		File file = new File("testXml.xml");
		System.out.println(file.getName());
		
	//	StringUtils.substringBefore();
		xstream = new XStream();
		xstream.alias("CareHomeObjects", CareHomeObjects.class); // this will remove the Country class package name
		xstream.alias("careHomeObject", CareHomeObject.class); // this will remove the State class package name
		xstream.useAttributeFor(CareHomeObject.class, "id"); 
		xstream.addImplicitCollection(CareHomeObjects.class, "careHomeObject"); // don't want all states inside .
		xstream.addImplicitCollection(CareHomeObject.class,"careProvided",String.class);
		xstream.addImplicitCollection(CareHomeObject.class,"admissions",String.class);
		xstream.addImplicitCollection(CareHomeObject.class,"homeType",String.class);
		
		CareHomeObjects  deSerializedCountry = (CareHomeObjects)xstream.fromXML(file);
		
		System.out.println(deSerializedCountry.getCareHomeObject().size());
	}
*/
	/**
	 * 
	 * @param xml string
	 * @return CareHomeObjects 
	 */
	public static CareHomeObjects serializeXml(String xml)
	{
		XStream xstream = new XStream();
		xstream.alias("CareHomeObjects", CareHomeObjects.class); 
		xstream.alias("careHomeObject", CareHomeObject.class); 
		xstream.useAttributeFor(CareHomeObject.class, "id"); 
		xstream.addImplicitCollection(CareHomeObjects.class, "careHomeObject"); 
		xstream.addImplicitCollection(CareHomeObject.class,"careProvided",String.class);
		xstream.addImplicitCollection(CareHomeObject.class,"admissions",String.class);
		xstream.addImplicitCollection(CareHomeObject.class,"homeType",String.class);
		
		CareHomeObjects  careHomeObject = (CareHomeObjects)xstream.fromXML(xml);
		return careHomeObject;
	}
	
	
	//public static CareHomeObjects serializeXml(File file)
	//{
		
	//}
	
	public static void main(String[] args)
	{


	}
}
