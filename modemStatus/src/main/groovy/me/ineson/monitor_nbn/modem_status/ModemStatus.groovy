package me.ineson.monitor_nbn.modem_status

import groovy.transform.CompileStatic
import groovy.transform.ToString

@CompileStatic
class ModemStatus {

	public ModemStatus() {
		super();
	}

	public ModemStatus(String connectionStatus, String accessType, String connectionType, String mode, ModemLeds leds) {
		super();
		this.connectionStatus = connectionStatus;
		this.accessType = accessType;
		this.connectionType = connectionType;
		this.mode = mode;
		this.leds = leds;
	}

    String connectionStatus

	String accessType
	
	String connectionType
	
	String mode
	
	ModemLeds leds
	
	@Override
	public String toString() {
		return "Modem Status: Connection Status [" + connectionStatus + "], Access Type [" + accessType + "], Connection Type [" + connectionType + "], Mode [" + mode + "], Leds (" + leds + ")";
	}

}