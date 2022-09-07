package io.learning;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Path("/hello")
public class GreetingResource {

    private final Meter meter;

    private final LongCounter noOfHits;

    private final LongCounter noOfFailures;

    public GreetingResource() {
        this.meter = GlobalOpenTelemetry.getMeter("io.opentelemetry.metrics");
        noOfHits = meter.counterBuilder("NoOfHitsCounter").setUnit("unit").build();
        noOfFailures = meter.counterBuilder("NoOfFailureCounter").setUnit("unit").build();
    }
    AtomicInteger counter = new AtomicInteger(0);
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        int counterValue = this.counter.incrementAndGet();
        log.info("Greeting resource called: " + counterValue + "times");
        noOfHits.add(1, Attributes.of(AttributeKey.stringKey("Endpoints"), "Hit on hello endpoint"));
        if(counterValue % 3 == 0) {
            noOfFailures.add(1);
            throw new RuntimeException("monitor error rate");
        }

        return "Hello RESTEasy";
    }
}