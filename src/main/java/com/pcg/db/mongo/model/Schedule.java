package com.pcg.db.mongo.model;

import java.util.Date;

/**
 * Represents a scheduled task 
 */
public class Schedule {
	
	private String id;
	private boolean running;
	private boolean enabled;
	private Date nextRunTime;
	private Date lastRunTime;
	private Long frequency;
	private String frequencyUnit;
	private Long contextId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Date getNextRunTime() {
		return nextRunTime;
	}
	public void setNextRunTime(Date nextRunTime) {
		this.nextRunTime = nextRunTime;
	}
	public Date getLastRunTime() {
		return lastRunTime;
	}
	public void setLastRunTime(Date lastRunTime) {
		this.lastRunTime = lastRunTime;
	}
	public Long getFrequency() {
		return frequency;
	}
	public void setFrequency(Long frequency) {
		this.frequency = frequency;
	}
	public String getFrequencyUnit() {
		return frequencyUnit;
	}
	public void setFrequencyUnit(String frequencyUnit) {
		this.frequencyUnit = frequencyUnit;
	}
	public Long getContextId() {
		return contextId;
	}
	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}
	
	
	
	
}
