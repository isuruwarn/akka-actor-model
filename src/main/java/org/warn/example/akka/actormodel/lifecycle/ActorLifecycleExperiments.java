package org.warn.example.akka.actormodel.lifecycle;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * 
 * https://doc.akka.io/docs/akka/current/typed/guide/tutorial_1.html
 *
 */

class StartStopActor1 extends AbstractBehavior<String> {

	static Behavior<String> create() {
		return Behaviors.setup(StartStopActor1::new);
	}

	private StartStopActor1(ActorContext<String> context) {
		super(context);
		System.out.println("first started - ThreadId=" + Thread.currentThread().getId());

		context.spawn(StartStopActor2.create(), "second");
	}

	@Override
	public Receive<String> createReceive() {
		return newReceiveBuilder()
				.onMessageEquals("stop", Behaviors::stopped)
				.onSignal(PostStop.class, signal -> onPostStop())
				.build();
	}

	private Behavior<String> onPostStop() {
		System.out.println("first stopped - ThreadId=" + Thread.currentThread().getId());
		return this;
	}
}

class StartStopActor2 extends AbstractBehavior<String> {

	static Behavior<String> create() {
		return Behaviors.setup(StartStopActor2::new);
	}

	private StartStopActor2(ActorContext<String> context) {
		super(context);
		System.out.println("second started - ThreadId=" + Thread.currentThread().getId());
	}

	@Override
	public Receive<String> createReceive() {
		return newReceiveBuilder()
				.onSignal(PostStop.class, signal -> onPostStop())
				.build();
	}

	private Behavior<String> onPostStop() {
		System.out.println("second stopped - ThreadId=" + Thread.currentThread().getId());
		return this;
	}
}

public class ActorLifecycleExperiments {

	public static void main(String[] args) throws InterruptedException {

		System.out.println("System Actor - creating StartStopActor1 - ThreadId=" + Thread.currentThread().getId());
		ActorRef<String> first = ActorSystem.create(StartStopActor1.create(), "first");
		//ActorRef<String> first = context.spawn(StartStopActor1.create(), "first");
		
		System.out.println("System Actor - Wait for 5 seconds..");
		Thread.sleep(5000);
		
		System.out.println("System Actor - Sending Stop message - ThreadId=" + Thread.currentThread().getId());
		first.tell("stop");
	}

}
