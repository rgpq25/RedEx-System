package pucp.e3c.redex_back.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.PaqueteXEstadoPaquete;
import pucp.e3c.redex_back.repository.PaqueteXEstadoPaqueteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaqueteXEstadoPaqueteService {
    @Autowired
    private PaqueteXEstadoPaqueteRepository paqueteXEstadoPaqueteRepository; //Inyecta la dependencia
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PaqueteXEstadoPaqueteService.class);

    public List<PaqueteXEstadoPaquete> findByPaqueteId(Integer id) {
        //return paqueteXEstadoPaqueteRepository.findByPaqueteId(id);
        try{
            return paqueteXEstadoPaqueteRepository.findByPaqueteId(id);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<PaqueteXEstadoPaquete> findByEstadoPaqueteId(Integer id) {
        //return paqueteXEstadoPaqueteRepository.findByEstadoPaqueteId(id);
        try{
            return paqueteXEstadoPaqueteRepository.findByEstadoPaqueteId(id);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public PaqueteXEstadoPaquete register(PaqueteXEstadoPaquete paqueteXEstadoPaquete) {
        //return paqueteXEstadoPaqueteRepository.save(paqueteXEstadoPaquete);
        try{
            return paqueteXEstadoPaqueteRepository.save(paqueteXEstadoPaquete);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public PaqueteXEstadoPaquete get(Integer id) {
        //Optional<PaqueteXEstadoPaquete> optional_paqueteXEstadoPaquete = paqueteXEstadoPaqueteRepository.findById(id);
        //return optional_paqueteXEstadoPaquete.get();
        try{
            Optional<PaqueteXEstadoPaquete> optional_paqueteXEstadoPaquete = paqueteXEstadoPaqueteRepository.findById(id);
            return optional_paqueteXEstadoPaquete.get();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<PaqueteXEstadoPaquete> getAll() {
        //return paqueteXEstadoPaqueteRepository.findAll();
        try{
            return paqueteXEstadoPaqueteRepository.findAll();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(Integer id) {
        //paqueteXEstadoPaqueteRepository.deleteById(id);
        try{
            paqueteXEstadoPaqueteRepository.deleteById(id);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
        
        }
    }

    public PaqueteXEstadoPaquete update(PaqueteXEstadoPaquete paqueteXEstadoPaquete) {
        //return paqueteXEstadoPaqueteRepository.save(paqueteXEstadoPaquete);
        try{
            return paqueteXEstadoPaqueteRepository.save(paqueteXEstadoPaquete);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
