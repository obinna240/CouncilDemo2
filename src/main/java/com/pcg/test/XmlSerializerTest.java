package com.pcg.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import javax.xml.bind.Unmarshaller;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author oonyimadu
 *
 */
public class XmlSerializerTest 
{
	public static void main(String[] args) 
	{
		
			File file = new File("testXml.xml");
			System.out.println(file.getName());
			
		
			XStream xstream = new XStream();
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
}
