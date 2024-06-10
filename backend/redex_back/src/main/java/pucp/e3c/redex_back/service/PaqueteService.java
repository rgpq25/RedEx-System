package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.Vuelo;
import pucp.e3c.redex_back.repository.PaqueteRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaqueteService {
    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private PlanRutaXVueloService planRutaXVueloService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaqueteService.class);

    public Paquete register(Paquete paquete) {
        // return paqueteRepository.save(paquete);
        try {
            return paqueteRepository.save(paquete);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Paquete> registerAll(ArrayList<Paquete> paquete) {
        try {
            return (ArrayList<Paquete>) paqueteRepository.saveAll(paquete);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    private boolean isAfterByMoreThanFiveMinutes(Date date1, Date date2) {
        long differenceInMillis = date1.getTime() - date2.getTime();
        long fiveMinutesInMillis = 5 * 60 * 1000; // 5 minutes in milliseconds

        return differenceInMillis > fiveMinutesInMillis;
    }

    public Paquete actualizaEstadoPaqueteNoSimulacion(Paquete paquete) {
        try {
            if (paquete.getPlanRutaActual() == null) {
                paquete.setEstado("En almacen origen");
            } else {
                ArrayList<Vuelo> vuelos = (ArrayList<Vuelo>) planRutaXVueloService
                        .findVuelosByPlanRutaOrdenadosIndice(paquete.getPlanRutaActual().getId());
                if (vuelos == null)
                    paquete.setEstado("En almacen origen");
                else {
                    Date fechaActual = new Date();
                    int i = 0;
                    for (Vuelo vuelo : vuelos) {
                        if (vuelo.getFechaSalida().after(fechaActual)) {
                            if (i == 0) {
                                paquete.setEstado("En almacen origen");
                            } else {
                                paquete.setEstado("En espera");
                            }
                            break;
                        } else if (vuelo.getFechaLlegada().before(fechaActual) && i == vuelos.size() - 1) {
                            if (isAfterByMoreThanFiveMinutes(fechaActual, vuelo.getFechaLlegada())) {
                                paquete.setEstado("Entregado");
                                paquete.setEntregado(true);
                            } else {
                                paquete.setEstado("En almacen destino");
                            }
                            break;
                        } else if (vuelo.getFechaSalida().before(fechaActual)
                                && vuelo.getFechaLlegada().after(fechaActual)) {
                            paquete.setEstado("Volando");
                            break;
                        }

                        i++;
                    }
                }
            }
            update(paquete);
            return paquete;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Paquete get(Integer id) {
        try {
            Optional<Paquete> optional_paquete = paqueteRepository.findById(id);
            Paquete paquete = optional_paquete.get();

            return optional_paquete.get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Paquete getPaqueteNoSimulacion(Integer id) {
        try {
            Paquete paquete = get(id);
            return actualizaEstadoPaqueteNoSimulacion(paquete);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Paquete> getAll() {
        // return paqueteRepository.findAll();
        try {
            return paqueteRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(Integer id) {
        // paqueteRepository.deleteById(id);
        try {
            paqueteRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Paquete update(Paquete paquete) {
        // return paqueteRepository.save(paquete);
        try {
            return paqueteRepository.save(paquete);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    // ...

    public List<Paquete> findByAeropuertoActualId(Integer id) {
        // return paqueteRepository.findByAeropuertoActualId(id);
        try {
            return paqueteRepository.findByAeropuertoActualId(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Paquete> findByEnvioId(Integer id) {
        // return paqueteRepository.findByEnvioId(id);
        try {
            return paqueteRepository.findByEnvioId(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Paquete> findBySimulacionId(Integer id) {
        return paqueteRepository.findBySimulacionActualId(id);

    }

    public Paquete findByPlanRutaId(Integer id) {
        // return paqueteRepository.findByPlanRutaActualId(id);
        try {
            return paqueteRepository.findByPlanRutaActualId(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Paquete> findPaquetesWithoutPlanRutaSimulacion(String idUbicacionOrigen, Integer idSimulacion,
            Date fechaCorte) {
        // return
        // paqueteRepository.findPaquetesWithoutPlanRutaSimulacion(idUbicacionOrigen,
        // idSimulacion, fechaCorte);
        try {
            return paqueteRepository.findPaquetesWithoutPlanRutaSimulacion(idUbicacionOrigen, idSimulacion, fechaCorte);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Paquete> findPaqueteSimulacionFechaCorte(int idSimulacion, Date fechaCorte) {
        // return
        // paqueteRepository.findPaquetesWithoutPlanRutaSimulacion(idUbicacionOrigen,
        // idSimulacion, fechaCorte);
        try {
            return paqueteRepository.findPaqueteSimulacionFechaCorte(idSimulacion, fechaCorte);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Paquete> findPaquetesSinSimulacionYNoEntregados() {
        try {
            return paqueteRepository.findPaquetesSinSimulacionYNoEntregados();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Paquete> findPaquetesWithoutPlanRuta(String idUbicacionOrigen, Date fechaCorte) {
        try {
            return paqueteRepository.findPaquetesWithoutPlanRuta(idUbicacionOrigen, fechaCorte);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Paquete> findPaquetesOperacionesDiaDia() {
        try {
            return paqueteRepository.findPaquetesOperacionesDiaDia();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

}
