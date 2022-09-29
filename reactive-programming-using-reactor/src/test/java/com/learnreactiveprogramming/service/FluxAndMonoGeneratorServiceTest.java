package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {

	FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

	@Test
	void namesFlux() {

		var namesFlux = fluxAndMonoGeneratorService.namesFlux();
		StepVerifier.create(namesFlux)
				//.expectNext("alex", "ben", "chloe")
				//.expectNextCount(3)
				.expectNext("alex")
				.expectNextCount(2)
				.verifyComplete();

	}

	@Test
	void namesFlux_map() {

		int stringLength = 3;
		var namesFlux = fluxAndMonoGeneratorService.namesFlux_map(stringLength);
		StepVerifier.create(namesFlux)
				// .expectNext("ALEX", "BEN", "CHLOE")
				.expectNext("4-ALEX", "5-CHLOE")
				.verifyComplete();
	}

	@Test
	void namesFlux_map_immutable() {

		var namesFlux = fluxAndMonoGeneratorService.namesFlux_immutable();
		StepVerifier.create(namesFlux)
				.expectNext("alex", "ben", "chloe")
				.verifyComplete();

	}

	@Test
	void namesFlux_flatmap() {
		int stringLength = 3;
		var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(stringLength);
		StepVerifier.create(namesFlux)
				.expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
				.verifyComplete();
	}

	@Test
	void namesFlux_flatmap_async() {
		int stringLength = 3;
		var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength);
		StepVerifier.create(namesFlux)
				//.expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
				.expectNextCount(9)
				.verifyComplete();
	}
	@Test
	void namesFlux_concatmap_async() {
		int stringLength = 3;
		var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap_async(stringLength);
		StepVerifier.create(namesFlux)
				.expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
				.verifyComplete();
	}

	@Test
	void nameMono_flatMap() {
		int stringLength = 3;
		var namesFlux = fluxAndMonoGeneratorService.nameMono_flatMap(stringLength);
		StepVerifier.create(namesFlux)
				.expectNext(List.of("A", "L", "E", "X"))
				.verifyComplete();
	}

	@Test
	void nameMono_flatMapMany() {
		int stringLength = 3;
		var namesFlux = fluxAndMonoGeneratorService.nameMono_flatMapMany(stringLength);
		StepVerifier.create(namesFlux)
				.expectNext("A", "L", "E", "X")
				.verifyComplete();
	}

    @Test
    void namesFlux_transform() {
		int stringLength = 3;
		var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);
		StepVerifier.create(namesFlux)
				.expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
				.verifyComplete();
    }

	@Test
	void namesFlux_transform_1() {
		int stringLength = 6;
		var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);
		StepVerifier.create(namesFlux)
				//.expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
				.expectNext("default")
				.verifyComplete();
	}

	@Test
	void namesFlux_transform_swithIfEmpty() {
		int stringLength = 6;
		var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(stringLength);
		StepVerifier.create(namesFlux)
				//.expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
				.expectNext("D","E","F","A","U","L","T")
				.verifyComplete();
	}
}