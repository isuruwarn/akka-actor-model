package org.warn.example.akka.actormodel.chatbot;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * 
 * https://doc.akka.io/docs/akka/current/typed/actors.html
 *
 */

public class HelloWorldMain extends AbstractBehavior<HelloWorldMain.SayHello> {

	public static class SayHello {
		public final String name;

		public SayHello(String name) {
			this.name = name;
		}
	}

	public static Behavior<SayHello> create() {
		return Behaviors.setup(HelloWorldMain::new);
	}

	private final ActorRef<HelloWorld.Greet> greeter;

	private HelloWorldMain(ActorContext<SayHello> context) {
		super(context);
		greeter = context.spawn(HelloWorld.create(), "greeter");
	}

	@Override
	public Receive<SayHello> createReceive() {
		return newReceiveBuilder().onMessage(SayHello.class, this::onStart).build();
	}

	private Behavior<SayHello> onStart(SayHello command) {
		ActorRef<HelloWorld.Greeted> replyTo = getContext().spawn(HelloWorldBot.create(3), command.name);
		greeter.tell(new HelloWorld.Greet(command.name, replyTo));
		return this;
	}
	
	public static void main(String [] args) {
		final ActorSystem<HelloWorldMain.SayHello> system = ActorSystem.create(HelloWorldMain.create(), "hello");

		system.tell(new HelloWorldMain.SayHello("World"));
		system.tell(new HelloWorldMain.SayHello("Akka"));
	}
}