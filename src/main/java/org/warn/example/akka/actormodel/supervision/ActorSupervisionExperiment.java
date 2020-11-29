package org.warn.example.akka.actormodel.supervision;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.PreRestart;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * 
 * https://doc.akka.io/docs/akka/current/typed/guide/tutorial_1.html
 *
 */

class SupervisingActor extends AbstractBehavior<String> {

	static Behavior<String> create() {
		return Behaviors.setup(SupervisingActor::new);
	}

	private final ActorRef<String> child;

	private SupervisingActor(ActorContext<String> context) {
		super(context);
		child = context.spawn(
				Behaviors.supervise(SupervisedActor.create())
				.onFailure(SupervisorStrategy.restart()), "supervised-actor");
		System.out.println("supervising actor (parent) started - ThreadId=" + Thread.currentThread().getId());
	}

	@Override
	public Receive<String> createReceive() {
		return newReceiveBuilder()
				.onMessageEquals("failChild", this::onFailChild)
				.build();
	}

	private Behavior<String> onFailChild() {
		child.tell("fail");
		return this;
	}
}

class SupervisedActor extends AbstractBehavior<String> {

	static Behavior<String> create() {
		return Behaviors.setup(SupervisedActor::new);
	}

	private SupervisedActor(ActorContext<String> context) {
		super(context);
		System.out.println("supervised actor (child) started - ThreadId=" + Thread.currentThread().getId());
	}

	@Override
	public Receive<String> createReceive() {
		return newReceiveBuilder()
				.onMessageEquals("fail", this::fail)
				.onSignal(PreRestart.class, signal -> preRestart())
				.onSignal(PostStop.class, signal -> postStop())
				.build();
	}

	private Behavior<String> fail() {
		System.out.println("supervised actor (child) error - ThreadId=" + Thread.currentThread().getId());
		throw new RuntimeException("I failed!");
	}

	private Behavior<String> preRestart() {
		System.out.println("supervised actor (child) will be restarted - ThreadId=" + Thread.currentThread().getId());
		return this;
	}

	private Behavior<String> postStop() {
		System.out.println("supervised actor (child) stopped - ThreadId=" + Thread.currentThread().getId());
		return this;
	}
}

public class ActorSupervisionExperiment {

	public static void main(String args []) throws InterruptedException {
		
		ActorRef<String> supervisingActor = ActorSystem.create(SupervisingActor.create(), "supervising-actor");
		//ActorRef<String> supervisingActor = context.spawn(SupervisingActor.create(), "supervising-actor");
		
		Thread.sleep(5000);
		System.out.println("triggering supervised actor (child) failure - ThreadId=" + Thread.currentThread().getId());
		
		supervisingActor.tell("failChild");
	}
}
