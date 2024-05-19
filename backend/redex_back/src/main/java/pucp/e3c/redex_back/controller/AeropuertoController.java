package pucp.e3c.redex_back.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.PlanRuta;
import pucp.e3c.redex_back.model.Simulacion;
import pucp.e3c.redex_back.model.Vuelo;
import pucp.e3c.redex_back.service.AeropuertoService;
import pucp.e3c.redex_back.service.PaqueteService;
import pucp.e3c.redex_back.service.PlanRutaXVueloService;
import pucp.e3c.redex_back.service.SimulacionService;
import pucp.e3c.redex_back.service.VueloService;

@RestController
@RequestMapping("back/aeropuerto")
public class AeropuertoController {
    
    @Autowired
    private AeropuertoService aeropuertoService;

    @Autowired
    private SimulacionService simulacionService;

    @Autowired
    private VueloService vueloService;

    @Autowired
    private PlanRutaXVueloService planRutaXVueloService;

    @Autowired
    private PaqueteService paqueteService;
    
    @PostMapping(value = "/")
    public Aeropuerto  register(@RequestBody Aeropuerto aeropuerto) {
        return aeropuertoService.register(aeropuerto);
    }

    @PutMapping(value = "/")
    public Aeropuerto  update(@RequestBody Aeropuerto aeropuerto) {
        return aeropuertoService.update(aeropuerto);
    }

    @GetMapping(value = "/")
    public List<Aeropuerto> getAll() {
        return aeropuertoService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Aeropuerto get(@PathVariable("id") Integer id) {
        return aeropuertoService.get(id);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Integer id) {
        aeropuertoService.delete(id);
    }

    @GetMapping(value = "/ubicacion/{idUbicacion}")
    public Aeropuerto findByUbicacion(@PathVariable("idUbicacion") String idUbicacion) {
        return aeropuertoService.findByUbicacion(idUbicacion);
    }

    private Date getFechaCorte(Simulacion simulacion){
        //get the date of today
        Date today = new Date();
        long todayTime = today.getTime();
        long inicioSis = simulacion.getFechaInicioSistema().getTime();
        long diferencia = todayTime - inicioSis;
        long diferenciaSim = (long)(diferencia * simulacion.getMultiplicadorTiempo());
        long inicioSim = simulacion.getFechaInicioSim().getTime();
        Date fechaSimulacion = new Date(inicioSim + diferenciaSim);
        return fechaSimulacion;
    }

    private boolean isAfterByMoreThanFiveMinutes(Date date1, Date date2) {
        long differenceInMillis = date1.getTime() - date2.getTime();
        long fiveMinutesInMillis = 5 * 60 * 1000; // 5 minutes in milliseconds

        return differenceInMillis > fiveMinutesInMillis;
    }
    
    @GetMapping(value = "/{idAeropuerto}/paquetesfromsimulation/{idSimulacion}")
    public ArrayList<Paquete> getPaquetesFromSimulation(@PathVariable("idAeropuerto") Integer idAeropuerto, @PathVariable("idSimulacion") Integer idSimulacion){
        Aeropuerto aeropuerto = aeropuertoService.get(idAeropuerto);
        //ArrayList<Vuelo> vuelos = vueloService.findVuelosValidosAeropuertoSimulacion(idSimulacion, aeropuerto.getUbicacion().getId());
        Simulacion simulacion = simulacionService.get(idSimulacion);

        Date fechaSimulacion = getFechaCorte(simulacion);
        
        ArrayList<Vuelo> vuelosDestino = vueloService.findVuelosDestinoAeropuertoSimulacionFecha(idSimulacion, aeropuerto.getUbicacion().getId(), fechaSimulacion);
        ArrayList<Vuelo> vuelosOrigen = vueloService.findVuelosOrigenAeropuertoSimulacionFecha(idSimulacion, aeropuerto.getUbicacion().getId(), fechaSimulacion);

        HashMap<Integer,PlanRuta> planRutasMap = new HashMap<>();

        if(vuelosDestino != null){
            for(Vuelo vuelo : vuelosDestino){
                List<PlanRuta> planRutas = planRutaXVueloService.findPlanesRutaByVuelo(vuelo.getId());
                if(planRutas == null) continue;
                for(PlanRuta planRuta : planRutas){
                    //check if plan ruta is already in the map
                    if(planRutasMap.containsKey(planRuta.getId())){
                        continue;
                    }
                    planRutasMap.put(planRuta.getId(), planRuta);
                }
            }
        }
        if(vuelosOrigen != null){
            for(Vuelo vuelo : vuelosOrigen){
                List<PlanRuta> planRutas = planRutaXVueloService.findPlanesRutaByVuelo(vuelo.getId());
                if(planRutas == null) continue;
                for(PlanRuta planRuta : planRutas){
                    //check if plan ruta is already in the map
                    if(planRutasMap.containsKey(planRuta.getId())){
                        continue;
                    }
                    planRutasMap.put(planRuta.getId(), planRuta);
                }
            }
        }
        
        //iterate hashmap planRutasMap
        HashMap<Integer,PlanRuta> planRutasValidasMap = new HashMap<>();
        if(planRutasMap.isEmpty()){
            return null;
        }
        for(Integer idPlanRuta : planRutasMap.keySet()){
            //itero sobre los posibles planes ruta
            List<Vuelo> vuelosPlanRuta = planRutaXVueloService.findVuelosByPlanRutaOrdenadosIndice(idPlanRuta);            
            if(vuelosPlanRuta == null) continue;
            int lastIndex = vuelosPlanRuta.size() - 1;
            for (int i = 0; i < vuelosPlanRuta.size(); i++) {
                //itero sobre los vuelos de un plan ruta
                Vuelo vuelo = vuelosPlanRuta.get(i);
                //check if vuelo is in vuelosDestino
                if(vuelosDestino.contains(vuelo)){
                    //check if vuelo is the last one
                    if(i == lastIndex){
                        //check if fechaSimulacion is greater than fechaLlegada by more than 5 minutes
                        if(isAfterByMoreThanFiveMinutes(fechaSimulacion, vuelo.getFechaLlegada())){
                            planRutasValidasMap.put(idPlanRuta, planRutasMap.get(idPlanRuta));
                            break;
                        }
                        else{
                            break;
                        }
                        
                    }
                    else{
                        //check if next vuelo is in vuelosOrigen
                        if(vuelosOrigen.contains(vuelosPlanRuta.get(i+1))){
                            planRutasValidasMap.put(idPlanRuta, planRutasMap.get(idPlanRuta));
                            break;
                        }
                        else{
                            break;
                        }
                    }
                }
                else if(vuelosOrigen.contains(vuelo)){
                    //check if vuelo is the first one
                    if(i == 0){
                        planRutasValidasMap.put(idPlanRuta, planRutasMap.get(idPlanRuta));
                        break;
                    }
                    else{
                        //check if previous vuelo is in vuelosDestino
                        if(vuelosDestino.contains(vuelosPlanRuta.get(i-1))){
                            planRutasValidasMap.put(idPlanRuta, planRutasMap.get(idPlanRuta));
                            break;
                        }
                        else{
                            break;
                        }
                    }
                }                
            }
        }

        ArrayList<Paquete> paquetes = new ArrayList<>();
        for(Integer idPlanRuta : planRutasValidasMap.keySet()){
            //PlanRuta planRuta = planRutasValidasMap.get(idPlanRuta);
            Paquete paquetePlanRuta = paqueteService.findByPlanRutaId(idPlanRuta);
            if(paquetePlanRuta == null) continue;
            paquetes.add(paquetePlanRuta);
        }

        ArrayList<Paquete> paquetesEnAeropuerto = paqueteService.findPaquetesWithoutPlanRutaSimulacion(aeropuerto.getUbicacion().getId(), idSimulacion, fechaSimulacion);
        if(paquetesEnAeropuerto != null){
            paquetes.addAll(paquetesEnAeropuerto);
        }
        return paquetes;
    }
    
}
