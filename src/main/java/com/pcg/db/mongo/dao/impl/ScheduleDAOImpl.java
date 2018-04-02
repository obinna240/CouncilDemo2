package com.pcg.db.mongo.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.pcg.db.mongo.dao.ScheduleDAOCustom;
import com.pcg.db.mongo.model.Schedule;

/**
 * DAO for managing scheduled tasks in Assist  
 */
public class ScheduleDAOImpl extends CustomDAOImpl<Schedule, String> implements ScheduleDAOCustom {
	
	@Autowired @Qualifier("mongoTemplate") MongoOperations mongoOps;
	private static Log m_log = LogFactory.getLog(ScheduleDAOImpl.class);
	
	public ScheduleDAOImpl() {
        super(Schedule.class);
    }
	
	@Override
	public synchronized List<Schedule> findPendingJobs(Long contextId) {

		List<Schedule> foundList = new ArrayList<Schedule>();
		Calendar now = Calendar.getInstance();
		Query query;
		if (contextId != null) {
			query = new Query();
			query.addCriteria(
				Criteria.where("running").is(false).and("enabled").is(true).and("nextRunTime").lt(now.getTime())
				.orOperator(
						where("contextId").is(contextId), 
						where("contextId").exists(false))
				);
		} else {
			query = new Query(where("running").is(false).and("enabled").is(true).and("nextRunTime").lt(now.getTime()));
		}

		try {
			foundList = mongoOps.find(query, Schedule.class);
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
		
		return foundList;
	}
	
	@Override
	public synchronized List<Schedule> findPendingJobs() {

		List<Schedule> foundList = new ArrayList<Schedule>();
		Calendar now = Calendar.getInstance();
		Query query = new Query(where("running").is(false).and("enabled").is(true).and("nextRunTime").lt(now.getTime())); 

		try {
			foundList = mongoOps.find(query, Schedule.class);
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
		
		return foundList;
	}
	
	@Override
	public synchronized List<Schedule> findRunningJobs() {

		List<Schedule> foundList = new ArrayList<Schedule>();
		
		Query query = new Query(where("running").is(true)); 

		try {
			foundList = mongoOps.find(query, Schedule.class);
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
		
		return foundList;
	}

	@Override
	public synchronized Schedule findPendingJob(String jobId, boolean flagAsRunning) {

		// code to solve multi context issue
		try {
		    Thread.sleep(5000);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		Schedule foundSchedule = null;
		Calendar now = Calendar.getInstance();
		Query query = new Query(where("_id").is(jobId).and("running").is(false).and("enabled").is(true).and("nextRunTime").lt(now.getTime())); 
		foundSchedule = mongoOps.findOne(query, Schedule.class);
		
		try {
			if (flagAsRunning && foundSchedule != null) {
				foundSchedule.setRunning(true);
				mongoOps.save(foundSchedule);
			}
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
		
		return foundSchedule;
	}

	@Override
	public synchronized void flagJobComplete(String jobId, boolean onDemand) {
		Query query = new Query(where("_id").is(jobId));
		
		try {
			Schedule schedule = mongoOps.findOne(query, Schedule.class);
			updateRunTimes(schedule, onDemand);
			schedule.setRunning(false);
			mongoOps.save(schedule);
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
	}
	
    /**
     * Set the next runtime according to the schedule in the db 
     */
    private void updateRunTimes(Schedule schedule, boolean onDemand){
		
    	Long frequency = schedule.getFrequency();
		String unit = schedule.getFrequencyUnit();
		
		Calendar calNow = Calendar.getInstance();
	
		Calendar calNext = Calendar.getInstance();
		if (schedule.getNextRunTime() != null){
			calNext.setTime(schedule.getNextRunTime());
		}
		
		if (!onDemand) {		
			Integer calPeriod = null;
	
			if ("MINUTE".equalsIgnoreCase(unit)) {
				calPeriod = Calendar.MINUTE;
			} else if ("HOUR".equalsIgnoreCase(unit)) {
				calPeriod = Calendar.HOUR;
			} else if ("DAY".equalsIgnoreCase(unit)) {
				calPeriod = Calendar.DAY_OF_MONTH;
			} else if ("WEEK".equalsIgnoreCase(unit)) {
				calPeriod = Calendar.WEEK_OF_MONTH;
			} else if ("MONTH".equalsIgnoreCase(unit)) {
				calPeriod = Calendar.MONTH;
			} 
			else {
				m_log.error("Cannot parse next runtime settings. Defaulting to 24 hours.");
				calPeriod = Calendar.HOUR;
				frequency = 24L;
			}
			
			//Next runtime may have already passed, so we may need to increment it more than once 
			while (calNext.before(calNow)){
				calNext.add(calPeriod, frequency.intValue());
			}
			schedule.setNextRunTime(calNext.getTime()); 
		}else{
			//nextRuntime is null for onDemand jobs. 
			//nextRuntime will be set in calling code when the job is scheduled with 'scheduleJob' method.
			schedule.setNextRunTime(null); 
		}
		schedule.setLastRunTime(calNow.getTime());
		
    }

    
	@Override
	public synchronized void scheduleJob(String jobId) {
		Schedule sc = findOnDemandJob(jobId);
		if (sc!=null){
			Calendar calNext = Calendar.getInstance();		
			sc.setNextRunTime(calNext.getTime());
			mongoOps.save(sc);
		}
		
	}
	
	private Schedule findOnDemandJob(String jobId) {

		Schedule sc = null;
		
		Query query = new Query(where("_id").is(jobId).and("running").is(false).and("enabled").is(true).and("nextRunTime").is(null)); 

		try {
			sc = mongoOps.findOne(query, Schedule.class);
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}
		
		return sc;
	}

}
