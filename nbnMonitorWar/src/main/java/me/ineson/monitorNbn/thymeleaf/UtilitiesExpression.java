package me.ineson.monitorNbn.thymeleaf;

import java.time.Duration;
import java.util.Objects;

public class UtilitiesExpression {

	public String format(final Duration duration) {
		if(Objects.isNull(duration)) {
			return "--:--:--";
		}
		
		long seconds = duration.getSeconds();
		
		long minutes = seconds / 60;
		seconds -= minutes * 60;

		long hours = minutes / 60;
		minutes -= hours * 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

}
