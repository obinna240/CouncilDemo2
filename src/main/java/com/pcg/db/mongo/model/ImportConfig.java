package com.pcg.db.mongo.model;

public class ImportConfig {
	
	private String id;
	
	private String csvRoot;
	private String csvProviderFile;
	private String csvProviderTimesFile;
	private String csvProviderSchoolsFile;
	
	private String importDBDriverClass;
	private String importDBUrl;
	private String importDBUserName;
	private String importDBPassword;
	
	private String eMailTo;
	private String eMailTitle;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCsvRoot() {
		return csvRoot;
	}
	public void setCsvRoot(String csvRoot) {
		this.csvRoot = csvRoot;
	}
	public String getCsvProviderFile() {
		return csvProviderFile;
	}
	public void setCsvProviderFile(String csvProviderFile) {
		this.csvProviderFile = csvProviderFile;
	}
	public String getCsvProviderTimesFile() {
		return csvProviderTimesFile;
	}
	public void setCsvProviderTimesFile(String csvProviderTimesFile) {
		this.csvProviderTimesFile = csvProviderTimesFile;
	}
	public String getCsvProviderSchoolsFile() {
		return csvProviderSchoolsFile;
	}
	public void setCsvProviderSchoolsFile(String csvProviderSchoolsFile) {
		this.csvProviderSchoolsFile = csvProviderSchoolsFile;
	}
	public String getImportDBDriverClass() {
		return importDBDriverClass;
	}
	public void setImportDBDriverClass(String importDBDriverClass) {
		this.importDBDriverClass = importDBDriverClass;
	}
	public String getImportDBUrl() {
		return importDBUrl;
	}
	public void setImportDBUrl(String importDBUrl) {
		this.importDBUrl = importDBUrl;
	}
	public String getImportDBUserName() {
		return importDBUserName;
	}
	public void setImportDBUserName(String importDBUserName) {
		this.importDBUserName = importDBUserName;
	}
	public String getImportDBPassword() {
		return importDBPassword;
	}
	public void setImportDBPassword(String importDBPassword) {
		this.importDBPassword = importDBPassword;
	}
	public String getEMailTo() {
		return eMailTo;
	}
	public void setEMailTo(String eMailTo) {
		this.eMailTo = eMailTo;
	}
	public String getEMailTitle() {
		return eMailTitle;
	}
	public void setEMailTitle(String eMailTitle) {
		this.eMailTitle = eMailTitle;
	}
}
