package pucp.e3c.redex_back;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.model.Algoritmo;
import pucp.e3c.redex_back.model.Funciones;
import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.PlanVuelo;
import pucp.e3c.redex_back.model.Simulacion;
import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.service.AeropuertoService;
import pucp.e3c.redex_back.service.EnvioService;
import pucp.e3c.redex_back.service.PaqueteService;
import pucp.e3c.redex_back.service.PlanVueloService;
import pucp.e3c.redex_back.service.SimulacionService;
import pucp.e3c.redex_back.service.UbicacionService;

@Component
public class DataInitializer {
    @Autowired
    private PlanVueloService planVueloService;

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private AeropuertoService aeropuertoService;

    @Autowired
    private PaqueteService paqueteService;

    @Autowired
    private SimulacionService simulacionService;

    @Autowired
    private EnvioService envioService;

    @PostConstruct
    public void initData() {
        System.out.println("Inicializando planes de vuelo y aeropuertos");
        String inputPath = "src\\main\\resources\\dataFija";

        ArrayList<Aeropuerto> aeropuertos = new ArrayList<Aeropuerto>();
        ArrayList<PlanVuelo> planVuelos = new ArrayList<PlanVuelo>();
        ArrayList<Paquete> paquetes = new ArrayList<>();
        HashMap<String, Ubicacion> ubicacionMap = Funciones.getUbicacionMap();

        aeropuertos = Funciones.leerAeropuertos(inputPath, ubicacionMap);
        planVuelos = Funciones.leerPlanesVuelo(ubicacionMap, inputPath);

        String startPackagesDate = "2024-01-01 00:00:00";
        String endPackagesDate = "2024-01-04 23:59:59";
        paquetes = Funciones.generarPaquetes(
                1000,
                aeropuertos,
                Funciones.parseDateString(startPackagesDate),
                Funciones.parseDateString(endPackagesDate));

        System.out.println("Se generaron " + paquetes.size() + " paquetes.");

        Date minDate = Funciones.parseDateString(startPackagesDate);
        Date maxDate = Funciones.parseDateString(endPackagesDate);

        System.out.println("Fecha minima de recepcion de paquetes: " + Funciones.getFormattedDate(minDate));
        System.out.println("Fecha maxima de recepcion de paquetes: " + Funciones.getFormattedDate(maxDate));

        Simulacion simulacion = new Simulacion();
        simulacion.fillData();

        simulacion = simulacionService.register(simulacion);

        for (Ubicacion ubicacion : ubicacionMap.values()) {
            ubicacionService.register(ubicacion);
        }

        for (Aeropuerto aeropuerto : aeropuertos) {
            aeropuertoService.register(aeropuerto);
        }

        for (PlanVuelo planVuelo : planVuelos) {
            planVueloService.register(planVuelo);
        }

        for (Paquete paquete : paquetes) {
            envioService.register(paquete.getEnvio());
            paquete.setSimulacionActual(simulacion);
            paqueteService.register(paquete);
        }

    }
}
