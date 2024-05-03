package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pucp.e3c.redex_back.model.PlanVuelo;
import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.repository.PlanVueloRepository;

@Component
public class PlanVueloService {
    @Autowired
    private PlanVueloRepository planVueloRepository;

    public PlanVuelo register(PlanVuelo PlanVuelo) {
        return planVueloRepository.save(PlanVuelo);
    }

    public PlanVuelo get(Integer id) {
        Optional<PlanVuelo> optional_aeropuerto = planVueloRepository.findById(id);
        return optional_aeropuerto.get();
    }

    public List<PlanVuelo> getAll() {
        return planVueloRepository.findAll();
    }

    public void delete(Integer id) {
        planVueloRepository.deleteById(id);
    }

    public PlanVuelo update(PlanVuelo planVuelo) {
        return planVueloRepository.save(planVuelo);
    }

    public ArrayList<PlanVuelo> findByCiudadOrigen(Ubicacion origen) {
        return planVueloRepository.findByCiudadOrigen(origen);
    }

    public ArrayList<PlanVuelo> findByCiudadDestino(Ubicacion destino) {
        return planVueloRepository.findByCiudadDestino(destino);
    }

    public ArrayList<PlanVuelo> findByCiudadOrigenAndCiudadDestino(Ubicacion origen, Ubicacion destino) {
        return planVueloRepository.findByCiudadOrigenAndCiudadDestino(origen, destino);
    }
}
