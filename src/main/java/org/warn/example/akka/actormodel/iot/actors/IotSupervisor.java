package org.warn.example.akka.actormodel.iot.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * 
 * https://doc.akka.io/docs/akka/current/typed/guide/tutorial_2.html
 *
 */

public class IotSupervisor extends AbstractBehavior<Void> {

	public static Behavior<Void> create() {
		return Behaviors.setup(IotSupervisor::new);
	}

	private IotSupervisor(ActorContext<Void> context) {
		super(context);
		context.getLog().info("IoT Application started");
	}

	// No need to handle any messages
	@Override
	public Receive<Void> createReceive() {
		return newReceiveBuilder()
				.onSignal(PostStop.class, signal -> onPostStop())
				.build();
	}

	private IotSupervisor onPostStop() {
		getContext().getLog().info("IoT Application stopped");
		return this;
	}
}