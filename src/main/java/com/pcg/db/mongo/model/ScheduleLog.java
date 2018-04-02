package com.pcg.db.mongo.model;

import java.util.Date;

/**
 * Represents a scheduled task 
 */
public class ScheduleLog {
	
	private Long id;
	private String scheduleId;
	private Date lastRunTime;
	private String outcome;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getScheduleId() {
		return scheduleId;
	}
	
	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}
	
	public Date getLastRunTime() {
		return lastRunTime;
	}
	
	public void setLastRunTime(Date lastRunTime) {
		this.lastRunTime = lastRunTime;
	}
	
	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
}
