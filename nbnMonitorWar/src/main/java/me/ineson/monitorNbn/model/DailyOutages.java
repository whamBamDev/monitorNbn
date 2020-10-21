package me.ineson.monitorNbn.model;

import java.time.LocalDate;

import me.ineson.monitorNbn.shared.entity.Outage;

public class DailyOutages {

	private LocalDate date;
	
	private Integer outageCount;
	
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

	public Outage getOutage() {
		return outage;
	}

	public void setOutage(Outage outage) {
		this.outage = outage;
	}
}