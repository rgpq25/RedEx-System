package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.model.PlanRuta;
import pucp.e3c.redex_back.model.Vuelo;
import pucp.e3c.redex_back.repository.PlanRutaRepository;
import pucp.e3c.redex_back.repository.VueloRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PlanRutaService {
    @Autowired
    private PlanRutaRepository planRutaRepository;

    @Autowired
    private PlanRutaXVueloService planRutaXVueloService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PlanRutaService.class);

    public PlanRuta register(PlanRuta planRuta) {
        // return planRutaRepository.save(planRuta);
        try {
            return planRutaRepository.save(planRuta);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public PlanRuta update(PlanRuta planRuta) {
        // return planRutaRepository.save(planRuta);
        try {
            return planRutaRepository.save(planRuta);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public PlanRuta get(int id) {
        // Optional<PlanRuta> optional_planRuta = planRutaRepository.findById(id);
        // return optional_planRuta.get();

        try {
            Optional<PlanRuta> optional_planRuta = planRutaRepository.findById(id);
            return optional_planRuta.get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public PlanRuta findBySimulacion(int id) {
        try {
            Optional<PlanRuta> optional_planRuta = planRutaRepository.findBySimulacionActualId(id);
            return optional_planRuta.get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<PlanRuta> getAll() {
        // return planRutaRepository.findAll();
        try {
            return planRutaRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int id) {
        // planRutaRepository.deleteById(id);
        try {
            planRutaRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public ArrayList<PlanRuta> findActive(Date fechaActual) {
        /*
         * ArrayList<PlanRuta> planes = new ArrayList<PlanRuta>();
         * for (PlanRuta plan : planRutaRepository.findAll()) {
         * List<Vuelo> vuelos =
         * planRutaXVueloService.findVuelosByPlanRuta(plan.getId());
         * if (vuelos.size() > 0) {
         * Date fechaSalida = vuelos.get(0).getFechaSalida();
         * Date fechaLlegada = vuelos.get(vuelos.size() - 1).getFechaLlegada();
         * if (fechaActual.after(fechaSalida) && fechaLlegada.after(fechaActual)) {
         * planes.add(plan);
         * }
         * }
         * }
         * return planes;
         */
        try {
            ArrayList<PlanRuta> planes = new ArrayList<PlanRuta>();
            for (PlanRuta plan : planRutaRepository.findAll()) {
                List<Vuelo> vuelos = planRutaXVueloService.findVuelosByPlanRuta(plan.getId());
                if (vuelos.size() > 0) {
                    Date fechaSalida = vuelos.get(0).getFechaSalida();
                    Date fechaLlegada = vuelos.get(vuelos.size() - 1).getFechaLlegada();
                    if (fechaActual.after(fechaSalida) && fechaLlegada.after(fechaActual)) {
                        planes.add(plan);
                    }
                }
            }
            return planes;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
