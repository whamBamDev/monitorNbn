package me.ineson.monitorNbn.dataLoader.entity;

import java.time.LocalDateTime;

public class Outage extends BaseEntity {

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

	
    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Outage [id=").append(getId())
				.append(", startTime=").append(startTime)
				.append(", endTime=").append(endTime)
				.append(", startFilePosition=").append(startFilePosition)
				.append(", numberOfLines=").append(numberOfLines)
				.append("]");
		return builder.toString();
	}
	
}
