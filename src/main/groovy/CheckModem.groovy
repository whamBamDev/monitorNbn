import static me.ineson.monitorNbn.CheckModemOutput.parseHtml as checker
import me.ineson.monitorNbn.ModemStatus
import java.io.InputStream
import java.net.URL

try {
    InputStream modemPage = new URL("http://192.168.0.1/").newInputStream([connectTimeout: 5000, readTimeout: 1000]);

    ModemStatus status = checker(modemPage)
	println status
} catch( Exception e) {
	println "### Error: " + e.getMessage()
}

