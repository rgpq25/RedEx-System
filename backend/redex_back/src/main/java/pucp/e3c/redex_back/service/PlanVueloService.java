package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pucp.e3c.redex_back.model.PlanVuelo;
import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.repository.PlanVueloRepository;
import pucp.e3c.redex_back.repository.UbicacionRepository;

@Component
public class PlanVueloService {
    @Autowired
    private PlanVueloRepository planVueloRepository;

    @Autowired
    private UbicacionRepository ubicacionRepository;

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

    public ArrayList<PlanVuelo> findByCiudadOrigen(String origen) {
        Optional<Ubicacion> ubicacion = ubicacionRepository.findById(origen);
        return planVueloRepository.findByCiudadOrigen(ubicacion.get());
    }

    public ArrayList<PlanVuelo> findByCiudadDestino(String destino) {
        Optional<Ubicacion> ubicacion = ubicacionRepository.findById(destino);
        return planVueloRepository.findByCiudadDestino(ubicacion.get());
    }

    public ArrayList<PlanVuelo> findByCiudadOrigenAndCiudadDestino(String origen, String destino) {
        Optional<Ubicacion> ubicacionOrigen = ubicacionRepository.findById(origen);
        Optional<Ubicacion> ubicacionDestino = ubicacionRepository.findById(destino);
        return planVueloRepository.findByCiudadOrigenAndCiudadDestino(ubicacionOrigen.get(), ubicacionDestino.get());
    }
}
