package com.trading.platform.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.controller.dto.JobsDto;
import com.trading.platform.persistence.JobsReadOnlyRepositoryImpl;
import com.trading.platform.persistence.JobsRepository;
import com.trading.platform.persistence.entity.Job;

import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotNull;

@RestController
public class JobsController {

	private static final Logger LOGGER = LogManager.getLogger(JobsController.class);

	@Autowired
	private JobsReadOnlyRepositoryImpl jobsRORepository;

	@Autowired
	private JobsRepository jobsRepository;

	@PostMapping("/jobs/create")
	@LogExecutionTime
	public ResponseEntity<String> createJob(@RequestBody @NotNull List<JobsDto> jobsList) {
		if (!jobsList.isEmpty()) {
			LOGGER.info("Attempting to save the jobs - {}", jobsList);
			jobsList.stream().map(JobsDto::toJob).forEach(job -> {
				jobsRepository.save(job);
				LOGGER.info("Job saved, job - {}", job);
			});
		} else {
			LOGGER.info("Job list is received as empty!");
			return new ResponseEntity<>("Job list is empty!", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Jobs created successfully!", HttpStatus.OK);
	}

	@PostMapping("/jobs/update")
	@LogExecutionTime
	public ResponseEntity<String> updateJob(@RequestBody @NotNull List<JobsDto> jobsList) {
		if (!jobsList.isEmpty()) {
			LOGGER.info("Attempting to update the jobs - {}", jobsList);
			jobsList.stream().map(JobsDto::toJob).forEach(job -> {
				Job updatedJob = jobsRORepository.findById(job.getName(), job.getUserName());
				updatedJob.setUserName(job.getUserName());
				updatedJob.setJobType(job.getJobType());
				updatedJob.setStrategy(job.getStrategy());
				updatedJob.setTrailingStrategy(job.getTrailingStrategy());
				updatedJob.setTrailBy(job.getTrailBy());
				updatedJob.setAtr(job.getAtr());
				updatedJob.setAtrMultiplier(job.getAtrMultiplier());
				updatedJob.setAggregationType(job.getAggregationType());
				updatedJob.setTargets(job.getTargets());
				updatedJob.setStrikePriceDelta(job.getStrikePriceDelta());
				updatedJob.setMaxVixAllowed(job.getMaxVixAllowed());
				updatedJob.setPaperTradable(job.isPaperTradable());
				updatedJob.setTradable(job.isTradable());
				updatedJob.setTradableDays(job.getTradableDays());

				jobsRepository.save(updatedJob);
				LOGGER.info("Job updated, job - {}", updatedJob);
			});
		} else {
			LOGGER.info("Job list is received as empty!");
			return new ResponseEntity<>("Job list is empty!", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Jobs updated successfully!", HttpStatus.OK);
	}

	@GetMapping({ "/jobs", "/jobs/{strategy}" })
	@LogExecutionTime
	public ResponseEntity<List<JobsDto>> getJobs(
			@PathVariable(name = "strategy", required = false) Optional<String> strategy) {
		List<Job> jobList;
		if (strategy.isPresent()) {
			LOGGER.info("Querying all the jobs for the strategy - {}", strategy.get());
			jobList = jobsRORepository.findJobsByStrategy(strategy.get());
		} else {
			LOGGER.info("Querying all the jobs");
			jobList = jobsRORepository.findJobs();
		}

		return new ResponseEntity<>(jobList.stream().map(JobsDto::of).collect(Collectors.toList()), HttpStatus.OK);
	}

	@GetMapping({ "/jobs-by-type/{job-type}", "/jobs-by-type/{job-type}/{strategy}" })
	@LogExecutionTime
	public ResponseEntity<List<JobsDto>> getJobsByType(
			@PathVariable(name = "job-type", required = true) String jobType,
			@PathVariable(name = "strategy", required = false) Optional<String> strategy) {
		List<Job> jobList;
		if (strategy.isPresent()) {
			LOGGER.info("Querying jobs for the type - {} and strategy - {}", jobType, strategy.get());
			jobList = jobsRORepository.findJobsBy(jobType, strategy.get());
		} else {
			LOGGER.info("Querying all the jobs for type - {}", jobType);
			jobList = jobsRORepository.findJobsByType(jobType);
		}

		return new ResponseEntity<>(jobList.stream().map(JobsDto::of).collect(Collectors.toList()), HttpStatus.OK);
	}

	@DeleteMapping({ "/jobs/delete/{job-name}", "/jobs/delete/{job-name}/{user}" })
	@LogExecutionTime
	public ResponseEntity<String> deleteJob(@PathVariable(name = "job-name", required = true) String jobName,
			@PathVariable(name = "user", required = false) Optional<String> user) {
		if (user.isPresent()) {
			try {
				Job job = jobsRORepository.findById(jobName, user.get());
				jobsRepository.delete(job);
				LOGGER.info("Deleted job - {}", job);
			} catch (NoResultException e) {
				LOGGER.error("Job with name - {} and user - {} is not found!", jobName, user, e);
				return new ResponseEntity<>("Job not found", HttpStatus.BAD_REQUEST);
			}
		} else {
			List<Job> jobsList = jobsRORepository.findByName(jobName);
			if (jobsList.isEmpty()) {
				return new ResponseEntity<>("Job not found", HttpStatus.BAD_REQUEST);
			}
			jobsList.stream().forEach((Job job) -> {
				jobsRepository.delete(job);
				LOGGER.info("Deleted job - {}", job);
			});
		}

		return new ResponseEntity<>("Jobs deleted", HttpStatus.OK);
	}

	@DeleteMapping("/jobs/delete")
	@LogExecutionTime
	public ResponseEntity<String> deleteJob(@RequestBody @NotNull List<JobsDto> jobsList) {
		jobsList.stream().map(JobsDto::toJob).forEach((Job job) -> {
			jobsRepository.delete(job);
			LOGGER.info("Deleted the job with name - {} and user -{}, {}", job.getName(), job.getUserName(), job);
		});
		return new ResponseEntity<>("Jobs deleted", HttpStatus.OK);
	}

}
