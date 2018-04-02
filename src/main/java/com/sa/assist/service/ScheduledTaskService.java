package com.sa.assist.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcg.db.mongo.dao.ScheduleDAO;
import com.pcg.db.mongo.dao.SystemConfigDAO;
import com.pcg.db.mongo.model.Schedule;
import com.pcg.db.mongo.model.SystemConfig;
import com.pcg.search.index.IndexManager;

/**
 * Job class which will be called periodically by the system
 */
@Service
public class ScheduledTaskService {

	private enum jobIds {
		REINDEX_ALL
	}

	@Autowired
	private SystemConfigDAO systemConfigDAO;
	@Autowired
	private ScheduleDAO scheduleDAO;
	@Autowired
	private IndexManager indexManager;


	private static Log m_log = LogFactory.getLog(ScheduledTaskService.class);

	public void doPendingJobs() {

		m_log.debug("Performing scheduled jobs..");
		int jobsRun = 0;

		// Reset any orphan job schedules (= those still flagged as running
		// after a long time, and so the processing thread may have died)
		List<Schedule> runningJobsList = scheduleDAO.findRunningJobs();

		for (Schedule schedule : runningJobsList) {
			DateTime lastRunTime = new DateTime(schedule.getLastRunTime());

			if (lastRunTime.isBefore(new DateTime().minus(Period.hours(1)))) {
				schedule.setRunning(false);
				scheduleDAO.save(schedule);
			}
		}

		Long contextId = null;
		SystemConfig config = systemConfigDAO.getDefaultSystemConfig();
		String contextRoot = "";//config.getContextRoot();

		try {
			// Run any pending jobs
			List<Schedule> pendingJobs = scheduleDAO.findPendingJobs(contextId);

			for (Schedule schedule : pendingJobs) {
				if (schedule.getId().equals(jobIds.REINDEX_ALL.name())) {
					reIndexAll();
					jobsRun++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			m_log.error(e);
		}

		m_log.debug(jobsRun + " scheduled jobs completed");
	}

	private void reIndexAll() {
		Schedule pendingJob = scheduleDAO.findPendingJob(jobIds.REINDEX_ALL.name(), true);

		try {
			if (pendingJob != null) {
				indexManager.indexAllCms();
				scheduleDAO.flagJobComplete(jobIds.REINDEX_ALL.name(), true);
				m_log.info("All data reindexed successfully.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			m_log.error(e);
		}
	}
}
