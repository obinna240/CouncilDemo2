package com.pcg.db.mongo.dao;

import java.util.List;

import com.pcg.db.mongo.model.Schedule;

public interface ScheduleDAOCustom extends CustomDAO<Schedule, String> {
	
	public List<Schedule> findPendingJobs(Long contextId); 
	public List<Schedule> findPendingJobs(); 
	public List<Schedule> findRunningJobs();
	public Schedule findPendingJob(String jobId, boolean flagAsRunning);
	public void flagJobComplete(String jobId, boolean onDemand);
	public void scheduleJob(String jobId);
}
