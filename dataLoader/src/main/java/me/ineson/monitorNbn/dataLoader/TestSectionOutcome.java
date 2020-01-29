package me.ineson.monitorNbn.dataLoader;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
