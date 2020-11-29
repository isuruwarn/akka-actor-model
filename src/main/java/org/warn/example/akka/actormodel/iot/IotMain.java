package org.warn.example.akka.actormodel.iot;

import org.warn.example.akka.actormodel.iot.actors.IotSupervisor;

import akka.actor.typed.ActorSystem;

/**
 * 
 * https://doc.akka.io/docs/akka/current/typed/guide/tutorial_2.html
 *
 */

public class IotMain {

	public static void main(String[] args) {
		// Create ActorSystem and top level supervisor
		ActorSystem.create(IotSupervisor.create(), "iot-system");
	}
}