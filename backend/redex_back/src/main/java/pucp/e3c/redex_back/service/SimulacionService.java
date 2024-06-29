package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.Envio;
import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.RespuestaReporte;
import pucp.e3c.redex_back.model.RespuestaReportePaquete;
import pucp.e3c.redex_back.model.Simulacion;
import pucp.e3c.redex_back.repository.EnvioRepository;
import pucp.e3c.redex_back.repository.PaqueteRepository;
import pucp.e3c.redex_back.repository.SimulacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SimulacionService {
    @Autowired
    private SimulacionRepository simulacionRepository;


    private static final Logger LOGGER = LoggerFactory.getLogger(SimulacionService.class);

    public Simulacion register(Simulacion simulacion) {        
        //return simulacionRepository.save(simulacion);
        try{
            return simulacionRepository.save(simulacion);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Simulacion get(int id) {
        //return simulacionRepository.findById(id).orElse(null);
        try{
            return simulacionRepository.findById(id).orElse(null);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Simulacion update(Simulacion simulacion) {
        //return simulacionRepository.save(simulacion);
        try{
            return simulacionRepository.save(simulacion);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        
        }
    }

    public void delete(int id) {
        //simulacionRepository.deleteById(id);
        try{
            simulacionRepository.deleteById(id);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    public List<Simulacion> getAll() {
        //return simulacionRepository.findAll();
        try{
            return simulacionRepository.findAll();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void deleteAll()
    {
        try{
            simulacionRepository.deleteAll();
            simulacionRepository.flush();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    

}
