package pucp.e3c.redex_back.controller;

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
    
    @GetMapping(value = "/{idAeropuerto}/simulation/{idSimulacion}")
    public List<Paquete> getPaquetesFromSimulation(@PathVariable("idAeropuerto") Integer idAeropuerto, @PathVariable("idSimulacion") Integer idSimulacion){
        Aeropuerto aeropuerto = aeropuertoService.get(idAeropuerto);
        ArrayList<Vuelo> vuelos = vueloService.findVuelosValidosAeropuertoSimulacion(idSimulacion, aeropuerto.getUbicacion().getId());
        Simulacion simulacion = simulacionService.get(idSimulacion);
        //get the date of today
        Date today = new Date();
        long todayTime = today.getTime();
        long inicioSis = simulacion.getFechaInicioSistema().getTime();
        long diferencia = todayTime - inicioSis;
        long diferenciaSim = (long)(diferencia * simulacion.getMultiplicadorTiempo());
        long inicioSim = simulacion.getFechaInicioSim().getTime();
        Date fechaSimulacion = new Date(inicioSim + diferenciaSim);
        //turn ArrayList<Vuelo> vuelos into hashmap

        //ArrayList<Vuelo> vuelosValidos = new ArrayList<>();
        HashMap<Integer, Vuelo> vuelosMap = new HashMap<>();
        for(Vuelo vuelo : vuelos){
            if(vuelo.getPlanVuelo().getCiudadDestino().getId()==aeropuerto.getUbicacion().getId()){
                //La ciudad destino del vuelo es el aeropuerto
                if(vuelo.getFechaLlegada().before(fechaSimulacion)){
                    //vuelosValidos.add(vuelo);
                    vuelosMap.put(vuelo.getId(), vuelo);
                }
            }
            else{
                //La ciudad origen del vuelo es el aeropuerto
                if(vuelo.getFechaSalida().after(fechaSimulacion)){
                    //vuelosValidos.add(vuelo);
                    vuelosMap.put(vuelo.getId(), vuelo);
                }
            }
        }

        HashMap<Integer,PlanRuta> planRutasMap = new HashMap<>();
        for(Integer idVuelo : vuelosMap.keySet()){
            List<PlanRuta> planRutas = planRutaXVueloService.findPlanesRutaByVuelo(idVuelo);
            for(PlanRuta planRuta : planRutas){
                //check if plan ruta is already in the map
                if(planRutasMap.containsKey(planRuta.getId())){
                    continue;
                }
                planRutasMap.put(planRuta.getId(), planRuta);
            }
        }
        
        //iterate hashmap planRutasMap
        HashMap<Integer,PlanRuta> planRutasValidasMap = new HashMap<>();
        for(Integer idPlanRuta : planRutasMap.keySet()){
            List<Vuelo> vuelosPlanRuta = planRutaXVueloService.findVuelosByPlanRuta(idPlanRuta);
            int lastIndex = vuelosPlanRuta.size() - 1;
            for (int i = 0; i < vuelosPlanRuta.size(); i++) {
                Vuelo vuelo = vuelosPlanRuta.get(i);
                //entre ciudades
                //TO DO: que pasa si solo hay 1 vuelo
                if((vuelo.getPlanVuelo().getCiudadDestino().getId()==aeropuerto.getUbicacion().getId()) || (vuelo.getPlanVuelo().getCiudadOrigen().getId()==aeropuerto.getUbicacion().getId()) && (i!=lastIndex)){
                    if(vuelo.getFechaSalida().before(fechaSimulacion) && vuelosPlanRuta.get(i+1).getFechaLlegada().after(fechaSimulacion)){
                        planRutasValidasMap.put(idPlanRuta, planRutasMap.get(idPlanRuta));
                    }
                    else{
                        break;
                    }
                }
                
            }
        }


        

        //1. Paquetes que se recibieron en el aeropuerto
        //2. Paquetes en espera durante ese tiempo, no es ciudad destino
        //3. Paquetes en espera durante ese tiempo, es ciudad destino
        return null;
    }
    
}
