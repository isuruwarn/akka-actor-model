package org.warn.example.akka.actormodel.iot.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.warn.example.akka.actormodel.iot.actors.Device;
import org.warn.example.akka.actormodel.iot.actors.ReadTemperature;
import org.warn.example.akka.actormodel.iot.actors.TemperatureRecorded;
import org.warn.example.akka.actormodel.iot.model.DeviceStatusDTO;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import lombok.extern.slf4j.Slf4j;

/**
 * https://spring.io/guides/gs/spring-boot/
 *
 */
@RestController
@Slf4j
public class IoTController {

	//private ActorSystem<Void> actorSystem;
	//private static ActorContext<Void> context;
	//private static ActorRef<Void> iotSupervisorRef;
	private static ActorRef<Device.TemperatureRecorded> temperatureRecorded;
	private static ActorRef<Device.RespondTemperature> respondTemperature;
	private static Map<String, ActorRef<Device.Command>> deviceMap = new HashMap<>();
	
	public IoTController() {
		//actorSystem = ActorSystem.create(IotSupervisor.create(), "iot-system");
		ActorSystem.create(create(), "IoTDemo");
	}

	private static Behavior<Void> create() {
		return Behaviors.setup(context -> {
			//IoTController.context = context;
			//iotSupervisorRef = context.spawn(IotSupervisor.create(), "iotSupervisor");
			temperatureRecorded = context.spawn(TemperatureRecorded.create(), "temperatureRecorded");
			respondTemperature = context.spawn(ReadTemperature.create(), "respondTemperature");
			
			ActorRef<Device.Command> device1 = context.spawn(Device.create("group1", "device1"), "device1");
			ActorRef<Device.Command> device2 = context.spawn(Device.create("group1", "device2"), "device2");
			ActorRef<Device.Command> device3 = context.spawn(Device.create("group1", "device3"), "device3");

			deviceMap.put("device1", device1);
			deviceMap.put("device2", device2);
			deviceMap.put("device3", device3);
			
			return Behaviors.receive(Void.class).onSignal(Terminated.class, sig -> Behaviors.stopped()).build();
		});
	}
	
	@RequestMapping("/deviceMessages/{deviceId}")
	public DeviceStatusDTO getDeviceMessage( @PathVariable String deviceId ) {
		deviceMap.get(deviceId).tell( new Device.ReadTemperature(0, respondTemperature) );
		if( ReadTemperature.latestDeviceValues != null && ReadTemperature.latestDeviceValues.get(deviceId) != null ) {
			return new DeviceStatusDTO( deviceId, ReadTemperature.latestDeviceValues.get(deviceId) );
		}
		return null;
	}
	
	@RequestMapping("/deviceMessages")
	public List<DeviceStatusDTO> getDeviceMessages() {
		List<DeviceStatusDTO> deviceStatuses = new ArrayList<DeviceStatusDTO>();
		for( String deviceId: deviceMap.keySet() ) {
			deviceMap.get(deviceId).tell( new Device.ReadTemperature(0, respondTemperature) );
			if( ReadTemperature.latestDeviceValues != null && ReadTemperature.latestDeviceValues.get(deviceId) != null ) {
				deviceStatuses.add( new DeviceStatusDTO( deviceId, ReadTemperature.latestDeviceValues.get(deviceId) ) );
			}
		}
		return deviceStatuses;
	}
	
	@PostMapping("/deviceMessages/{deviceId}/{value}")
	public String deviceMessage( @PathVariable String deviceId, @PathVariable double value ) {
		//log.info("DeviceMessage - deviceId={}, value={}", deviceId, value);
		long requestId = System.currentTimeMillis();
		deviceMap.get(deviceId).tell( new Device.RecordTemperature(requestId, value, temperatureRecorded) );
		return String.valueOf(requestId);
	}

	@PostMapping("/deviceMessages/simulate/{messagesPerDevice}")
	public String submit( @PathVariable int messagesPerDevice ) {

		for( int i=0; i<messagesPerDevice; i++ ) {
			for( String deviceId: deviceMap.keySet() ) {
				long requestId = System.currentTimeMillis();
				double value = Math.random() * 100;
				deviceMap.get(deviceId).tell( new Device.RecordTemperature(requestId, value, temperatureRecorded) );
			}
		}
		
		return "Completed";
	}

}