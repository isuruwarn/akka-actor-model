package org.warn.example.akka.actormodel.hierarchy;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * 
 * https://doc.akka.io/docs/akka/current/typed/guide/tutorial_1.html
 *
 */

class PrintMyActorRefActor extends AbstractBehavior<String> {

	static Behavior<String> create() {
		return Behaviors.setup(PrintMyActorRefActor::new);
	}

	private PrintMyActorRefActor(ActorContext<String> context) {
		super(context);
	}

	@Override
	public Receive<String> createReceive() {
		System.out.println("Print Actor - createReceive - ThreadId=" + Thread.currentThread().getId());
		return newReceiveBuilder()
				.onMessageEquals("printit", this::printIt)
				.build();
	}

	private Behavior<String> printIt() {
		System.out.println("Print Actor - creating second-actor - ThreadId=" + Thread.currentThread().getId());
		ActorRef<String> secondRef = getContext().spawn(Behaviors.empty(), "second-actor");
		System.out.println("Print Actor - Second: " + secondRef + " - ThreadId=" + Thread.currentThread().getId());
		return this;
	}
}

class Main extends AbstractBehavior<String> {

	static Behavior<String> create() {
		return Behaviors.setup(Main::new);
	}

	private Main(ActorContext<String> context) {
		super(context);
	}

	@Override
	public Receive<String> createReceive() {
		System.out.println("Main Actor - createReceive - ThreadId=" + Thread.currentThread().getId());
		return newReceiveBuilder()
				.onMessageEquals("start", this::start)
				.build();
	}

	private Behavior<String> start() {
		System.out.println("Main Actor - creating first-actor - ThreadId=" + Thread.currentThread().getId());
		ActorRef<String> firstRef = getContext().spawn(PrintMyActorRefActor.create(), "first-actor");

		System.out.println("Main Actor - First: " + firstRef + " - ThreadId=" + Thread.currentThread().getId());
		firstRef.tell("printit");
		return Behaviors.same();
	}
}

public class ActorHierarchyExperiments {
	
	public static void main(String[] args) throws InterruptedException {
		
		System.out.println("System Actor - creating main-actor - ThreadId=" + Thread.currentThread().getId());
		ActorRef<String> testSystem = ActorSystem.create(Main.create(), "testSystem");
		
		System.out.println("System Actor - Wait for 3 seconds..");
		Thread.sleep(3000);
		
		System.out.println("System Actor - Main: " + testSystem +  " - ThreadId=" + Thread.currentThread().getId());
		testSystem.tell("start");
	}
}