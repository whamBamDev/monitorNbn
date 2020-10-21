package me.ineson.monitorNbn.model;

import java.time.LocalDate;

import me.ineson.monitorNbn.shared.entity.Outage;

public class LastOutage {

	private LocalDate date;
	
	private Integer outageCount;

    private Integer failedTestCount;

    private Integer testCount;

	private Outage outage;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Integer getOutageCount() {
		return outageCount;
	}

	public void setOutageCount(Integer outageCount) {
		this.outageCount = outageCount;
	}

	public Integer getFailedTestCount() {
		return failedTestCount;
	}

	public void setFailedTestCount(Integer failedTestCount) {
		this.failedTestCount = failedTestCount;
	}

	public Integer getTestCount() {
		return testCount;
	}

	public void setTestCount(Integer testCount) {
		this.testCount = testCount;
	}

	public Outage getOutage() {
		return outage;
	}

	public void setOutage(Outage outage) {
		this.outage = outage;
	}
}