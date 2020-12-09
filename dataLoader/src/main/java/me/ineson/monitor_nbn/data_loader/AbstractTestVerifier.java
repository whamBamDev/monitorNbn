/**
 * 
 */
package me.ineson.monitor_nbn.data_loader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ineson.monitor_nbn.shared.io.TestSection;

/**
 * @author peter
 *
 */
public abstract class AbstractTestVerifier {

    private static final Logger LOG = LogManager.getLogger(AbstractTestVerifier.class);
    
    private static final Map<String, AbstractTestVerifier> VERIFIERS = createVerifiers();

    // echo "==== start(001): 03-12-2019 00:55:29" 
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private static final String DATETIME_FORMAT = "\\d{1,2}-\\d{1,2}-\\d{2,4} \\d{1,2}\\:\\d{1,2}\\:\\d{1,2}";

    protected static final Pattern START_LINE = Pattern.compile("==== start\\((\\d*)\\): (" + DATETIME_FORMAT + ")");

    protected static final int START_LINE_DATE_POS = 2;

    protected static final Pattern END_LINE = Pattern.compile("==== end: (" + DATETIME_FORMAT + ")");

    protected static final int END_LINE_DATE_POS = 1;

    protected static final String START_TEST = "== ";

    protected static final String END_TEST = "Exit Code:";

    protected static final String TEST_SUCCESS = "Exit Code: 0";

    protected static final String TEST_TRACEROUTE_SUCCESS = "<syn,ack>";

    protected static final String TEST_PING_SUCCESS = " 0% packet loss";

    protected static final String TEST_TRACEROUTE = "== traceroute";

    protected static final String TEST_PING = "== ping";

    protected static final String TEST_MODEN_STATUS = "== Modem Status";

    protected static final String TEST_MODEN_STATUS_LINE = "Modem Status:";

    private static Pattern MODEM_STATUS_LINE = Pattern.compile("Modem Status\\: Connection Status \\[(\\w*)\\], Access Type \\[(\\w*)\\], Connection Type \\[(\\w*)\\], Mode \\[(\\w*)\\], Leds \\(Modem LEDs: Online \\[(\\w*)\\], WAN/DSL \\[(\\w*)\\], Mobile Mode \\[(\\w*)\\], Mobile Signal \\[(\\w*)\\], Phone \\[(\\w*)\\]\\)");

    private static Map<String, AbstractTestVerifier> createVerifiers() {
        Map<String, AbstractTestVerifier> verifiers = new HashMap<String, AbstractTestVerifier>();
        verifiers.put("001", new TestVerifierFormatVersion001());
        return Collections.unmodifiableMap(verifiers);
    }

	public static AbstractTestVerifier getTestVerifier(List<String>lines) {
		if( CollectionUtils.isEmpty(lines)) {
			throw new IllegalStateException("Failed to get the version of test, no data lines");
		}
		
		String firstLine = lines.get(0);
		if( StringUtils.isBlank(firstLine)) {
			throw new IllegalStateException("Failed to get the version of test, the first line was blank");
		}

		Matcher versionMatcher = START_LINE.matcher(firstLine);
		if( ! versionMatcher.matches()) {
			throw new IllegalStateException("Failed to parse line for getting the version: " + firstLine);
		}
		
		String version = versionMatcher.group(1);
		LOG.debug("Got version {} from line: {}", version, firstLine);
		
		AbstractTestVerifier verifier = VERIFIERS.get(version);
		if( Objects.isNull(verifier)) {
			throw new IllegalStateException("No test verifier found, version of " + version + " is not supported");
		}

		return verifier;
	}

	/**
	 * @param testSection
	 * @return
	 */
	public abstract TestSectionOutcome getTestOutcome( TestSection testSection);

	/**
	 * @param matcher
	 * @param dateField
	 * @return
	 */
	protected static LocalDateTime parseDate(Matcher matcher, int dateField) {
		String stringDateTime = matcher.group(dateField);
		if( StringUtils.isNotBlank(stringDateTime)) {
			return LocalDateTime.parse(stringDateTime, DATETIME_FORMATTER);
		}
		
		return null;
	}

	/**
	 * @param modemOutput
	 * @return
	 */
	protected static boolean connectedViaNbn(String modemOutput) {
		if (Objects.isNull(modemOutput)) {
			throw new IllegalStateException("Failed to parse modem status line as is it is null");
		}
		
		Matcher matcher = MODEM_STATUS_LINE.matcher(modemOutput);
		if (!matcher.matches()) {
			throw new IllegalStateException("Failed to parse modem status line: " + modemOutput);
		}

		String connectionStatus = matcher.group(1);
		String connectionType = matcher.group(3);
		LOG.debug("Modem status read, connectionStatus = {}, connectionType = {}", connectionStatus, connectionType);

		return "Connected".equals(connectionStatus) && "IPoE".equals(connectionType);
	}

}
