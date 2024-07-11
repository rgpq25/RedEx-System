package pucp.e3c.redex_back.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.service.TimeService;

@RestController
@CrossOrigin(origins = "https://inf226-981-3c.inf.pucp.edu.pe")
@RequestMapping("back/time")
public class TimeController {
    @Autowired
    private TimeService timeService;
    
    @GetMapping("/now")
    public Date now() {
        return timeService.now();
    }

    @PostMapping("/reset")
    public void resetClock() {
        timeService.resetClock();
    }
}
