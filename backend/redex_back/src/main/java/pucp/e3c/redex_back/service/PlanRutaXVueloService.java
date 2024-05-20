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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PlanRutaXVueloService {
    @Autowired
    private PlanRutaXVueloRepository planRutaXVueloRepository;

    @Autowired
    VueloRepository vueloRepository;

    @Autowired
    PlanRutaRepository planRutaRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PlanRutaXVueloService.class);

    public PlanRutaXVuelo register(PlanRutaXVuelo planRutaXVuelo) {
        //return planRutaXVueloRepository.save(planRutaXVuelo);
        try{
            return planRutaXVueloRepository.save(planRutaXVuelo);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public PlanRutaXVuelo update(PlanRutaXVuelo planRutaXVuelo) {
        //return planRutaXVueloRepository.save(planRutaXVuelo);
        try{
            return planRutaXVueloRepository.save(planRutaXVuelo);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int id) {
        //planRutaXVueloRepository.deleteById(id);
        try{
            planRutaXVueloRepository.deleteById(id);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    public List<PlanRutaXVuelo> getAll() {
        //return planRutaXVueloRepository.findAll();
        try{
            return planRutaXVueloRepository.findAll();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public PlanRutaXVuelo get(int id) {
        //return planRutaXVueloRepository.findById(id).orElse(null);
        try{
            return planRutaXVueloRepository.findById(id).orElse(null);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Vuelo> findVuelosByPlanRuta(int planRutaId) {
        /* 
        List<PlanRutaXVuelo> planRutaXVuelos = planRutaXVueloRepository.findByPlanRutaId(planRutaId);
        List<Vuelo> vuelos = new ArrayList<Vuelo>();
        for (PlanRutaXVuelo planRutaXVuelo : planRutaXVuelos) {
            vueloRepository.findById(planRutaXVuelo.getVuelo().getId()).ifPresent(vuelos::add);
        }
        Collections.sort(vuelos, (vuelo1, vuelo2) -> vuelo1.getFechaSalida().compareTo(vuelo2.getFechaSalida()));
        return vuelos;*/
        try{
            List<PlanRutaXVuelo> planRutaXVuelos = planRutaXVueloRepository.findByPlanRutaId(planRutaId);
            if(planRutaXVuelos == null || planRutaXVuelos.isEmpty()){
                return null;
            }
            List<Vuelo> vuelos = new ArrayList<Vuelo>();
            for (PlanRutaXVuelo planRutaXVuelo : planRutaXVuelos) {
                vueloRepository.findById(planRutaXVuelo.getVuelo().getId()).ifPresent(vuelos::add);
            }
            Collections.sort(vuelos, (vuelo1, vuelo2) -> vuelo1.getFechaSalida().compareTo(vuelo2.getFechaSalida()));
            return vuelos;
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Vuelo> findVuelosByPlanRutaOrdenadosIndice(int planRutaId) {
        /*List<PlanRutaXVuelo> planRutaXVuelos = planRutaXVueloRepository.findByPlanRutaId(planRutaId);
        if(planRutaXVuelos == null || planRutaXVuelos.isEmpty()){
            return null;
        }
        //order by indiceDeOrden asc
        planRutaXVuelos.sort((pr1, pr2) -> pr1.getIndiceDeOrden() - pr2.getIndiceDeOrden());
        List<Vuelo> vuelos = new ArrayList<Vuelo>();
        for (PlanRutaXVuelo planRutaXVuelo : planRutaXVuelos) {
            vueloRepository.findById(planRutaXVuelo.getVuelo().getId()).ifPresent(vuelos::add);
        }
        //Collections.sort(vuelos, (vuelo1, vuelo2) -> vuelo1.getFechaSalida().compareTo(vuelo2.getFechaSalida()));       
        return vuelos;*/
        try{
            List<PlanRutaXVuelo> planRutaXVuelos = planRutaXVueloRepository.findByPlanRutaId(planRutaId);
            if(planRutaXVuelos == null || planRutaXVuelos.isEmpty()){
                return null;
            }
            //order by indiceDeOrden asc
            planRutaXVuelos.sort((pr1, pr2) -> pr1.getIndiceDeOrden() - pr2.getIndiceDeOrden());
            List<Vuelo> vuelos = new ArrayList<Vuelo>();
            for (PlanRutaXVuelo planRutaXVuelo : planRutaXVuelos) {
                vueloRepository.findById(planRutaXVuelo.getVuelo().getId()).ifPresent(vuelos::add);
            }
            //Collections.sort(vuelos, (vuelo1, vuelo2) -> vuelo1.getFechaSalida().compareTo(vuelo2.getFechaSalida()));       
            return vuelos;
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<PlanRutaXVuelo> findByPlanRutaId(int idPlan) {
        //return planRutaXVueloRepository.findByPlanRutaId(idPlan);
        try{
            return planRutaXVueloRepository.findByPlanRutaId(idPlan);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<PlanRuta> findPlanesRutaByVuelo(Integer idVuelo){
        /*List<PlanRutaXVuelo> planRutaXVuelos = planRutaXVueloRepository.findByVueloId(idVuelo);
        List<PlanRuta> planesRuta = new ArrayList<PlanRuta>();
        for (PlanRutaXVuelo planRutaXVuelo : planRutaXVuelos) {
            planRutaRepository.findById(planRutaXVuelo.getPlanRuta().getId()).ifPresent(planesRuta::add);
        }
        return planesRuta;*/
        try{
            List<PlanRutaXVuelo> planRutaXVuelos = planRutaXVueloRepository.findByVueloId(idVuelo);
            if(planRutaXVuelos == null || planRutaXVuelos.isEmpty()){
                return null;
            }
            List<PlanRuta> planesRuta = new ArrayList<PlanRuta>();
            for (PlanRutaXVuelo planRutaXVuelo : planRutaXVuelos) {
                planRutaRepository.findById(planRutaXVuelo.getPlanRuta().getId()).ifPresent(planesRuta::add);
            }
            return planesRuta;
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        
        }
    }

    public List<PlanRutaXVuelo> findByVueloId(Integer id) {
        //return planRutaXVueloRepository.findByVueloId(id);
        try{
            return planRutaXVueloRepository.findByVueloId(id);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
