package pucp.e3c.redex_back.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.repository.AeropuertoRepository;


@Service
public class AeropuertoService {
    @Autowired
    private AeropuertoRepository aeropuertoRepository; //Inyecta la dependencia

    private static final Logger LOGGER = LoggerFactory.getLogger(AeropuertoService.class);
    /*@Autowired
    private UbicacionRepository ubicacionRepository;*/

    public Aeropuerto register(Aeropuerto aeropuerto) {
        try{
            return aeropuertoRepository.save(aeropuerto);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
        //return aeropuertoRepository.save(aeropuerto);
    }

    public Aeropuerto get(Integer id) {
        try{
            Optional<Aeropuerto> optional_aeropuerto = aeropuertoRepository.findById(id);
            return optional_aeropuerto.get();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
        //Optional<Aeropuerto> optional_aeropuerto = aeropuertoRepository.findById(id);
        //return optional_aeropuerto.get();
    }

    public List<Aeropuerto> getAll() {
        try{
            return aeropuertoRepository.findAll();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
        //return aeropuertoRepository.findAll();
    }

    public void delete(Integer id) {
        try{
            aeropuertoRepository.deleteById(id);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
        }
        //aeropuertoRepository.deleteById(id);
    }

    public Aeropuerto update(Aeropuerto aeropuerto) {
        try{
            return aeropuertoRepository.save(aeropuerto);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
        //return aeropuertoRepository.save(aeropuerto);
    }

    public Aeropuerto findByUbicacion(String idUbicacion) {
        //Optional<Ubicacion> optional_ubicacion = ubicacionRepository.findById(idUbicacion);
        //return aeropuertoRepository.findByUbicacion(optional_ubicacion.get());
        
        try{
            return aeropuertoRepository.findByUbicacionId(idUbicacion);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
        //return aeropuertoRepository.findByUbicacionId(idUbicacion);
    }

    public void deleteAll() {
        try{
            aeropuertoRepository.deleteAll();
            aeropuertoRepository.flush();
        }
        catch(Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}
