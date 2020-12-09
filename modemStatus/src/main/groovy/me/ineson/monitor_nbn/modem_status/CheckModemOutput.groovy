package me.ineson.monitor_nbn.modem_status

import static me.ineson.monitor_nbn.modem_status.ModemLeds.PHONE_SOS

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import groovy.transform.CompileStatic

@CompileStatic
class CheckModemOutput {

	static def ModemStatus parseHtml( InputStream html, String url) {
		ModemStatus status = new ModemStatus();
		ModemLeds leds = new ModemLeds();
		status.leds = leds;
				
		Document document = Jsoup.parse(html, "UTF-8", url);
	
        Elements links = document.getElementsByTag("h4");
		for (Element link : links) {
			if( link.hasText()) {
				String headerText = link.text();

                if( headerText == "Broadband") {
					Element parent = link.parent();
                    Elements divs = parent.getElementsByTag("div");
					Element div = divs.last();
					status.connectionStatus = div.text();
                } 
			}
		}
		
		Element statusElement = document.getElementById("Access Type");
		if(statusElement != null) {
			status.accessType = statusElement.text();
		}
		
		statusElement = document.getElementById("Connection Type");
		if(statusElement != null) {
			status.connectionType = statusElement.text();
		}

		statusElement = document.getElementById("Mode");
		if(statusElement != null) {
			status.mode = statusElement.text();
		}

		leds.online = getLedStatus( document, "ONLINE");
		leds.wanDsl = getLedStatus( document, "WAN/DSL");
		leds.mobileMode = getLedStatus( document, "MOBILE MODE");
		leds.mobileSignal = getLedStatus( document, "MOBILE SIGNAL");

		Element phoneLedStatusElement = document.getElementById("PHONE");
		Elements divs = phoneLedStatusElement.getElementsByTag("div");
		if( ! divs.isEmpty()) {
			Element div = divs.get( 0);
			if( div.hasClass("orange") && ! div.hasClass("hide")) {
				leds.phone = PHONE_SOS;
            } else {
                leds.phone = Boolean.valueOf( div.hasClass("green") && ! div.hasClass("hide"));
			}
		}
		
		return status;
	}
	
	static def Boolean getLedStatus( Document document, String ledId) {
		Boolean result = null;
		Element ledStatusElement = document.getElementById(ledId);
		Elements divs = ledStatusElement.getElementsByTag("div");
		if( ! divs.isEmpty()) {
			Element div = divs.get( 0);
			result = div.hasClass("green") && ! div.hasClass("hide");
		}
		
		return result;
	}
}