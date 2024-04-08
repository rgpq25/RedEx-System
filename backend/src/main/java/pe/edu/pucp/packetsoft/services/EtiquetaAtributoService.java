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
import pe.edu.pucp.packetsoft.models.Etiqueta;
import pe.edu.pucp.packetsoft.models.EtiquetaAtributo;
import pe.edu.pucp.packetsoft.repository.AtributoValor.AtributoRepository;
import pe.edu.pucp.packetsoft.repository.AtributoValor.EtiquetaAtributoRepository;
import pe.edu.pucp.packetsoft.repository.AtributoValor.EtiquetaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class EtiquetaAtributoService {
    private static final Logger logger = LoggerFactory.getLogger(EtiquetaAtributoService.class);
    @Autowired 
    private EtiquetaAtributoRepository etiquetaAtributoRepository;

    @Autowired
    private EtiquetaRepository etiquetaRepository;

    @Autowired
    private AtributoRepository atributoRepository;

    @PersistenceContext
    EntityManager entityManager;

    //CRUDS

    //create
    
    public EtiquetaAtributo register(EtiquetaAtributo reg){
        try {
            return etiquetaAtributoRepository.save(reg);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        // EtiquetaAtributo resultado = etiquetaAtributoRepository.save(reg);
        // if(resultado == null){
        //     return null;
        // }
        // return resultado;
    }

    //read
    
    public List<EtiquetaAtributo> getAll(){
        try {
            return etiquetaAtributoRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        // return etiquetaAtributoRepository.findAll();
    }

    
    public EtiquetaAtributo get(int id){
        try {
            return etiquetaAtributoRepository.findRegisterById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        // return etiquetaAtributoRepository.findRegisterById(id);
    }

    //update
    
    public EtiquetaAtributo update(EtiquetaAtributo reg){
        try {
            return etiquetaAtributoRepository.save(reg);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        // return etiquetaAtributoRepository.save(reg);
    }

    //delete
    
    public void delete(int id){
        try {
            etiquetaAtributoRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //etiquetaAtributoRepository.deleteById(id);
    }

    public List<EtiquetaAtributo> listarEtiquetasAtributosPorEtiqueta(int idEtiqueta){
        try {
            Etiqueta etiqueta = new Etiqueta();
            etiqueta = etiquetaRepository.findTagById(idEtiqueta);
            return etiquetaAtributoRepository.findEtiquetaAtributoByEtiqueta(etiqueta);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<EtiquetaAtributo> listarEtiquetasAtributosPorAtributo(int idAtributo){
        try {
            Atributo atributo = new Atributo();
            atributo = atributoRepository.findAtributoById(idAtributo);
            return etiquetaAtributoRepository.findEtiquetaAtributoByAtributo(atributo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
