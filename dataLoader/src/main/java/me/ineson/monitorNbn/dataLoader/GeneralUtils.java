/**
 * 
 */
package me.ineson.monitorNbn.dataLoader;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Set of helpful utilities.
 * 
 * @author Peter
 *
 */
public final class GeneralUtils {

	/**
	 * @param test
	 * @param matchLines
	 * @return
	 */
	public static boolean checkStartStringExists(String test, List<String>matchLines) {
		if( StringUtils.isBlank(test) || CollectionUtils.isEmpty(matchLines)) {
			return false;
		}

		String matched = matchLines.stream().filter(line -> line != null && line.startsWith(test)).findAny().orElse(null); 

		return StringUtils.isNotBlank(matched);
	}

	/**
	 * @param test
	 * @param matchLines
	 * @return
	 */
	public static boolean checkStringContainsIgnoreCase(String test, List<String> matchLines) {
		if (StringUtils.isBlank(test) || CollectionUtils.isEmpty(matchLines)) {
			return false;
		}

		String matched = matchLines.stream()
				.filter(line -> line != null && StringUtils.indexOfIgnoreCase(line, test) != StringUtils.INDEX_NOT_FOUND)
				.findAny().orElse(null);

		return StringUtils.isNotBlank(matched);
	}

}
