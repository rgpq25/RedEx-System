package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.PlanRuta;
import pucp.e3c.redex_back.model.PlanRutaXVuelo;
import pucp.e3c.redex_back.model.Vuelo;
import pucp.e3c.redex_back.repository.PlanRutaRepository;
import pucp.e3c.redex_back.repository.PlanRutaXVueloRepository;
import pucp.e3c.redex_back.repository.VueloRepository;

@Service
public class PlanRutaXVueloService {
    @Autowired
    private PlanRutaXVueloRepository planRutaXVueloRepository;

    @Autowired
    VueloRepository vueloRepository;

    @Autowired
    PlanRutaRepository planRutaRepository;

    public PlanRutaXVuelo register(PlanRutaXVuelo planRutaXVuelo) {
        return planRutaXVueloRepository.save(planRutaXVuelo);
    }

    public PlanRutaXVuelo update(PlanRutaXVuelo planRutaXVuelo) {
        return planRutaXVueloRepository.save(planRutaXVuelo);
    }

    public void delete(int id) {
        planRutaXVueloRepository.deleteById(id);
    }

    public List<PlanRutaXVuelo> getAll() {
        return planRutaXVueloRepository.findAll();
    }

    public PlanRutaXVuelo get(int id) {
        return planRutaXVueloRepository.findById(id).orElse(null);
    }

    public List<Vuelo> findVuelosByPlanRuta(int planRutaId) {
        List<PlanRutaXVuelo> planRutaXVuelos = planRutaXVueloRepository.findByPlanRutaId(planRutaId);
        List<Vuelo> vuelos = new ArrayList<Vuelo>();
        for (PlanRutaXVuelo planRutaXVuelo : planRutaXVuelos) {
            vueloRepository.findById(planRutaXVuelo.getVuelo().getId()).ifPresent(vuelos::add);
        }
        Collections.sort(vuelos, (vuelo1, vuelo2) -> vuelo1.getFechaSalida().compareTo(vuelo2.getFechaSalida()));
        return vuelos;
    }

    public List<PlanRutaXVuelo> findByPlanRutaId(int idPlan) {
        return planRutaXVueloRepository.findByPlanRutaId(idPlan);
    }

    public List<PlanRuta> findPlanesRutaByVuelo(Integer idVuelo){
        List<PlanRutaXVuelo> planRutaXVuelos = planRutaXVueloRepository.findByVueloId(idVuelo);
        List<PlanRuta> planesRuta = new ArrayList<PlanRuta>();
        for (PlanRutaXVuelo planRutaXVuelo : planRutaXVuelos) {
            planRutaRepository.findById(planRutaXVuelo.getPlanRuta().getId()).ifPresent(planesRuta::add);
        }
        return planesRuta;
    }

    public List<PlanRutaXVuelo> findByVueloId(Integer id) {
        return planRutaXVueloRepository.findByVueloId(id);
    }
}
