package pe.edu.pucp.packetsoft.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.PathVariable;

import pe.edu.pucp.packetsoft.controllers.HerramientaController;
import pe.edu.pucp.packetsoft.models.Atributo;
import pe.edu.pucp.packetsoft.models.Cronograma;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.Proyecto;
import pe.edu.pucp.packetsoft.models.Valor;
import pe.edu.pucp.packetsoft.models.Tabla;

import pe.edu.pucp.packetsoft.repository.AtributoValor.AtributoRepository;
import pe.edu.pucp.packetsoft.repository.AtributoValor.TablaRepository;
import pe.edu.pucp.packetsoft.repository.AtributoValor.ValorRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CronogramaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.ProyectoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ValorService {

    private static final Logger logger = LoggerFactory.getLogger(ValorService.class);

    @Autowired 
    private ValorRepository valorRepository;

    @Autowired
    private AtributoRepository atributoRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private TablaRepository tablaRepository;

    @Autowired
    private CronogramaRepository cronogramaRepository;

    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private ProyectoService proyectoService;

    //CRUDS

    //create

    public Valor register(Valor valor){
        try {
            //verificaFechaFinProyecto(valor);
            return valorRepository.save(valor);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
/* 
    public void verificaFechaFinProyecto(Valor valor){
        try{
            Atributo atributo = atributoRepository.getById(valor.getAtributo().getId());
            Tabla tabla = tablaRepository.getById(atributo.getTabla().getId());
            //Se verifica que la Tabla pertenezca a un Cronograma
            if(tabla.getCronograma()!= null){
                double diferenciaInicio = Math.abs( atributo.getFactorDeOrden() - 1.5);
                double diferenciaFin = Math.abs( atributo.getFactorDeOrden() - 1.6);

                //Se verifica si el atributo es de Fecha de Inicio o Fecha de Fin
                if(!((diferenciaFin < 0.0001) || (diferenciaInicio < 0.0001))) return;

                Cronograma cronograma = cronogramaRepository.getById(tabla.getCronograma().getId());
                HerramientaXProyecto herramientaXProyecto = herramientaXProyectoRepository.getById(cronograma.getHerramientaXProyecto().getId());
                Proyecto proyecto = proyectoRepository.getById(herramientaXProyecto.getProyecto().getId());

                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fecha = LocalDate.parse(valor.getContenido(), formato);
                ZoneId zonaHoraria = ZoneId.systemDefault();
                Date fechaCambio = Date.from(fecha.atStartOfDay(zonaHoraria).toInstant());
                //Si el atributo es de Fecha de Inicio
                if(diferenciaInicio < 0.0001){
                    if(proyecto.getFechaInicio()==null){
                        proyecto.setFechaInicio(fechaCambio);
                        //Proyecto proyectoGuardado = proyectoRepository.save(proyecto);
                        proyectoService.register(proyecto);
                        logger.error("se guardo fecha inicio");
                        return;
                    }

                    Instant instantInicio = proyecto.getFechaInicio().toInstant();
                    LocalDate fechaInicio = instantInicio.atZone(zonaHoraria).toLocalDate();
                    if(fecha.isBefore(fechaInicio)){
                        proyecto.setFechaInicio(fechaCambio);
                        //Proyecto proyectoGuardado = proyectoRepository.save(proyecto);
                        logger.error("se guardo fecha inicio");
                    }
                }

                //Si el atributo es de Fecha de Fin
                if(diferenciaFin < 0.0001){
                    if(proyecto.getFechaFin()==null){
                        proyecto.setFechaFin(fechaCambio);
                        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);
                        logger.error("se guardo fecha fin");
                        return;
                    }
                    Instant instant = proyecto.getFechaFin().toInstant();
                    LocalDate fechaFin = instant.atZone(zonaHoraria).toLocalDate();

                    if(fecha.isAfter(fechaFin)){
                        proyecto.setFechaFin(fechaCambio);
                        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);
                        logger.error("se guardo fecha fin");
                    }
                }               
            }
        }
        catch(Exception e){
            logger.error(e.getMessage());
        }
    }

   */

    //read
    public List<Valor> getAll(){
        try {
            return valorRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return valorRepository.findAll();
    }


    public Valor get(int id){
        try {
            return valorRepository.findValorById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return valorRepository.findValorById(id);
    }

    //update

    public Valor update(Valor valor){
        try {
            //verificaFechaFinProyecto(valor);
            
            return valorRepository.save(valor);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    //delete
    
    public void delete(int id){
        try {
            valorRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //valorRepository.deleteById(id);
    }

    public List<Valor> findValorByAtributo(int idAtributo){
        Atributo atributo = new Atributo();
        try {
            atributo = atributoRepository.findAtributoById(idAtributo);
            return valorRepository.findValorByAtributo(atributo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        // atributo = atributoRepository.findAtributoById(idAtributo);
        // return valorRepository.findValorByAtributo(atributo);
    }

     public List<Valor> listarValorPorAtributo(int idAtributo){
        try {
            Atributo atributo = new Atributo();
            atributo = atributoRepository.findAtributoById(idAtributo);
            return valorRepository.findValorByAtributo(atributo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
