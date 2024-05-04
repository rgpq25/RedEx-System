package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.PlanRuta;
import pucp.e3c.redex_back.model.Vuelo;
import pucp.e3c.redex_back.repository.PlanRutaRepository;
import pucp.e3c.redex_back.repository.VueloRepository;

@Service
public class PlanRutaService {
    @Autowired
    private PlanRutaRepository planRutaRepository;

    @Autowired
    private PlanRutaXVueloService planRutaXVueloService;

    public PlanRuta register(PlanRuta planRuta) {
        return planRutaRepository.save(planRuta);
    }

    public PlanRuta update(PlanRuta planRuta) {
        return planRutaRepository.save(planRuta);
    }

    public PlanRuta get(int id) {
        return planRutaRepository.findById(id);
    }

    public List<PlanRuta> getAll() {
        return planRutaRepository.findAll();
    }

    public void delete(int id) {
        planRutaRepository.deleteById(id);
    }

    public ArrayList<PlanRuta> findActive(Date fechaActual) {
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
    }
}
