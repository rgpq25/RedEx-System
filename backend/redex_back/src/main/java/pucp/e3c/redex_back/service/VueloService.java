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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VueloService {
    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private PlanVueloRepository planVueloRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(VueloService.class);

    public Vuelo register(Vuelo vuelo) {
        //return vueloRepository.save(vuelo);
        try{
            return vueloRepository.save(vuelo);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Vuelo update(Vuelo vuelo) {
        //return vueloRepository.save(vuelo);
        try{
            return vueloRepository.save(vuelo);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Vuelo get(int vueloId) {
        //return vueloRepository.findById(vueloId).orElse(null);
        try{
            return vueloRepository.findById(vueloId).orElse(null);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Vuelo> getAll() {
        //return vueloRepository.findAll();
        try{
            return vueloRepository.findAll();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int vueloId) {
        //vueloRepository.deleteById(vueloId);
        try{
            vueloRepository.deleteById(vueloId);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    public ArrayList<Vuelo> findByCiudadOrigen(Ubicacion ciudadOrigen) {
        /*ArrayList<PlanVuelo> planVuelos = planVueloRepository.findByCiudadOrigen(ciudadOrigen);
        ArrayList<Vuelo> vuelos = new ArrayList<>();
        for (PlanVuelo planVuelo : planVuelos) {
            ArrayList<Vuelo> vuelosDePlan = vueloRepository.findByPlanVueloId(planVuelo.getId());
            vuelos.addAll(vuelosDePlan);
        }
        return vuelos;*/
        try{
            ArrayList<PlanVuelo> planVuelos = planVueloRepository.findByCiudadOrigen(ciudadOrigen);
            ArrayList<Vuelo> vuelos = new ArrayList<>();
            for (PlanVuelo planVuelo : planVuelos) {
                ArrayList<Vuelo> vuelosDePlan = vueloRepository.findByPlanVueloId(planVuelo.getId());
                vuelos.addAll(vuelosDePlan);
            }
            return vuelos;
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Vuelo> findByCiudadDestino(Ubicacion ciudadDestino) {
        /*ArrayList<PlanVuelo> planVuelos = planVueloRepository.findByCiudadDestino(ciudadDestino);
        ArrayList<Vuelo> vuelos = new ArrayList<>();
        for (PlanVuelo planVuelo : planVuelos) {
            ArrayList<Vuelo> vuelosDePlan = vueloRepository.findByPlanVueloId(planVuelo.getId());
            vuelos.addAll(vuelosDePlan);
        }
        return vuelos;*/
        try{
            ArrayList<PlanVuelo> planVuelos = planVueloRepository.findByCiudadDestino(ciudadDestino);
            ArrayList<Vuelo> vuelos = new ArrayList<>();
            for (PlanVuelo planVuelo : planVuelos) {
                ArrayList<Vuelo> vuelosDePlan = vueloRepository.findByPlanVueloId(planVuelo.getId());
                vuelos.addAll(vuelosDePlan);
            }
            return vuelos;
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Vuelo> findByCiudadOrigenAndCiudadDestino(Ubicacion ciudadOrigen, Ubicacion ciudadDestino) {
        /*ArrayList<PlanVuelo> planVuelos = planVueloRepository.findByCiudadOrigenAndCiudadDestino(ciudadOrigen,
                ciudadDestino);
        ArrayList<Vuelo> vuelos = new ArrayList<>();
        for (PlanVuelo planVuelo : planVuelos) {
            ArrayList<Vuelo> vuelosDePlan = vueloRepository.findByPlanVueloId(planVuelo.getId());
            vuelos.addAll(vuelosDePlan);
        }
        return vuelos;*/
        try{
            ArrayList<PlanVuelo> planVuelos = planVueloRepository.findByCiudadOrigenAndCiudadDestino(ciudadOrigen,
                    ciudadDestino);
            ArrayList<Vuelo> vuelos = new ArrayList<>();
            for (PlanVuelo planVuelo : planVuelos) {
                ArrayList<Vuelo> vuelosDePlan = vueloRepository.findByPlanVueloId(planVuelo.getId());
                vuelos.addAll(vuelosDePlan);
            }
            return vuelos;
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Vuelo> findVuelosValidos(Ubicacion origen, Date fechaInicio, Date fechaFin) {
        //return vueloRepository.findValidos(origen.getId(), fechaInicio, fechaFin);
        try{
            return vueloRepository.findValidos(origen.getId(), fechaInicio, fechaFin);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Vuelo> findVuelosValidosAeropuertoSimulacion(Integer idSimulacion,String idUbicacion) {
        //return vueloRepository.findValidosAeropuertoSimulacion(idSimulacion,idUbicacion);
        try{
            return vueloRepository.findValidosAeropuertoSimulacion(idSimulacion,idUbicacion);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Vuelo> findVuelosDestinoAeropuertoSimulacionFecha(Integer idSimulacion, String idUbicacion, Date fechaCorte) {
        //return vueloRepository.findVuelosDestinoAeropuertoSimulacionFechaCorte(idSimulacion, idUbicacion, fechaCorte);
        try{
            return vueloRepository.findVuelosDestinoAeropuertoSimulacionFechaCorte(idSimulacion, idUbicacion, fechaCorte);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        
        }
    }

    public ArrayList<Vuelo> findVuelosOrigenAeropuertoSimulacionFecha(Integer idSimulacion, String idUbicacion, Date fechaCorte) {
        //return vueloRepository.findVuelosOrigenAeropuertoSimulacionFechaCorte(idSimulacion, idUbicacion, fechaCorte);
        try{
            return vueloRepository.findVuelosOrigenAeropuertoSimulacionFechaCorte(idSimulacion, idUbicacion, fechaCorte);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
