package pe.edu.pucp.packetsoft.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import pe.edu.pucp.packetsoft.models.ActaDeConstitucion;
import pe.edu.pucp.packetsoft.models.Autoevaluacion;
import pe.edu.pucp.packetsoft.models.CatalogoDeInteresados;
import pe.edu.pucp.packetsoft.models.CatalogoDeRiesgos;
import pe.edu.pucp.packetsoft.models.Cronograma;
import pe.edu.pucp.packetsoft.models.EDT;
import pe.edu.pucp.packetsoft.models.MatrizDeComunicaciones;
import pe.edu.pucp.packetsoft.models.MatrizDeResponsabilidades;
import pe.edu.pucp.packetsoft.models.PlanDeCalidad;
import pe.edu.pucp.packetsoft.models.Plantilla;
import pe.edu.pucp.packetsoft.models.Presupuesto;
import pe.edu.pucp.packetsoft.models.ProductBacklog;
import pe.edu.pucp.packetsoft.models.Tabla;
import pe.edu.pucp.packetsoft.models.TableroKanban;
import pe.edu.pucp.packetsoft.repository.AtributoValor.TablaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.ActaDeConstitucionRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CatalogoDeInteresadosRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CatalogoDeRiesgosRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CronogramaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.EDTRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.MatrizDeResponsabilidadesRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.MatrizDeComunicacionesRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.PlanDeCalidadRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.PlantillaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.PresupuestoRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.ProductBacklogRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.TableroKanbanRepository;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.AutoevaluacionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service //Esta anotación marca la clase UsuarioService como un componente de servicio de Spring.
//Los componentes de servicio en Spring se utilizan para encapsular la lógica de negocio y
//proporcionar métodos para interactuar con los datos o realizar operaciones específicas.

public class TablaService {
    private static final Logger logger = LoggerFactory.getLogger(TablaService.class);
    //Esta anotación se utiliza para inyectar una instancia de TablaRepository en el servicio.
    // El TablaRepository generalmente es un componente de acceso a datos que proporciona métodos
    //para interactuar con la base de datos y obtener información sobre los registros de la tabla "tabla".
    @Autowired 
    private TablaRepository tablaRepository;

    @Autowired
    private PlantillaRepository plantillaRepository;

    @Autowired
    private PlanDeCalidadRepository planDeCalidadRepository;

    @Autowired
    private ActaDeConstitucionRepository actaDeConstitucionRepository;

    @Autowired
    private CronogramaRepository cronogramaRepository;

    @Autowired
    private EDTRepository edtRepository;

    @Autowired
    private ProductBacklogRepository productBacklogRepository;

    @Autowired
    private PresupuestoRepository presupuestoRepository;    

    @Autowired
    private CatalogoDeRiesgosRepository catalogoDeRiesgosRepository;

    @Autowired
    private MatrizDeResponsabilidadesRepository matrizDeResponsabilidadesRepository;

    @Autowired
    private MatrizDeComunicacionesRepository matrizDeComunicacionesRepository;

    @Autowired
    private TableroKanbanRepository tableroKanbanRepository;  
    
    @Autowired
    private CatalogoDeInteresadosRepository catalogoDeInteresadosRepository;

    @Autowired
    private AutoevaluacionRepository autoevaluacionRepository;

    @PersistenceContext
    EntityManager entityManager;

    //CRUDS

    //create

    public Tabla register(Tabla tabla){
        try {
            return tablaRepository.save(tabla);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        // Tabla resultado = tablaRepository.save(tabla);        
        // if(resultado == null){
        //     return null;
        // }
        // return resultado;
    }

    //read

    public List<Tabla> getAll(){
        try {
            return tablaRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return tablaRepository.findAll();
    }

    public Tabla get(int id){
        try {
            return tablaRepository.findTablaById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return tablaRepository.findTablaById(id);
    }

    //update

    public Tabla update(Tabla tabla){
        try {
            return tablaRepository.save(tabla);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        //return tablaRepository.save(tabla);
    }

    //delete

    public void delete(int id){
        try {
            tablaRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        //tablaRepository.deleteById(id);
    }
   
    public Tabla duplicadoPropio(int id){
        Tabla tabla = null;
        try {
            return tablaRepository.duplicadoPropio(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    public List<Tabla> listarTablasPorPlantilla(int idPlantilla){
        try {
            Plantilla plantilla = new Plantilla();
            plantilla = plantillaRepository.findPlantillaById(idPlantilla);
            return tablaRepository.findTablaByPlantilla(plantilla);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<Tabla> listarTablasPorPlanDeCalidad(int idPlanDeCalidad){
        try {
            PlanDeCalidad planDeCalidad = new PlanDeCalidad();
            planDeCalidad = planDeCalidadRepository.findPlanDeCalidadById(idPlanDeCalidad);
            return tablaRepository.findTablaByPlanDeCalidad(planDeCalidad);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<Tabla> listarTablasPorActaDeConstitucion(int idActaDeConstitucion){
        try {
            ActaDeConstitucion actaDeConstitucion = new ActaDeConstitucion();
            actaDeConstitucion = actaDeConstitucionRepository.findActaDeConstitucionById(idActaDeConstitucion);
            return tablaRepository.findTablaByActaDeConstitucion(actaDeConstitucion);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Tabla listarTablasPorCronograma(int idCronograma){
        try {
            Cronograma cronograma = new Cronograma();
            cronograma = cronogramaRepository.findCronogramaById(idCronograma);
            return tablaRepository.findTablaByCronograma(cronograma);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
    
    public Tabla listarTablasPorEDT(int idEDT){
        try {
            EDT edt = new EDT();
            edt = edtRepository.findEDTById(idEDT);
            return tablaRepository.findTablaByEdt(edt);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Tabla listarTablasPorProductBacklog(int idProductBacklog){
        try {
            ProductBacklog productBacklog = new ProductBacklog();
            productBacklog = productBacklogRepository.findProductBacklogById(idProductBacklog);
            return tablaRepository.findTablaByProductBacklog(productBacklog);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Tabla listarTablasPorPresupuesto(int idPresupuesto){
        try {
            Presupuesto presupuesto = new Presupuesto();
            presupuesto = presupuestoRepository.findPresupuestoById(idPresupuesto);
            return tablaRepository.findTablaByPresupuesto(presupuesto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }   
    
    public Tabla listarTablasPorCatalogoDeRiesgo(int idCatalogoDeRiesgos){
        try {
            CatalogoDeRiesgos catalogoDeRiesgos = new CatalogoDeRiesgos();
            catalogoDeRiesgos = catalogoDeRiesgosRepository.findCatalogoDeRiesgosById(idCatalogoDeRiesgos);
            return tablaRepository.findTablaByCatalogoDeRiesgos(catalogoDeRiesgos);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Tabla listarTablasPorMatrizDeResponsabilidades(int idMatrizDeResponsabilidades){
        try {
            MatrizDeResponsabilidades matrizDeResponsabilidades = new MatrizDeResponsabilidades();
            matrizDeResponsabilidades = matrizDeResponsabilidadesRepository.findMatrizDeResponsabilidadesById(idMatrizDeResponsabilidades);
            return tablaRepository.findTablaByMatrizDeResponsabilidades(matrizDeResponsabilidades);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }    

    public Tabla listarTablasPorMatrizDeComunicaciones(int idMatrizDeComunicaciones){
        try {
            MatrizDeComunicaciones matrizDeComunicaciones = new MatrizDeComunicaciones();
            matrizDeComunicaciones = matrizDeComunicacionesRepository.findMatrizDeComunicacionesById(idMatrizDeComunicaciones);
            return tablaRepository.findTablaByMatrizDeComunicaciones(matrizDeComunicaciones);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<Tabla> listarTablasPorTableroKanban(int idTableroKanban){
        try {
            TableroKanban tableroKanban = new TableroKanban();
            tableroKanban = tableroKanbanRepository.findTableroKanbanById(idTableroKanban);
            return tablaRepository.findTablaByTableroKanban(tableroKanban);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }    

    public Tabla listarTablasPorCatalogoDeInteresados(int idCatalogoDeInteresados){
        try {
            CatalogoDeInteresados catalogoDeInteresados = new CatalogoDeInteresados();
            catalogoDeInteresados = catalogoDeInteresadosRepository.findCatalogoDeInteresadosById(idCatalogoDeInteresados);
            return tablaRepository.findTablaByCatalogoDeInteresados(catalogoDeInteresados);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Tabla listarTablasPorAutoevaluacion(int idAutoevaluacion){
        try {
            Autoevaluacion autoevaluacion = new Autoevaluacion();
            autoevaluacion = autoevaluacionRepository.findAutoevaluacionById(idAutoevaluacion);
            return tablaRepository.findTablaByAutoevaluacion(autoevaluacion);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

}
