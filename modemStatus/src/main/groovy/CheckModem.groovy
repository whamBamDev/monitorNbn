import static me.ineson.monitorNbn.CheckModemOutput.parseHtml as checkModem
import me.ineson.monitorNbn.ModemStatus
import java.io.InputStream
import java.net.URL

try {
	String modemUrl = "http://192.168.0.1/";
	println "Modem URL: " + modemUrl
	
    InputStream modemPage = new URL( modemUrl).newInputStream( [connectTimeout: 5000, readTimeout: 1000]);

    ModemStatus status = checkModem(modemPage, modemUrl);
	println status
} catch( Exception e) {
	println "### Error: " + e.getMessage()
}

