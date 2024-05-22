package pucp.e3c.redex_back.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pucp.e3c.redex_back.model.EstadoPaquete;
import pucp.e3c.redex_back.repository.EstadoPaqueteRepository;

@Service
public class EstadoPaqueteService {
    @Autowired
    private EstadoPaqueteRepository estadoPaqueteRepository; //Inyecta la dependencia

    private static final Logger LOGGER = LoggerFactory.getLogger(EstadoPaqueteService.class);

    public EstadoPaquete register(EstadoPaquete estadoPaquete) {
        //return estadoPaqueteRepository.save(estadoPaquete);
        try{
            return estadoPaqueteRepository.save(estadoPaquete);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public EstadoPaquete get(Integer id) {
        //Optional<EstadoPaquete> optional_estado_paquete = estadoPaqueteRepository.findById(id);
        //return optional_estado_paquete.get();
        try{
            Optional<EstadoPaquete> optional_estado_paquete = estadoPaqueteRepository.findById(id);
            return optional_estado_paquete.get();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<EstadoPaquete> getAll() {
        //return estadoPaqueteRepository.findAll();
        try{
            return estadoPaqueteRepository.findAll();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(Integer id) {
        //estadoPaqueteRepository.deleteById(id);
        try{
            estadoPaqueteRepository.deleteById(id);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
        
        }
    }

    public EstadoPaquete update(EstadoPaquete estadoPaquete) {
        //return estadoPaqueteRepository.save(estadoPaquete);
        try{
            return estadoPaqueteRepository.save(estadoPaquete);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
