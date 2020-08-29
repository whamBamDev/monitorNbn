package me.ineson.monitorNbn.model;

import java.time.Duration;

import me.ineson.monitorNbn.shared.entity.Outage;

public class OutageResults {

    private Outage outage;

    private Duration duration;

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
}