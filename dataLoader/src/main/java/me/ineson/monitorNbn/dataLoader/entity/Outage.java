package me.ineson.monitorNbn.dataLoader.entity;

import java.time.LocalDateTime;

public class Outage {

    private LocalDateTime startTime;

    private LocalDateTime endTime;
    
    private Long startFilePosition;
    
    private Integer numberOfLines;

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

	public Long getStartFilePosition() {
		return startFilePosition;
	}

	public void setStartFilePosition(Long startFilePosition) {
		this.startFilePosition = startFilePosition;
	}

	public Integer getNumberOfLines() {
		return numberOfLines;
	}

	public void setNumberOfLines(Integer numberOfLines) {
		this.numberOfLines = numberOfLines;
    }

}
