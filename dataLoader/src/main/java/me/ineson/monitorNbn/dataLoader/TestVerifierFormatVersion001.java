/**
 * 
 */
package me.ineson.monitorNbn.dataLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author peter
 *
 */
public class TestVerifierFormatVersion001 extends AbstractTestVerifier {

    private static final Logger log = LogManager.getLogger(TestVerifierFormatVersion001.class);
	
    private static List<String>SUPPORTED_TESTS = Collections.unmodifiableList(
    		Arrays.asList(TEST_TRACEROUTE_IP, TEST_TRACEROUTE_DNS, TEST_PING, TEST_MODEN_STATUS));

	public TestSectionOutcome getTestOutcome( TestSection testSection) {
		TestSectionOutcome outcome = new TestSectionOutcome();
		
		
		List<?>individualTests = new ArrayList<List<String>>(); 

		List<String>individualTest = null; 

		for (String line : testSection.getLines()) {
			if( Objects.isNull( outcome.getStartTime())) {
				Matcher versionMatcher = START_LINE.matcher(line);
				if( versionMatcher.matches()) {
					outcome.setStartTime(parseDate( versionMatcher, START_LINE_DATE_POS));
				}
			} else if( Objects.isNull(outcome.getEndTime())) {
				Matcher versionMatcher = END_LINE.matcher(line);
				if( versionMatcher.matches()) {
					outcome.setStartTime(parseDate( versionMatcher, END_LINE_DATE_POS));
				}
			}
		}
/*			if( END)
			
			
		}
*/		
		
		
		return outcome;
	}
	
}
