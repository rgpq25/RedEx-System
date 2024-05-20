package pucp.e3c.redex_back.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import pucp.e3c.redex_back.service.PlanRutaService;
import pucp.e3c.redex_back.service.PlanRutaXVueloService;
import pucp.e3c.redex_back.service.VueloService;

public class Algoritmo {

    private final SimpMessagingTemplate messagingTemplate;

    public Algoritmo(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public ArrayList<PlanRutaNT> loopPrincipal(ArrayList<Aeropuerto> aeropuertos,
            ArrayList<PlanVuelo> planVuelos, ArrayList<Paquete> paquetes, VueloService vueloService,
            PlanRutaService planRutaService, PlanRutaXVueloService planRutaXVueloService, int id_simulacion) {

        ArrayList<PlanRutaNT> planRutas = new ArrayList<>();

        if (paquetes.size() == 0) {
            System.out.println("ERROR: No hay paquetes para procesar.");
            return null;
        }
        if (planVuelos.size() == 0) {
            System.out.println("ERROR: No hay planes de vuelo para procesar.");
            return null;
        }

        // recorrer los paquetes por cada 50
        int tamanhoPaquetes = 50;
        HashMap<Integer, Integer> ocupacionVuelos = new HashMap<Integer, Integer>();
        GrafoVuelos grafoVuelos = new GrafoVuelos(planVuelos, paquetes);

        for (int i = 0; i < paquetes.size(); i += tamanhoPaquetes) {
            ArrayList<Paquete> paquetesTemp = new ArrayList<>();
            for (int j = i; j < i + tamanhoPaquetes; j++) {
                if (j < paquetes.size()) {
                    paquetesTemp.add(paquetes.get(j));
                }
            }
            RespuestaAlgoritmo respuestaAlgoritmo = procesarPaquetes(grafoVuelos, ocupacionVuelos, paquetesTemp,
                    aeropuertos, planVuelos,
                    tamanhoPaquetes, i, vueloService, planRutaService, id_simulacion);
            for (int idx = 0; idx < respuestaAlgoritmo.getPlanesRutas().size(); idx++) {
                PlanRutaNT planRutaNT = respuestaAlgoritmo.getPlanesRutas().get(idx);
                Paquete paquete = paquetesTemp.get(idx);

                planRutaNT.updateCodigo();

                // Crear y guardar PlanRuta
                PlanRuta planRuta = new PlanRuta();
                planRuta.setCodigo(planRutaNT.getCodigo());

                Simulacion simulacion = new Simulacion();
                simulacion.setId(id_simulacion);
                planRuta.setSimulacionActual(simulacion);
                planRuta.setPaquete(paquete);
                planRuta = planRutaService.register(planRuta);

                // Asociar cada PlanRuta con sus vuelos
                for (Vuelo vuelo : planRutaNT.getVuelos()) {
                    PlanRutaXVuelo planRutaXVuelo = new PlanRutaXVuelo();
                    planRutaXVuelo.setPlanRuta(planRuta);
                    planRutaXVuelo.setVuelo(vuelo);
                    planRutaXVuelo.setIndiceDeOrden(planRutaNT.getVuelos().indexOf(vuelo));
                    planRutaXVueloService.register(planRutaXVuelo);
                }

            }

            messagingTemplate.convertAndSend("/tema/mensajes", respuestaAlgoritmo);
            // System.out.println("PlanRutas: " + planRutas.size());

            planRutas.addAll(respuestaAlgoritmo.getPlanesRutas());
        }
        return planRutas;

    }

    public static RespuestaAlgoritmo procesarPaquetes(GrafoVuelos grafoVuelos,
            HashMap<Integer, Integer> ocupacionVuelos, ArrayList<Paquete> paquetes,
            ArrayList<Aeropuerto> aeropuertos, ArrayList<PlanVuelo> planVuelos, int tamanhoPaquetes, int iteracion,
            VueloService vueloService, PlanRutaService planRutaService, int id_simulacion) {
        // Simmulated Annealing Parameters
        double temperature = 1000;
        double coolingRate = 0.08;
        int neighbourCount = 10;
        int windowSize = tamanhoPaquetes / 5;
        boolean stopWhenNoPackagesLeft = true;

        // Weight Parameters
        double badSolutionPenalization = 100;
        double flightPenalization = 200;
        double airportPenalization = 300;

        // funcion_fitness = (SUMA_TOTAL_PAQUETES) * 10 + (SUMA_TOTAL_VUELOS) * 4 +
        // (PROMEDIO_PONDERADO_TIEMPO_AEROPUERTO) * 4
        double sumaPaquetesWeight = 10;
        double sumaVuelosWeight = 4;
        double promedioPonderadoTiempoAeropuertoWeight = 4;

        SAImplementation sa = new SAImplementation();

        sa.setData(
                aeropuertos,
                planVuelos,
                paquetes, ocupacionVuelos);

        sa.setParameters(
                stopWhenNoPackagesLeft,
                temperature,
                coolingRate,
                neighbourCount,
                windowSize,
                badSolutionPenalization,
                flightPenalization,
                airportPenalization,
                sumaPaquetesWeight,
                sumaVuelosWeight,
                promedioPonderadoTiempoAeropuertoWeight);

        return sa.startAlgorithm(grafoVuelos, vueloService, planRutaService, id_simulacion);
    }
}
