import static me.ineson.monitor_nbn.modem_status.CheckModemOutput.parseHtml as checkModem
import static me.ineson.monitor_nbn.modem_status.CheckModemOutput.parseHtml

import java.io.InputStream
import java.net.URL

import me.ineson.monitor_nbn.modem_status.ModemStatus


def retryAttempts = 3;

while (retryAttempts > 0) {
    try {
		retryAttempts--
        String modemUrl = "http://192.168.0.1/";
        println "Modem URL: " + modemUrl
	
        InputStream modemPage = new URL( modemUrl).newInputStream( [connectTimeout: 10000, readTimeout: 5000]);
		
        ModemStatus status = checkModem(modemPage, modemUrl);
        println status
		retryAttempts = 0
    } catch( Exception e) {
	    println "### Error: " + e.getMessage()
    }
} 
