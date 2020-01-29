package me.ineson.monitorNbn.modemStatus;

import static me.ineson.monitorNbn.modemStatus.ModemLeds.PHONE_SOS

import me.ineson.monitorNbn.modemStatus.ModemLeds
import spock.lang.*

/**
 * @author peter
 *
 */
class ModemLedsSpecification extends Specification {

	@Unroll
    def "test leds toString method"( ModemLeds leds, String stringOutput) {

		// blocks go here
		expect:
			leds.toString() == stringOutput
			
        where:
            leds                                                                                    | stringOutput
            new ModemLeds( Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE)  | "Modem LEDs: Online [true], WAN/DSL [false], Mobile Mode [true], Mobile Signal [false], Phone [true]"
            new ModemLeds( Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE) | "Modem LEDs: Online [false], WAN/DSL [true], Mobile Mode [false], Mobile Signal [true], Phone [false]"
			new ModemLeds( Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, PHONE_SOS)     | "Modem LEDs: Online [true], WAN/DSL [false], Mobile Mode [true], Mobile Signal [false], Phone [SOS]"
			new ModemLeds()                                                                         | "Modem LEDs: Online [null], WAN/DSL [null], Mobile Mode [null], Mobile Signal [null], Phone [null]"
    }
}
