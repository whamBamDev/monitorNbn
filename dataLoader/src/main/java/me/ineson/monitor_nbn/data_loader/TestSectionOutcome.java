package me.ineson.monitor_nbn.data_loader;

import java.time.LocalDateTime;

public class TestSectionOutcome {

    private boolean testSuccessful = true;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
    

	public boolean isTestSuccessful() {
		return testSuccessful;
	}

	public void setTestSuccessful(boolean testSuccessful) {
		this.testSuccessful = testSuccessful;
	}


	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}


	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "TestSectionOutcome [testSuccessful=" + testSuccessful
				+ ", startTime=" + startTime
				+ ", endTime=" + endTime + "]";
	}
}
