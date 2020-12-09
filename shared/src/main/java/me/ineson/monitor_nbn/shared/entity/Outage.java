package me.ineson.monitor_nbn.shared.entity;

import java.time.LocalDateTime;

public class Outage extends BaseEntity {

    private LocalDateTime startTime;

	private LocalDateTime endTime;
    
    private Long startFilePosition;
    
    private Integer numberOfLines;

    private Boolean inProgress;

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

	
    public Boolean getInProgress() {
		return inProgress;
	}

	public void setInProgress(Boolean inProgress) {
		this.inProgress = inProgress;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Outage [id=").append(getId())
				.append(", startTime=").append(startTime)
				.append(", endTime=").append(endTime)
				.append(", startFilePosition=").append(startFilePosition)
				.append(", numberOfLines=").append(numberOfLines)
				.append(", inProgress=").append(inProgress)
				.append("]");
		return builder.toString();
	}
	
}
