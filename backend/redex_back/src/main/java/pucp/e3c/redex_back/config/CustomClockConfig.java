package pucp.e3c.redex_back.config;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class CustomClockConfig {
    
    /*@Bean
    public Clock customClock() {
        Clock baseClock = Clock.systemDefaultZone();
        Instant fixedInstant = Instant.parse("2024-07-22T10:45:00Z");
        return Clock.offset(baseClock, Duration.between(Instant.now(), fixedInstant));
    }*/

    @Bean
    public ClockHolder clockHolder() {
        return new ClockHolder();
    }

    @Bean
    public Clock customClock(ClockHolder clockHolder) {
        return clockHolder.getClock();
    }

    @Component
    public static class ClockHolder {
        private Clock clock;

        public ClockHolder() {
            resetClock();
        }

        public Clock getClock() {
            return clock;
        }

        public void resetClock() {
            Clock baseClock = Clock.systemDefaultZone();
            //Instant fixedInstant = Instant.parse("2024-07-22T10:45:00Z");
            //Para pruebas
            Instant fixedInstant = Instant.parse("2024-07-22T11:00:00Z");
            this.clock = Clock.offset(baseClock, Duration.between(Instant.now(), fixedInstant));
        }
    }

}
