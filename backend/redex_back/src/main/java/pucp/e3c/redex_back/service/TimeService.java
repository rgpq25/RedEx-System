package pucp.e3c.redex_back.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import pucp.e3c.redex_back.config.CustomClockConfig.ClockHolder;

@Service
public class TimeService {
    /* 
    private final Clock clock;
    @Autowired
    public TimeService(Clock clock) {
        this.clock = clock;
    }

    public Date now() {
        Instant instant = clock.instant();
        return Date.from(instant);
    }
        */

    private final ClockHolder clockHolder;

    @Autowired
    public TimeService(ClockHolder clockHolder) {
        this.clockHolder = clockHolder;
    }

    public Date now() {
        Instant instant = clockHolder.getClock().instant();
        return Date.from(instant);
    }

    public void resetClock() {
        clockHolder.resetClock();
    }
}
