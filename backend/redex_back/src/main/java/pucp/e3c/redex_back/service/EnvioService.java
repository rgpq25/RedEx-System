package pucp.e3c.redex_back.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pucp.e3c.redex_back.model.Envio;
import pucp.e3c.redex_back.repository.EnvioRepository;


@Service
public class EnvioService {
    @Autowired
    private EnvioRepository envioRepository; //Inyecta la dependencia

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvioService.class);

    public Envio register(Envio envio) {
        //return envioRepository.save(envio);
        try{
            return envioRepository.save(envio);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Envio get(Integer id) {
        //Optional<Envio> optional_envio = envioRepository.findById(id);
        //return optional_envio.get();
        try{
            Optional<Envio> optional_envio = envioRepository.findById(id);
            return optional_envio.get();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Envio> getAll() {
        //return envioRepository.findAll();
        try{
            return envioRepository.findAll();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(Integer id) {
        //envioRepository.deleteById(id);
        try{
            envioRepository.deleteById(id);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    public Envio update(Envio envio) {
        //return envioRepository.save(envio);
        try{
            return envioRepository.save(envio);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        
        }
    }

    public Envio findByCodigo_seguridad(String codigoSeguridad) {
        //return envioRepository.findByCodigoSeguridad(codigoSeguridad);
        try{
            return envioRepository.findByCodigoSeguridad(codigoSeguridad);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
    }    
    
}
