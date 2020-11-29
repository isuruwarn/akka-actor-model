package org.warn.example.akka.actormodel.iot.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class TemperatureRecorded extends AbstractBehavior<Device.TemperatureRecorded> {

	public static Device.TemperatureRecorded temperatureRecorded;
	
	public static Behavior<Device.TemperatureRecorded> create() {
		return Behaviors.setup(context -> new TemperatureRecorded(context));
	}

	private TemperatureRecorded(ActorContext<Device.TemperatureRecorded> context) {
		super(context);
	}

	@Override
	public Receive<Device.TemperatureRecorded> createReceive() {
		return newReceiveBuilder()
				.onMessage(Device.TemperatureRecorded.class, this::onTemperatureRecorded)
				.build();
	}

	private Behavior<Device.TemperatureRecorded> onTemperatureRecorded(Device.TemperatureRecorded message) {
		//getContext().getLog().info("RequestId={}", message.requestId);
		temperatureRecorded = message;
		return this;
	}

	public Device.TemperatureRecorded getTemperatureRecorded() {
		return temperatureRecorded;
	}
	
}