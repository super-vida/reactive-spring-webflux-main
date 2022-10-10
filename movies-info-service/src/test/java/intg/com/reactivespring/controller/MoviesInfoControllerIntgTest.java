package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebClient
class MoviesInfoControllerIntgTest {

	public static final String MOVIES_INFO_URL = "/v1/movieinfos";
	@Autowired
	MovieInfoRepository movieInfoRepository;
	@Autowired
	WebTestClient webTestClient;

	@BeforeEach
	void setUp() {
		var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
						2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
				new MovieInfo(null, "The Dark Knight",
						2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
				new MovieInfo("abc", "Dark Knight Rises",
						2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

		movieInfoRepository.saveAll(movieinfos)
				.blockLast();//only in tests
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void addMovieInfo() {

		var movieInfo = new MovieInfo(null, "Batman Begins1",
				2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

		webTestClient.post()
				.uri(MOVIES_INFO_URL)
				.bodyValue(movieInfo).exchange()
				.expectStatus()
				.isCreated()
				.expectBody(MovieInfo.class)
				.consumeWith(movieInfoEntityExchangeResult -> {
					var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
					assert savedMovieInfo != null;
					assert savedMovieInfo.getMovieInfoId() != null;
				});

	}

	@Test
	void getAllMovieInfos() {
		webTestClient
				.get()
				.uri(MOVIES_INFO_URL)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBodyList(MovieInfo.class)
				.hasSize(3);
	}
	@Test
	void getAllMovieByYear() {
		var uri = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL)
				.queryParam("year", 2005)
				.buildAndExpand().toUri();
		webTestClient
				.get()
				.uri(uri)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBodyList(MovieInfo.class)
				.hasSize(1);
	}

	@Test
	void getMovieInfoById() {
		var movieInfoId = "abc";
		webTestClient
				.get()
				.uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody()
				.jsonPath("$.name").isEqualTo("Dark Knight Rises");
		//                .expectBody(MovieInfo.class)
		//                .consumeWith(movieInfoEntityExchangeResult -> {
		//                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
		//                    assertNotNull(movieInfo);
		//                });
	}

	@Test
	void getMovieInfoByIdNotFound() {
		var movieInfoId = "def";
		webTestClient
				.get()
				.uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
				.exchange()
				.expectStatus()
				.isNotFound();
	}

	@Test
	void updateMovieInfo() {

		var movieInfoId = "abc";
		var movieInfo = new MovieInfo(null, "Dark Knight Rises1",
				2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

		webTestClient.put()
				.uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
				.bodyValue(movieInfo).exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody(MovieInfo.class)
				.consumeWith(movieInfoEntityExchangeResult -> {
					var updatedMovieInFo = movieInfoEntityExchangeResult.getResponseBody();
					assert updatedMovieInFo != null;
					assert updatedMovieInFo.getMovieInfoId() != null;
					assertEquals("Dark Knight Rises1", updatedMovieInFo.getName());
				});

	}
	@Test
	void updateMovieInfoNotFound() {

		var movieInfoId = "def";
		var movieInfo = new MovieInfo(null, "Dark Knight Rises1",
				2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

		webTestClient.put()
				.uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
				.bodyValue(movieInfo).exchange()
				.expectStatus()
				.isNotFound();

	}

	@Test
	void deleteMovieInfoById() {
		var id = "abc";
		webTestClient
				.delete()
				.uri(MOVIES_INFO_URL + "/{id}", id)
				.exchange()
				.expectStatus()
				.isNoContent();
	}
}