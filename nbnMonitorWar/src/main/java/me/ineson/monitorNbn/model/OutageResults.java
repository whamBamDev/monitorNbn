package me.ineson.monitorNbn.model;

import java.time.Duration;
import java.util.List;

import me.ineson.monitorNbn.shared.entity.Outage;

public class OutageResults {

    private Outage outage;

    private Duration duration;

    private List<String>testOutput;

	public Outage getOutage() {
		return outage;
	}

	public void setOutage(Outage outage) {
		this.outage = outage;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public List<String> getTestOutput() {
		return testOutput;
	}

	public void setTestOutput(List<String> testOutput) {
		this.testOutput = testOutput;
	}
}