/**
 * 
 */
package me.ineson.monitorNbn.dataLoader;

import java.util.List;
import java.util.Objects;

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
	 * @param strings
	 * @param prefix
	 * @return
	 */
	public static boolean hasStringStartingWithPrefix(List<String> strings, String prefix) {
		return Objects.nonNull(getStringStartingWithPrefix(strings, prefix));
	}

	/**
	 * @param strings
	 * @param preFix
	 * @return
	 */
	public static String getStringStartingWithPrefix(List<String> strings, String preFix) {
		if( StringUtils.isBlank(preFix) || CollectionUtils.isEmpty(strings)) {
			return null;
		}

		return strings.stream().filter(line -> line != null && line.startsWith(preFix)).findAny().orElse(null); 
	}
	
	/**
	 * @param searchString
	 * @param strings
	 * @return
	 */
	public static boolean hasStringContainsIgnoreCase(List<String> strings, String searchString) {
		if (StringUtils.isBlank(searchString) || CollectionUtils.isEmpty(strings)) {
			return false;
		}

		String matched = strings.stream()
				.filter(line -> line != null && StringUtils.indexOfIgnoreCase(line, searchString) != StringUtils.INDEX_NOT_FOUND)
				.findAny()
				.orElse(null);

		return StringUtils.isNotBlank(matched);
	}

}
