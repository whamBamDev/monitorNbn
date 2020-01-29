/**
 * 
 */
package me.ineson.monitorNbn.dataLoader;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author peter
 *
 */
public abstract class AbstractTestVerifier {

    private static final Logger log = LogManager.getLogger(AbstractTestVerifier.class);
    
    private static final Map<String, AbstractTestVerifier> VERIFIERS = createVerifiers();

    // echo "==== start(001): 03-12-2019 00:55:29" 

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	private static final String DATETIME_FORMAT = "\\d{1,2}-\\d{1,2}-\\d{2,4} \\d{1,2}\\:\\d{1,2}\\:\\d{1,2}";

	private static final Pattern START_LINE = Pattern.compile("==== start\\((\\d*)\\): (" + DATETIME_FORMAT + ")");
	
	private static final Pattern END_LINE = Pattern.compile("==== end: (" + DATETIME_FORMAT + ")");

	
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
		log.debug("Got version {} from line: {}", version, firstLine);
		
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
	
}
