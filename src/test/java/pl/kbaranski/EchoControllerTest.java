package pl.kbaranski;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Configuration
@AutoConfigureObservability
class EchoControllerTest {

    @Test
    void shouldReturnEchoValue() {
        var echoResponse = WebClient.create()
                 .get()
                 .uri("http://localhost:8080/echo")
                 .exchangeToMono( this::handleResponse);
        StepVerifier.create(echoResponse)
                    .expectNext("echo")
                    .verifyComplete();
    }

    private Mono<String> handleResponse(ClientResponse response) {
        if (response.statusCode().is2xxSuccessful())
            return response.bodyToMono(String.class);
        else
            return Mono.just("ERROR response wit code " + response.statusCode());
    }
}