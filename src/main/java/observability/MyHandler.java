package observability;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.stream.StreamSupport;

@Component
class MyHandler implements ObservationHandler<Observation.Context> {

    private static final Logger log = LoggerFactory.getLogger(MyHandler.class);
    public void onEvent(Observation.Event event, Observation.Context context) {

    }
    @Override
    public void onStart(Observation.Context context) {

        log.info("Before running the observation for context [{}], userType [{}]", context.getName(), getUserTypeFromContext(context));
    }

    @Override
    public void onStop(Observation.Context context) {
        log.info("After running the observation for context [{}], userType [{}]", context.getName(), getUserTypeFromContext(context));
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }

    private String getUserTypeFromContext(Observation.Context context) {
        return StreamSupport.stream(context.getLowCardinalityKeyValues().spliterator(), false)
                .filter(keyValue -> "userType".equals(keyValue.getKey()))
                .map(KeyValue::getValue)
                .findFirst()
                .orElse("UNKNOWN");
    }
}

@Configuration
class ActuatorHttpExchangesConfiguration {
    @Bean
    public HttpExchangeRepository httpTraceRepository()
    {
        return new InMemoryHttpExchangeRepository();
    }
}