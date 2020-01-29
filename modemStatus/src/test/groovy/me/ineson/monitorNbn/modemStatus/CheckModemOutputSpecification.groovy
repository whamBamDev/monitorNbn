package me.ineson.monitorNbn.modemStatus;

import static me.ineson.monitorNbn.modemStatus.CheckModemOutput.parseHtml as checker

import java.io.InputStream

import groovy.transform.CompileStatic
import me.ineson.monitorNbn.modemStatus.ModemStatus
import spock.lang.*

class CheckModemOutputSpecification extends Specification {

	@Unroll
    def "test paring modem page #statusPage gets the status"(String statusPage, String connectionStatus,
		String accessType, String connectionType, String mode,
		Boolean ledOnline, Boolean ledWanDsl, Boolean ledMobileMode, Boolean ledMobileSignal, def ledPhone) {

		// blocks go here
		expect:
			InputStream modemHtml = Thread.currentThread().getContextClassLoader()
			    .getResourceAsStream( "me/ineson/monitorNbn/modemStatus/" + statusPage);
			ModemStatus status = checker(modemHtml, "");
            status.connectionStatus == connectionStatus
			status.accessType == accessType
			status.connectionType == connectionType
			status.mode == mode
			status.leds.online == ledOnline
			status.leds.wanDsl == ledWanDsl
			status.leds.mobileMode == ledMobileMode
			status.leds.mobileSignal == ledMobileSignal
			status.leds.phone == ledPhone
			
        where:
            statusPage               | connectionStatus | accessType | connectionType | mode  | ledOnline     | ledWanDsl     | ledMobileMode | ledMobileSignal | ledPhone
            "connected_via_nbn.html" | "Connected"      | "ETH"      | "IPoE"         | "SIP" | Boolean.TRUE  | Boolean.TRUE  | Boolean.FALSE | Boolean.TRUE    | Boolean.TRUE
            "connected_via_4G.html"  | "Connected"      | "ETH"      | "Mobile"       | "SIP" | Boolean.TRUE  | Boolean.TRUE  | Boolean.TRUE  | Boolean.TRUE    | Boolean.TRUE
            "disconnected.html"      | "Disconnected"   | "ETH"      | "IPoE"         | "SIP" | Boolean.TRUE  | Boolean.TRUE  | Boolean.TRUE  | Boolean.TRUE    | Boolean.TRUE
            "phone_sos.html"         | "Disconnected"   | "ETH"      | "IPoE"         | "SIP" | Boolean.FALSE | Boolean.FALSE | Boolean.FALSE | Boolean.TRUE    | "SOS"
    }

}


