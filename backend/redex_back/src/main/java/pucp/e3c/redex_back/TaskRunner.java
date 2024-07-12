package pucp.e3c.redex_back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.model.Algoritmo;
import pucp.e3c.redex_back.model.PlanVuelo;
import pucp.e3c.redex_back.service.AeropuertoService;
import pucp.e3c.redex_back.service.PaqueteService;
import pucp.e3c.redex_back.service.PlanRutaService;
import pucp.e3c.redex_back.service.PlanRutaXVueloService;
import pucp.e3c.redex_back.service.VueloService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TaskRunner {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private CompletableFuture<Void> future;
    @Autowired
    private Algoritmo algoritmo;

    public void startTask(ArrayList<Aeropuerto> aeropuertosLoop, ArrayList<PlanVuelo> planVuelosLoop, VueloService vueloService,
                          PlanRutaService planRutaService, PaqueteService paqueteService,
                          PlanRutaXVueloService planRutaXVueloService, AeropuertoService aeropuertoService) {
        running.set(true);
        future = CompletableFuture.runAsync(() -> {
            if (running.get()) {
                algoritmo.loopPrincipalDiaADia(aeropuertosLoop, planVuelosLoop,
                        vueloService, planRutaService, paqueteService, planRutaXVueloService, aeropuertoService,
                        120, 60);
            }
        });
    }

    public void stopTask() {
        running.set(false);
        if (future != null && !future.isDone() && !future.isCancelled()) {
            future.cancel(true);
        }
    }
}