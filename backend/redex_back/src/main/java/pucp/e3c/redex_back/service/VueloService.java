package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
}
