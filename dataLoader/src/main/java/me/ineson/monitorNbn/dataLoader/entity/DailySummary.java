package me.ineson.monitorNbn.dataLoader.entity;

import java.time.LocalDate;


public final class DailySummary extends BaseEntity {
	
	private LocalDate date;
    
	private String datafile;
    
    private Integer outageCount;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDatafile() {
		return datafile;
	}

	public void setDatafile(String datafile) {
		this.datafile = datafile;
	}

	public Integer getOutageCount() {
		return outageCount;
	}

	public void setOutageCount(Integer outageCount) {
		this.outageCount = outageCount;
	}
    
    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DailySummary [id=").append(getId())
			.append(", date=").append(date)
			.append(", datafile=").append(datafile)
			.append(", outageCount=").append(outageCount)
			.append("]");
		return builder.toString();
	}

}
