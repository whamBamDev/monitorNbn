package me.ineson.monitor_nbn.shared.io;

import java.util.List;
import java.util.Objects;

public class TestSection {

	private long filePosition;

	private int firstLineNumber;

	private int lastLineNumber;

	private List<String> lines;

	public long getFilePosition() {
		return filePosition;
	}

	public void setFilePosition(long filePosition) {
		this.filePosition = filePosition;
	}

	public int getFirstLineNumber() {
		return firstLineNumber;
	}

	public void setFirstLineNumber(int firstLineNumber) {
		this.firstLineNumber = firstLineNumber;
	}

	public int getLastLineNumber() {
		return lastLineNumber;
	}

	public void setLastLineNumber(int lastLineNumber) {
		this.lastLineNumber = lastLineNumber;
	}

	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	@Override
	public String toString() {
		return "TestSection [filePosition=" + filePosition
				+ ", firstLineNumber=" + firstLineNumber
				+ ", lastLineNumber=" + lastLineNumber
				+ ", sectionLineCount=" + (Objects.nonNull(lines) ? String.valueOf( lines.size()) : "null") + "]";
	}
}
