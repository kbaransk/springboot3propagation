package pl.kbaranski;

import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Configuration
public class Application {

    @Bean
    Baggage valueToPropagate(Tracer tracer) {
        return tracer.createBaggage("valueToPropagate");
    }

	public static void main(String[] args) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(Application.class, args);
	}

}

@RestController
class EchoController {
    private Baggage valueToPropagate;

    EchoController(Baggage valueToPropagate) {
        this.valueToPropagate = valueToPropagate;
    }

    @GetMapping("/{value}")
    @Observed
    Mono<String> echo(@PathVariable String value) {
        var baggageInScope = valueToPropagate.makeCurrent(value);
        var baggageValue = valueToPropagate.makeCurrent().get();
        System.out.println("Values: " + value + " -> " + baggageInScope.get() + " -> " + baggageValue);
        return Mono.justOrEmpty(baggageValue);
    }
}
