package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.PlanVuelo;
import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.model.Vuelo;
import pucp.e3c.redex_back.repository.PlanVueloRepository;
import pucp.e3c.redex_back.repository.VueloRepository;

@Service
public class VueloService {
    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private PlanVueloRepository planVueloRepository;

    public Vuelo register(Vuelo vuelo) {
        return vueloRepository.save(vuelo);
    }

    public Vuelo update(Vuelo vuelo) {
        return vueloRepository.save(vuelo);
    }

    public Vuelo get(int vueloId) {
        return vueloRepository.findById(vueloId).orElse(null);
    }

    public List<Vuelo> getAll() {
        return vueloRepository.findAll();
    }

    public void delete(int vueloId) {
        vueloRepository.deleteById(vueloId);
    }

    public ArrayList<Vuelo> findByCiudadOrigen(Ubicacion ciudadOrigen) {
        ArrayList<PlanVuelo> planVuelos = planVueloRepository.findByCiudadOrigen(ciudadOrigen);
        ArrayList<Vuelo> vuelos = new ArrayList<>();
        for (PlanVuelo planVuelo : planVuelos) {
            ArrayList<Vuelo> vuelosDePlan = vueloRepository.findByPlanVueloId(planVuelo.getId());
            vuelos.addAll(vuelosDePlan);
        }
        return vuelos;
    }

    public ArrayList<Vuelo> findByCiudadDestino(Ubicacion ciudadDestino) {
        ArrayList<PlanVuelo> planVuelos = planVueloRepository.findByCiudadDestino(ciudadDestino);
        ArrayList<Vuelo> vuelos = new ArrayList<>();
        for (PlanVuelo planVuelo : planVuelos) {
            ArrayList<Vuelo> vuelosDePlan = vueloRepository.findByPlanVueloId(planVuelo.getId());
            vuelos.addAll(vuelosDePlan);
        }
        return vuelos;
    }

    public ArrayList<Vuelo> findByCiudadOrigenAndCiudadDestino(Ubicacion ciudadOrigen, Ubicacion ciudadDestino) {
        ArrayList<PlanVuelo> planVuelos = planVueloRepository.findByCiudadOrigenAndCiudadDestino(ciudadOrigen,
                ciudadDestino);
        ArrayList<Vuelo> vuelos = new ArrayList<>();
        for (PlanVuelo planVuelo : planVuelos) {
            ArrayList<Vuelo> vuelosDePlan = vueloRepository.findByPlanVueloId(planVuelo.getId());
            vuelos.addAll(vuelosDePlan);
        }
        return vuelos;
    }

    public ArrayList<Vuelo> findVuelosValidos(Ubicacion origen, Date fechaInicio, Date fechaFin) {
        return vueloRepository.findValidos(origen.getId(), fechaInicio, fechaFin);
    }

    public ArrayList<Vuelo> findVuelosValidosAeropuertoSimulacion(Integer idSimulacion,String idUbicacion) {
        return vueloRepository.findValidosAeropuertoSimulacion(idSimulacion,idUbicacion);
    }
}
