package com.github.nsilbernagel.discordbot;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DiscordbotApplicationTests {
  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate testRestTemplate;

  private UriComponentsBuilder uriBuilder;

  @BeforeEach
  public void init() {
    this.uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost").path("users").port(this.port);
  }

  @Test
  public void shouldReturn200WhenSendingGetRequestToUserEndpoint() throws Exception {
    this.uriBuilder.queryParam("id", 1);

    @SuppressWarnings("rawtypes")
    ResponseEntity<Map> entity = this.testRestTemplate.getForEntity(this.uriBuilder.toUriString(), Map.class);

    then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void shouldReturn400WhenSendingGetRequestToUsersEndpointWithoutProvidingId() throws Exception {
    @SuppressWarnings("rawtypes")
    ResponseEntity<Map> entity = this.testRestTemplate.getForEntity(this.uriBuilder.toUriString(), Map.class);

    then(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void shouldReturn405WhenSendingPostRequestToUsersEndpoint() {
    this.uriBuilder.queryParam("id", 1);

    Map<String, Object> randomPostData = new HashMap<String, Object>();
    randomPostData.put("name", "ich");

    @SuppressWarnings("rawtypes")
    ResponseEntity<Map> entity = this.testRestTemplate.postForEntity(this.uriBuilder.toUriString(), randomPostData,
        Map.class);

    then(entity.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
  }
}
