package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.PlanVuelo;
import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.repository.PlanVueloRepository;
import pucp.e3c.redex_back.repository.UbicacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PlanVueloService {
    @Autowired
    private PlanVueloRepository planVueloRepository;

    @Autowired
    private UbicacionRepository ubicacionRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PlanVueloService.class);

    public PlanVuelo register(PlanVuelo PlanVuelo) {
        //return planVueloRepository.save(PlanVuelo);
        try{
            return planVueloRepository.save(PlanVuelo);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public PlanVuelo get(Integer id) {
        //Optional<PlanVuelo> optional_aeropuerto = planVueloRepository.findById(id);
        //return optional_aeropuerto.get();
        try{
            Optional<PlanVuelo> optional_aeropuerto = planVueloRepository.findById(id);
            return optional_aeropuerto.get();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<PlanVuelo> getAll() {
        //return planVueloRepository.findAll();
        try{
            return planVueloRepository.findAll();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(Integer id) {
        //planVueloRepository.deleteById(id);
        try{
            planVueloRepository.deleteById(id);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    public PlanVuelo update(PlanVuelo planVuelo) {
        //return planVueloRepository.save(planVuelo);
        try{
            return planVueloRepository.save(planVuelo);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<PlanVuelo> findByCiudadOrigen(String origen) {
        //Optional<Ubicacion> ubicacion = ubicacionRepository.findById(origen);
        //return planVueloRepository.findByCiudadOrigen(ubicacion.get());
        try{
            Optional<Ubicacion> ubicacion = ubicacionRepository.findById(origen);
            return planVueloRepository.findByCiudadOrigen(ubicacion.get());
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<PlanVuelo> findByCiudadDestino(String destino) {
        //Optional<Ubicacion> ubicacion = ubicacionRepository.findById(destino);
        //return planVueloRepository.findByCiudadDestino(ubicacion.get());
        try{
            Optional<Ubicacion> ubicacion = ubicacionRepository.findById(destino);
            return planVueloRepository.findByCiudadDestino(ubicacion.get());
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<PlanVuelo> findByCiudadOrigenAndCiudadDestino(String origen, String destino) {
        //Optional<Ubicacion> ubicacionOrigen = ubicacionRepository.findById(origen);
        //Optional<Ubicacion> ubicacionDestino = ubicacionRepository.findById(destino);
        //return planVueloRepository.findByCiudadOrigenAndCiudadDestino(ubicacionOrigen.get(), ubicacionDestino.get());
        try{
            Optional<Ubicacion> ubicacionOrigen = ubicacionRepository.findById(origen);
            Optional<Ubicacion> ubicacionDestino = ubicacionRepository.findById(destino);
            return planVueloRepository.findByCiudadOrigenAndCiudadDestino(ubicacionOrigen.get(), ubicacionDestino.get());
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
