package org.warn.example.akka.actormodel.iot.actors;

import java.util.HashMap;
import java.util.Map;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class ReadTemperature extends AbstractBehavior<Device.RespondTemperature> {

	public static final Map<String, Double> latestDeviceValues = new HashMap<>();
	
	public static Behavior<Device.RespondTemperature> create() {
		return Behaviors.setup(context -> new ReadTemperature(context));
	}

	private ReadTemperature(ActorContext<Device.RespondTemperature> context) {
		super(context);
	}

	@Override
	public Receive<Device.RespondTemperature> createReceive() {
		return newReceiveBuilder()
				.onMessage(Device.RespondTemperature.class, this::onReceive)
				.build();
	}

	private Behavior<Device.RespondTemperature> onReceive(Device.RespondTemperature message) {
		getContext().getLog().info("RequestId={}, DeviceId={}, Value={}, ThreadId={}", 
				message.requestId, message.deviceId, message.value, Thread.currentThread().getId());
		latestDeviceValues.put(message.deviceId, message.value.orElse(0.0));
		return this;
	}
	
}