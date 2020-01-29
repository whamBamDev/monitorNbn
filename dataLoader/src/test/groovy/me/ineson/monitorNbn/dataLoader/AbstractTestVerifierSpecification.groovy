package me.ineson.monitorNbn.dataLoader;

import spock.lang.*

class AbstractTestVerifierSpecification extends Specification {

	
	@Unroll
	def "should return proper verifier for a specific version"(List<String> testLines, Class<? extends AbstractTestVerifier> expectedClass) {
        when:
		    def verifier = AbstractTestVerifier.getTestVerifier( testLines)

		then:
			expectedClass.isCase(verifier)
	
		where:
			testLines                                          | expectedClass
			[ "==== start(001): 05-12-2019 00:00:01", "test"]  | TestVerifierFormatVersion001
	
		}
	
}
