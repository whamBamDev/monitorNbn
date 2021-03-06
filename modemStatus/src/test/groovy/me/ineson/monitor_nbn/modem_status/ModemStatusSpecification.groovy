package me.ineson.monitor_nbn.modem_status;

import spock.lang.*

/**
 * @author peter
 *
 */
class ModemStatusSpecification extends Specification {

	@Shared	
	def ModemLeds leds = new ModemLeds( Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE)

	@Shared	
	def ledsString = "Modem LEDs: Online [true], WAN/DSL [false], Mobile Mode [true], Mobile Signal [false], Phone [true]"
	
	@Unroll
    def "test modem status toString method"( ModemStatus status, String stringOutput) {

		// blocks go here
		expect:
			status.toString() == stringOutput
			
        where:
            status                                          | stringOutput
			new ModemStatus("cS", "aT", "cT", "mode", leds) | "Modem Status: Connection Status [cS], Access Type [aT], Connection Type [cT], Mode [mode], Leds (" + ledsString + ")"
			new ModemStatus()                               | "Modem Status: Connection Status [null], Access Type [null], Connection Type [null], Mode [null], Leds (null)"
    }
}
