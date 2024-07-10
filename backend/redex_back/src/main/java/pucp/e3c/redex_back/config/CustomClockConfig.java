package pucp.e3c.redex_back.config;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomClockConfig {
    
    @Bean
    public Clock customClock() {
        Clock baseClock = Clock.systemDefaultZone();
        Instant fixedInstant = Instant.parse("2024-07-22T11:00:00Z");
        return Clock.offset(baseClock, Duration.between(Instant.now(), fixedInstant));
    }

}
