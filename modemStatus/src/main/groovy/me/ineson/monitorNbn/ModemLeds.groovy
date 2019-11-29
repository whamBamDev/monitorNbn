package me.ineson.monitorNbn

import groovy.transform.ToString

@ToString
class ModemLeds {
	
	public static final String PHONE_SOS = "SOS"
	
	public ModemLeds() {
		super();
	}

	public ModemLeds(Boolean online, Boolean wanDsl, Boolean mobileMode, Boolean mobileSignal, Object phone) {
		super();
		this.online = online;
		this.wanDsl = wanDsl;
		this.mobileMode = mobileMode;
		this.mobileSignal = mobileSignal;
		this.phone = phone;
	}
	
    Boolean online

    Boolean wanDsl

    Boolean mobileMode
	
	Boolean mobileSignal
	
    def phone
	
	@Override
	public String toString() {
        return "Modem LEDs: Online [" + online + "], WAN/DSL [" + wanDsl + "], Mobile Mode [" + mobileMode + "], Mobile Signal [" + mobileSignal + "], Phone [" + phone + "]";
	}
}