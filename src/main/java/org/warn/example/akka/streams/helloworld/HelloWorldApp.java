package org.warn.example.akka.streams.helloworld;

import java.util.concurrent.CompletionStage;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.javadsl.Source;

public class HelloWorldApp {
	
	public static void main(String [] args) {
		
		final ActorSystem system = ActorSystem.create("QuickStart");
		final Source<Integer, NotUsed> source = Source.range(1, 100);
		final CompletionStage<Done> done = source.runForeach(i -> System.out.println(i), system);
		done.thenRun(() -> system.terminate());
		
	}
	
}
