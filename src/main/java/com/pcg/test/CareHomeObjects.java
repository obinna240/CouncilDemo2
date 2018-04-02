package com.pcg.test;

import java.util.List;



import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("root")
public class CareHomeObjects 
{
	
	List<CareHomeObject> careHomeObject;

	public List<CareHomeObject> getCareHomeObject() {
		return careHomeObject;
	}
	
	
	public void setCareHomeObject(List<CareHomeObject> careHomeObject) {
		this.careHomeObject = careHomeObject;
	}
}
