package pe.edu.pucp.packetsoft.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.PathVariable;

import pe.edu.pucp.packetsoft.models.Atributo;
import pe.edu.pucp.packetsoft.models.Tabla;
import pe.edu.pucp.packetsoft.repository.AtributoValor.AtributoRepository;
import pe.edu.pucp.packetsoft.repository.AtributoValor.TablaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class AtributoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AtributoService.class);
    @Autowired
    private AtributoRepository atributoRepository;

    @Autowired
    private TablaRepository tablaRepository;
    
    @PersistenceContext
    EntityManager entityManager;

    //CRUDS

    //create
    public Atributo register(Atributo atributo){
        try {
            return atributoRepository.save(atributo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // Atributo resultado = atributoRepository.save(atributo);
        // if(resultado == null){
        //     return null;
        // }
        // return resultado;
    }

    //read
    public List<Atributo> getAll(){
        try {
            return atributoRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return atributoRepository.findAll();
    }


    public Atributo get(int id){
        try {
            return atributoRepository.findAtributoById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return atributoRepository.findAtributoById(id);
    }

    //update

    public Atributo update(Atributo atributo){
        try {
            return atributoRepository.save(atributo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return atributoRepository.save(atributo);
    }

    //delete

    public void delete(int id){
        try {
            atributoRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        // atributoRepository.deleteById(id);
    }    

    public List<Atributo> findAtributoByTabla(int idTabla){
        try {
            Tabla tabla = new Tabla();
            tabla = tablaRepository.findTablaById(idTabla);
            return atributoRepository.findAtributoByTabla(tabla);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
