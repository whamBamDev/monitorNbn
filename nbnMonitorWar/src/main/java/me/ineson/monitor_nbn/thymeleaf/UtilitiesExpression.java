package me.ineson.monitor_nbn.thymeleaf;

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

	public String outputIterable(final Iterable<?>iterable) {
		return outputIterable(iterable, "\n");
	}

	public String outputIterable(final Iterable<?>iterable, String lineEnd) {
		if(Objects.isNull(iterable)) {
			return "";
		}
		
		StringBuilder stringBuilder= new StringBuilder();
		boolean firstObject = true;

		for (Object object : iterable) {
			if(!firstObject && Objects.nonNull(lineEnd)) {
				stringBuilder.append(lineEnd);
			}
			firstObject = false;

			stringBuilder.append(object);
		}
		
		return stringBuilder.toString();
	}
	
}
