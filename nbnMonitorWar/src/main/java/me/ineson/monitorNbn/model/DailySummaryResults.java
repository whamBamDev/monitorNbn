package me.ineson.monitorNbn.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import me.ineson.monitorNbn.shared.entity.DailySummary;

public class DailySummaryResults {
	
    private LocalDate startDate;

    private LocalDate endDate;

    private List<DailySummary>results;

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public List<DailySummary> getResults() {
		return results;
	}

	public void setResults(List<DailySummary> results) {
		this.results = results;
	} 
}
