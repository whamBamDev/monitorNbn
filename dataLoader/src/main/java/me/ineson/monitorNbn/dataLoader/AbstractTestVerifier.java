/**
 * 
 */
package me.ineson.monitorNbn.dataLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.synth.Region;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	protected static final int START_LINE_DATE_POS = 1;

	protected static final Pattern END_LINE = Pattern.compile("==== end: (" + DATETIME_FORMAT + ")");

	protected static final int END_LINE_DATE_POS = 0;

	protected static final String START_TEST = "== ";

	protected static final String END_TEST = "Exit Code:";

	protected static final String TEST_SUCCESS = "Exit Code: 0";

	protected static final String TEST_TRACEROUTE_SUCCESS = "<syn,ack>";

	protected static final String TEST_PING_SUCCESS = "0% packet loss";

	protected static final String TEST_TRACEROUTE_IP = "== traceroute - IP";
	
	protected static final String TEST_TRACEROUTE_DNS = "== traceroute - DNS Lookup";

	protected static final String TEST_PING = "== ping - DNS Lookup";
	
	protected static final String TEST_MODEN_STATUS = "== Modem Status";
	
//	== traceroute - direct IP: 03-12-2019 00:56:01
//  == traceroute - DNS Lookup: 03-12-2019 00:56:03
//  == ping - direct IP: 03-12-2019 00:56:03
//  == ping - DNS Lookup: 03-12-2019 00:56:03
//			Modem URL: http://192.168.0.1/
//			Modem Status: Connection Status [Connected], Access Type [ETH], Connection Type [IPoE], Mode [SIP], Leds (Modem LEDs: Online [true], WAN/DSL [true], Mobile Mode [false], Mobile Signal [true], Phone [true])
//			Exit Code: 0

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
	
	protected static void put(String string, TestVerifierFormatVersion001 testVerifierFormatVersion001) {
		// TODO Auto-generated method stub
		
	}

	public abstract TestSectionOutcome getTestOutcome( TestSection testSection);

	
	protected static LocalDateTime parseDate(Matcher matcher, int dateField) {
		String stringDateTime = matcher.group(dateField);
		if( StringUtils.isNotBlank(stringDateTime)) {
			return LocalDateTime.parse(stringDateTime, DATETIME_FORMATTER);
		}
		
		return null;
	}

}
