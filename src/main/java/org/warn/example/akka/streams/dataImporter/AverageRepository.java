package org.warn.example.akka.streams.dataImporter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * https://www.baeldung.com/akka-streams
 * https://github.com/eugenp/tutorials/blob/master/akka-streams/pom.xml
 */

public class AverageRepository {
	CompletionStage<Double> save(Double average) {
		return CompletableFuture.supplyAsync(() -> {
			System.out.println("saving average: " + average);
			return average;
		});
	}
}