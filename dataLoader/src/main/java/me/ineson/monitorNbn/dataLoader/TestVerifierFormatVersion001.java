/**
 * 
 */
package me.ineson.monitorNbn.dataLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author peter
 *
 */
public class TestVerifierFormatVersion001 extends AbstractTestVerifier {

    private static final Logger log = LogManager.getLogger(TestVerifierFormatVersion001.class);
	
	public TestSectionOutcome getTestOutcome( TestSection testSection) {
		TestSectionOutcome outcome = new TestSectionOutcome();
		return outcome;
	}
	
}
