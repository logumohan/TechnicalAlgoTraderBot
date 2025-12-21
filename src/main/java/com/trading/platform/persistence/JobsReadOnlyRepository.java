package com.trading.platform.persistence;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import com.trading.platform.persistence.entity.Job;

@NoRepositoryBean
public interface JobsReadOnlyRepository extends Repository<Job, String> {

	Job findById(String name, String userName);
	
	List<Job> findByName(String name);
	
	List<Job> findJobs();
	
	List<Job> findJobsByType(String jobType);
	
	List<Job> findJobsByStrategy(String strategy);
	
	List<Job> findJobsBy(String jobType, String strategy);
	
}
