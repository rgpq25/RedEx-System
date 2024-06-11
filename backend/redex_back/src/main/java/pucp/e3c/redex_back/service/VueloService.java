package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.PlanRuta;
import pucp.e3c.redex_back.model.PlanRutaXVuelo;
import pucp.e3c.redex_back.model.PlanVuelo;
import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.model.Vuelo;
import pucp.e3c.redex_back.repository.PaqueteRepository;
import pucp.e3c.redex_back.repository.PlanRutaRepository;
import pucp.e3c.redex_back.repository.PlanRutaXVueloRepository;
import pucp.e3c.redex_back.repository.PlanVueloRepository;
import pucp.e3c.redex_back.repository.VueloRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VueloService {
    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private PlanRutaRepository planRutaRepository;

    @Autowired
    private PlanRutaXVueloRepository planRutaXVueloRepository;

    @Autowired
    private PlanVueloRepository planVueloRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(VueloService.class);

    public Vuelo register(Vuelo vuelo) {
        // return vueloRepository.save(vuelo);
        try {
            return vueloRepository.save(vuelo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Vuelo> findVuelosByPaqueteId(int idPaquete) {
        Optional<Paquete> optionalPaquete = paqueteRepository.findById(idPaquete);
        if (optionalPaquete.isPresent()) {
            Paquete paquete = optionalPaquete.get();
            if (paquete.getPlanRutaActual() == null) {
                return null;
            }
            Optional<PlanRuta> optionalPlanRuta = planRutaRepository.findById(paquete.getPlanRutaActual().getId());
            if (optionalPlanRuta.isPresent()) {
                PlanRuta planRuta = optionalPlanRuta.get();
                ArrayList<PlanRutaXVuelo> planRutaXVuelos = new ArrayList<PlanRutaXVuelo>(
                        planRutaXVueloRepository.findByPlanRutaId(planRuta.getId()));
                planRutaXVuelos.sort(Comparator.comparingInt(PlanRutaXVuelo::getIndiceDeOrden));
                ArrayList<Vuelo> vuelos = new ArrayList<>();
                for (PlanRutaXVuelo planRutaXVuelo : planRutaXVuelos) {
                    Vuelo vuelo = vueloRepository.findById(planRutaXVuelo.getVuelo().getId()).get();
                    vuelos.add(vuelo);
                }
                return vuelos;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public HashMap<Integer, ArrayList<Vuelo>> findVuelosByPaqueteIds(List<Paquete> paquetes) {
        List<Integer> paqueteIds = new ArrayList<>();
        for (Paquete paquete : paquetes) {
            paqueteIds.add(paquete.getId());
        }
        paquetes = paqueteRepository.findAllById(paqueteIds);

        HashMap<Integer, ArrayList<Vuelo>> vuelosPorPaquete = new HashMap<>();

        if (paquetes.isEmpty()) {
            return vuelosPorPaquete;
        }

        // Collect PlanRuta ids from the paquetes
        List<Integer> planRutaIds = paquetes.stream()
                .filter(p -> p.getPlanRutaActual() != null)
                .map(p -> p.getPlanRutaActual().getId())
                .collect(Collectors.toList());

        // Map PlanRuta ids to corresponding PlanRutaXVuelos in a single query
        HashMap<Integer, List<PlanRutaXVuelo>> planRutaXVuelosMap = (HashMap<Integer, List<PlanRutaXVuelo>>) planRutaXVueloRepository
                .findAllById(planRutaIds)
                .stream()
                .collect(Collectors.groupingBy(prxv -> prxv.getPlanRuta().getId()));

        // Collect all Vuelo ids involved
        List<Integer> vueloIds = planRutaXVuelosMap.values().stream()
                .flatMap(Collection::stream)
                .map(prxv -> prxv.getVuelo().getId())
                .distinct()
                .collect(Collectors.toList());

        // Get all Vuelos in one query
        HashMap<Integer, Vuelo> vuelosMap = (HashMap<Integer, Vuelo>) vueloRepository.findAllById(vueloIds)
                .stream()
                .collect(Collectors.toMap(Vuelo::getId, Function.identity()));

        // Build the final map of vuelos by paquete id
        for (Paquete paquete : paquetes) {
            if (paquete.getPlanRutaActual() != null) {
                List<Vuelo> vuelos = planRutaXVuelosMap
                        .getOrDefault(paquete.getPlanRutaActual().getId(), new ArrayList<>())
                        .stream()
                        .map(prxv -> vuelosMap.get(prxv.getVuelo().getId()))
                        .collect(Collectors.toCollection(ArrayList::new));
                vuelosPorPaquete.put(paquete.getId(), new ArrayList<>(vuelos));
            } else {
                vuelosPorPaquete.put(paquete.getId(), new ArrayList<>());
            }
        }

        return vuelosPorPaquete;
    }

    public Vuelo update(Vuelo vuelo) {
        // return vueloRepository.save(vuelo);
        try {
            return vueloRepository.save(vuelo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Vuelo get(int vueloId) {
        // return vueloRepository.findById(vueloId).orElse(null);
        try {
            return vueloRepository.findById(vueloId).orElse(null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Vuelo> getAll() {
        // return vueloRepository.findAll();
        try {
            return vueloRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int vueloId) {
        // vueloRepository.deleteById(vueloId);
        try {
            vueloRepository.deleteById(vueloId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public ArrayList<Vuelo> findByCiudadOrigen(Ubicacion ciudadOrigen) {
        /*
         * ArrayList<PlanVuelo> planVuelos =
         * planVueloRepository.findByCiudadOrigen(ciudadOrigen);
         * ArrayList<Vuelo> vuelos = new ArrayList<>();
         * for (PlanVuelo planVuelo : planVuelos) {
         * ArrayList<Vuelo> vuelosDePlan =
         * vueloRepository.findByPlanVueloId(planVuelo.getId());
         * vuelos.addAll(vuelosDePlan);
         * }
         * return vuelos;
         */
        try {
            ArrayList<PlanVuelo> planVuelos = planVueloRepository.findByCiudadOrigen(ciudadOrigen);
            ArrayList<Vuelo> vuelos = new ArrayList<>();
            for (PlanVuelo planVuelo : planVuelos) {
                ArrayList<Vuelo> vuelosDePlan = vueloRepository.findByPlanVueloId(planVuelo.getId());
                vuelos.addAll(vuelosDePlan);
            }
            return vuelos;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public HashMap<Integer, ArrayList<Vuelo>> findByAllPaqueteId(ArrayList<Paquete> paquetes) {
        HashMap<Integer, ArrayList<Vuelo>> hashVuelosXPaquete = new HashMap<>();
        if (paquetes.size() <= 0) {
            return new HashMap<Integer, ArrayList<Vuelo>>();
        }
        for (Paquete paquete : paquetes) {
            ArrayList<Vuelo> vuelos = findVuelosByPaqueteId(paquete.getId());
            if (vuelos != null) {
                hashVuelosXPaquete.put(paquete.getId(), vuelos);
            } else {
                hashVuelosXPaquete.put(paquete.getId(), new ArrayList<Vuelo>());
            }
        }

        return hashVuelosXPaquete;

    }

    public ArrayList<Vuelo> findByCiudadDestino(Ubicacion ciudadDestino) {
        /*
         * ArrayList<PlanVuelo> planVuelos =
         * planVueloRepository.findByCiudadDestino(ciudadDestino);
         * ArrayList<Vuelo> vuelos = new ArrayList<>();
         * for (PlanVuelo planVuelo : planVuelos) {
         * ArrayList<Vuelo> vuelosDePlan =
         * vueloRepository.findByPlanVueloId(planVuelo.getId());
         * vuelos.addAll(vuelosDePlan);
         * }
         * return vuelos;
         */
        try {
            ArrayList<PlanVuelo> planVuelos = planVueloRepository.findByCiudadDestino(ciudadDestino);
            ArrayList<Vuelo> vuelos = new ArrayList<>();
            for (PlanVuelo planVuelo : planVuelos) {
                ArrayList<Vuelo> vuelosDePlan = vueloRepository.findByPlanVueloId(planVuelo.getId());
                vuelos.addAll(vuelosDePlan);
            }
            return vuelos;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Vuelo> findByCiudadOrigenAndCiudadDestino(Ubicacion ciudadOrigen, Ubicacion ciudadDestino) {
        /*
         * ArrayList<PlanVuelo> planVuelos =
         * planVueloRepository.findByCiudadOrigenAndCiudadDestino(ciudadOrigen,
         * ciudadDestino);
         * ArrayList<Vuelo> vuelos = new ArrayList<>();
         * for (PlanVuelo planVuelo : planVuelos) {
         * ArrayList<Vuelo> vuelosDePlan =
         * vueloRepository.findByPlanVueloId(planVuelo.getId());
         * vuelos.addAll(vuelosDePlan);
         * }
         * return vuelos;
         */
        try {
            ArrayList<PlanVuelo> planVuelos = planVueloRepository.findByCiudadOrigenAndCiudadDestino(ciudadOrigen,
                    ciudadDestino);
            ArrayList<Vuelo> vuelos = new ArrayList<>();
            for (PlanVuelo planVuelo : planVuelos) {
                ArrayList<Vuelo> vuelosDePlan = vueloRepository.findByPlanVueloId(planVuelo.getId());
                vuelos.addAll(vuelosDePlan);
            }
            return vuelos;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Vuelo> findVuelosValidos(Ubicacion origen, Date fechaInicio, Date fechaFin) {
        // return vueloRepository.findValidos(origen.getId(), fechaInicio, fechaFin);
        try {
            return vueloRepository.findValidos(origen.getId(), fechaInicio, fechaFin);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Vuelo> findVuelosValidosAeropuertoSimulacion(Integer idSimulacion, String idUbicacion) {
        // return
        // vueloRepository.findValidosAeropuertoSimulacion(idSimulacion,idUbicacion);
        try {
            return vueloRepository.findValidosAeropuertoSimulacion(idSimulacion, idUbicacion);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Vuelo> findVuelosDestinoAeropuertoSimulacionFecha(Integer idSimulacion, String idUbicacion,
            Date fechaCorte) {
        // return
        // vueloRepository.findVuelosDestinoAeropuertoSimulacionFechaCorte(idSimulacion,
        // idUbicacion, fechaCorte);
        try {
            return vueloRepository.findVuelosDestinoAeropuertoSimulacionFechaCorte(idSimulacion, idUbicacion,
                    fechaCorte);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;

        }
    }

    public ArrayList<Vuelo> findVuelosOrigenAeropuertoSimulacionFecha(Integer idSimulacion, String idUbicacion,
            Date fechaCorte) {
        // return
        // vueloRepository.findVuelosOrigenAeropuertoSimulacionFechaCorte(idSimulacion,
        // idUbicacion, fechaCorte);
        try {
            return vueloRepository.findVuelosOrigenAeropuertoSimulacionFechaCorte(idSimulacion, idUbicacion,
                    fechaCorte);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Vuelo> findVuelosDestinoAeropuertoFechaCorte(String idUbicacion, Date fechaCorte) {
        try {
            return vueloRepository.findVuelosDestinoAeropuertoFechaCorte(idUbicacion, fechaCorte);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Vuelo> findVuelosOrigenAeropuertoFechaCorte(String idUbicacion, Date fechaCorte) {
        try {
            return vueloRepository.findVuelosOrigenAeropuertoFechaCorte(idUbicacion, fechaCorte);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void deleteAll() {
        try {
            vueloRepository.deleteAll();
            vueloRepository.flush();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
