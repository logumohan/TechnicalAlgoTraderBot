package com.trading.platform.controller.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;

public class JobsDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("name")
	private String name;

	@JsonProperty("user-name")
	private String userName;

	@JsonProperty("job-type")
	private String jobType;

	@JsonProperty("strategy")
	private String strategy;

	@JsonProperty("trailing-strategy")
	private String trailingStrategy;

	@JsonProperty("trail-by")
	private String trailBy;

	@JsonProperty("atr")
	private int atr;

	@JsonProperty("atr-multiplier")
	private double atrMultiplier;

	@JsonProperty("aggregation-type")
	private String aggregationType;

	@JsonProperty("lot-size")
	private int lotSize;

	@JsonProperty("targets")
	private List<Target> targets;

	@JsonProperty("strike-price-delta")
	private int strikePriceDelta;

	@JsonProperty("max-vix-allowed")
	private int maxVixAllowed;

	@JsonProperty("paper-tradable")
	private boolean paperTradable;

	@JsonProperty("tradable")
	private boolean tradable;

	@JsonProperty("tradable-days")
	private String[] tradableDays;

	public static JobsDto of(Job job) {
		JobsDto jobsDto = new JobsDto();
		jobsDto.setName(job.getName());
		jobsDto.setUserName(job.getUserName());
		jobsDto.setJobType(job.getJobType());
		jobsDto.setStrategy(job.getStrategy());
		jobsDto.setTrailingStrategy(job.getTrailingStrategy());
		jobsDto.setTrailBy(job.getTrailBy());
		jobsDto.setAtr(job.getAtr());
		jobsDto.setAtrMultiplier(job.getAtrMultiplier());
		jobsDto.setAggregationType(job.getAggregationType());
		jobsDto.setLotSize(job.getLotSize());
		jobsDto.setTargets(job.getTargets());
		jobsDto.setStrikePriceDelta(job.getStrikePriceDelta());
		jobsDto.setMaxVixAllowed(job.getMaxVixAllowed());
		jobsDto.setPaperTradable(job.isPaperTradable());
		jobsDto.setTradable(job.isTradable());
		jobsDto.setTradableDays(job.getTradableDays());

		return jobsDto;
	}

	public Job toJob() {
		Job job = new Job();
		job.setName(this.getName());
		job.setUserName(this.getUserName());
		job.setJobType(this.getJobType());
		job.setStrategy(this.getStrategy());
		job.setTrailingStrategy(this.getTrailingStrategy());
		job.setTrailBy(this.getTrailBy());
		job.setAtr(this.getAtr());
		job.setAtrMultiplier(this.getAtrMultiplier());
		job.setAggregationType(this.getAggregationType());
		job.setLotSize(this.getLotSize());
		job.setTargets(this.getTargets());
		job.setStrikePriceDelta(this.getStrikePriceDelta());
		job.setMaxVixAllowed(this.getMaxVixAllowed());
		job.setPaperTradable(this.isPaperTradable());
		job.setTradable(this.isTradable());
		job.setTradableDays(this.getTradableDays());

		return job;
	}

	public String getName() {
		return name;
	}

	public String getUserName() {
		return userName;
	}

	public String getJobType() {
		return jobType;
	}

	public String getStrategy() {
		return strategy;
	}

	public String getTrailingStrategy() {
		return trailingStrategy;
	}

	public String getTrailBy() {
		return trailBy;
	}

	public int getAtr() {
		return atr;
	}

	public double getAtrMultiplier() {
		return atrMultiplier;
	}

	public String getAggregationType() {
		return aggregationType;
	}

	public int getLotSize() {
		return lotSize;
	}

	public List<Target> getTargets() {
		return targets;
	}

	public int getStrikePriceDelta() {
		return strikePriceDelta;
	}

	public int getMaxVixAllowed() {
		return maxVixAllowed;
	}

	public boolean isPaperTradable() {
		return paperTradable;
	}

	public boolean isTradable() {
		return tradable;
	}

	public String[] getTradableDays() {
		return tradableDays;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public void setTrailingStrategy(String trialingStrategy) {
		this.trailingStrategy = trialingStrategy;
	}

	public void setTrailBy(String trailBy) {
		this.trailBy = trailBy;
	}

	public void setAtr(int atr) {
		this.atr = atr;
	}

	public void setAtrMultiplier(double atrMultiplier) {
		this.atrMultiplier = atrMultiplier;
	}

	public void setAggregationType(String aggregationType) {
		this.aggregationType = aggregationType;
	}

	public void setLotSize(int lotSize) {
		this.lotSize = lotSize;
	}

	public void setTargets(List<Target> targets) {
		this.targets = targets;
	}

	public void setStrikePriceDelta(int strikePriceDelta) {
		this.strikePriceDelta = strikePriceDelta;
	}

	public void setMaxVixAllowed(int maxVixAllowed) {
		this.maxVixAllowed = maxVixAllowed;
	}

	public void setPaperTradable(boolean paperTradable) {
		this.paperTradable = paperTradable;
	}

	public void setTradable(boolean tradable) {
		this.tradable = tradable;
	}

	public void setTradableDays(String[] tradableDays) {
		this.tradableDays = tradableDays;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JobsDto [name=");
		builder.append(name);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", jobType=");
		builder.append(jobType);
		builder.append(", strategy=");
		builder.append(strategy);
		builder.append(", trialingStrategy=");
		builder.append(trailingStrategy);
		builder.append(", trailBy=");
		builder.append(trailBy);
		builder.append(", atr=");
		builder.append(atr);
		builder.append(", atrMultiplier=");
		builder.append(atrMultiplier);
		builder.append(", aggregationType=");
		builder.append(aggregationType);
		builder.append(", lotSize=");
		builder.append(lotSize);
		builder.append(", targets=");
		builder.append(targets);
		builder.append(", strikePriceDelta=");
		builder.append(strikePriceDelta);
		builder.append(", maxVixAllowed=");
		builder.append(maxVixAllowed);
		builder.append(", paperTradable=");
		builder.append(paperTradable);
		builder.append(", tradable=");
		builder.append(tradable);
		builder.append(", tradableDays=");
		builder.append(tradableDays);
		builder.append("]");
		return builder.toString();
	}

}
