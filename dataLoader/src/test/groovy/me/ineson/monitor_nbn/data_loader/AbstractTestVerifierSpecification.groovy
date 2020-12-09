package me.ineson.monitor_nbn.data_loader;

import me.ineson.monitor_nbn.data_loader.AbstractTestVerifier
import me.ineson.monitor_nbn.data_loader.TestVerifierFormatVersion001
import spock.lang.*

class AbstractTestVerifierSpecification extends Specification {

	@Unroll
	def "factory method should return proper verifier for a specific version"(List<String> testLines, Class<? extends AbstractTestVerifier> expectedClass) {
        when:
		    def verifier = AbstractTestVerifier.getTestVerifier( testLines)

		then:
			expectedClass.isCase(verifier)
	
		where:
			testLines                                          | expectedClass
			[ "==== start(001): 05-12-2019 00:00:01", "test"]  | TestVerifierFormatVersion001
	
    }

	@Unroll
	def "factory method should thrown exception for unknown version"(List<String> testLines) {
		when:
			def verifier = AbstractTestVerifier.getTestVerifier( testLines)

        then:
			thrown IllegalStateException
	
		where:
			testLines                                          | _
			null | _
			[ null ]  | _
			[ "", ""]  | _
			[ "xxxxxxxxxxxx", "xxxx"]  | _
			[ "==== start(999): 05-12-2019 00:00:01", "test"]  | _
			
	}

	@Unroll
	def "Test parsing of modem status line and check of connection via NBN"(boolean result, String testLine) {
        expect:
            AbstractTestVerifier.connectedViaNbn(testLine) == result
	
        where:
            result | testLine
            true   | "Modem Status: Connection Status [Connected], Access Type [ETH], Connection Type [IPoE], Mode [SIP], Leds (Modem LEDs: Online [true], WAN/DSL [true], Mobile Mode [false], Mobile Signal [true], Phone [true])"
            false  | "Modem Status: Connection Status [Connected], Access Type [ETH], Connection Type [Mobile], Mode [SIP], Leds (Modem LEDs: Online [true], WAN/DSL [true], Mobile Mode [true], Mobile Signal [true], Phone [true])"
            false  | "Modem Status: Connection Status [Disconnected], Access Type [ETH], Connection Type [IPoE], Mode [SIP], Leds (Modem LEDs: Online [true], WAN/DSL [true], Mobile Mode [false], Mobile Signal [true], Phone [true])"
    }
	
    @Unroll
    def "Test parsing of modem status line with invalid data throws exception"(String testLine) {
        when:
            AbstractTestVerifier.connectedViaNbn(testLine) == result

        then:
			thrown IllegalStateException

        where:
            testLine | _
			null | _
			"" | _
			"xxx" | _
			"Modem Status: Connection Status " | _
	}

}
