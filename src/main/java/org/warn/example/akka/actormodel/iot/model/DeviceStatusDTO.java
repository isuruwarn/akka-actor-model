package org.warn.example.akka.actormodel.iot.model;

public class DeviceStatusDTO {
	
	private String deviceId;
	private double value;
	
	public DeviceStatusDTO(String deviceId, double value) {
		this.deviceId = deviceId;
		this.value = value;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
