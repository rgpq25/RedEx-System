package pe.edu.pucp.packetsoft.services;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pe.edu.pucp.packetsoft.repository.AtributoValor.AtributoRepository;
import pe.edu.pucp.packetsoft.repository.AtributoValor.TablaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.ActaDeConstitucionRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CatalogoDeInteresadosRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CatalogoDeRiesgosRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.CronogramaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.EDTRepository;
//import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.HerramientaXProyectoRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.MatrizDeComunicacionesRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.MatrizDeResponsabilidadesRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.PlanDeCalidadRepository;
import pe.edu.pucp.packetsoft.models.ActaDeConstitucion;
import pe.edu.pucp.packetsoft.models.Atributo;
import pe.edu.pucp.packetsoft.models.CatalogoDeInteresados;
import pe.edu.pucp.packetsoft.models.CatalogoDeRiesgos;
import pe.edu.pucp.packetsoft.models.Cronograma;
import pe.edu.pucp.packetsoft.models.EDT;
import pe.edu.pucp.packetsoft.models.Etiqueta;
import pe.edu.pucp.packetsoft.models.EtiquetaAtributo;
import pe.edu.pucp.packetsoft.models.Herramienta;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.MatrizDeComunicaciones;
import pe.edu.pucp.packetsoft.models.MatrizDeResponsabilidades;
import pe.edu.pucp.packetsoft.models.PlanDeCalidad;
import pe.edu.pucp.packetsoft.models.Plantilla;
import pe.edu.pucp.packetsoft.models.Presupuesto;
import pe.edu.pucp.packetsoft.models.ProductBacklog;
import pe.edu.pucp.packetsoft.models.Tabla;
import pe.edu.pucp.packetsoft.models.TableroKanban;
import pe.edu.pucp.packetsoft.models.Valor;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.PlantillaRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.PresupuestoRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.ProductBacklogRepository;
import pe.edu.pucp.packetsoft.repository.HerrramientasRepository.TableroKanbanRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service

public class PlantillaService {
    private static final Logger logger = LoggerFactory.getLogger(PlantillaService.class);
    @Autowired
    private HerramientaRepository herramientaRepository;
    @Autowired
    private TablaRepository tablaRepository;
    @Autowired
    private PlantillaRepository plantillaRepository;
    @Autowired
    private AtributoRepository atributoRepository;

    @Autowired
    private ActaDeConstitucionRepository actaDeConstitucionRepository;
    @Autowired
    private CronogramaRepository cronogramaRepository;
    @Autowired
    private EDTRepository edtRepository;
    @Autowired
    private PresupuestoRepository presupuestoRepository;
    @Autowired
    private CatalogoDeRiesgosRepository catalogoRiesgosRepository;
    @Autowired
    private ProductBacklogRepository productBacklogRepository;
    @Autowired
    private PlanDeCalidadRepository planDeCalidadRepository;
    @Autowired
    private MatrizDeResponsabilidadesRepository matrizDeResponsabilidadesRepository;
    @Autowired
    private MatrizDeComunicacionesRepository matrizDeComunicacionesRepository;
    @Autowired
    private TableroKanbanRepository tableroKanbanRepository;
    @Autowired
    private CatalogoDeInteresadosRepository catalogoDeInteresadosRepository;
    @Autowired
    private HerramientaXProyectoRepository herramientaXProyectoRepository;
    @Autowired
    private EtiquetaAtributoService etiquetaAtributoService;
    @Autowired
    private EtiquetaService etiquetaService;
    @Autowired
    private ValorService valorService;

    @PersistenceContext
    EntityManager entityManager;

    // CRUDS

    // create

    public Plantilla register(Plantilla plantilla) {
        try {
            return plantillaRepository.save(plantilla);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    // read

    public List<Plantilla> getAll() {
        try {
            return plantillaRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Plantilla getPlantillaById(int id) {
        try {
            return plantillaRepository.findPlantillaById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<Plantilla> findPlantillaByHerramienta(int idHerramienta) {
        try {
            Herramienta herramienta = new Herramienta();
            herramienta = herramientaRepository.findHerramientaById(idHerramienta);
            return plantillaRepository.findPlantillaByHerramienta(herramienta);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public List<Tabla> duplicarPlantilla(int idPlantilla, int idHerramientaXProyecto) {
        try {

            List<Tabla> tablas = new ArrayList<Tabla>();
            List<Tabla> nuevasTablas = new ArrayList<Tabla>();
            Plantilla plantilla = plantillaRepository.findPlantillaById(idPlantilla);
            ActaDeConstitucion acta = null;
            Cronograma cronograma = null;
            ProductBacklog productBacklog = null;
            PlanDeCalidad planDeCalidad = null;
            Presupuesto presupuesto = null;
            MatrizDeResponsabilidades matrizResp = null;
            MatrizDeComunicaciones matrizCom = null;
            TableroKanban tabKanban = null;
            CatalogoDeInteresados catalogoDeInteresados = null;
            HerramientaXProyecto herramientaXProyecto = herramientaXProyectoRepository
                    .findHerramientaXProyectoById(idHerramientaXProyecto);

            if (plantilla == null)
                return null; // Por si no encuentra la plantilla Hago return null para que no se caiga el
                             // programa, pero se deberia de hacer un throw exception

            if (plantilla.getHerramienta().getId() != herramientaXProyecto.getHerramienta().getId())
                throw new IllegalArgumentException(
                        "La herramienta de la plantilla no coincide con la de la herramientaXProyecto");
            EDT edt = null;
            CatalogoDeRiesgos catalogoDeRiesgos = null;

            Herramienta herramienta = new Herramienta();
            herramienta = herramientaRepository.EncontrarIdxHerramienta("Acta de Constitución");
            int idHerramientaActaConstitucion = herramienta.getId();
            herramienta = herramientaRepository.EncontrarIdxHerramienta("Cronograma");
            int idHerramientaCronograma = herramienta.getId();
            herramienta = herramientaRepository.EncontrarIdxHerramienta("Product Backlog");
            int idHerramientaProductBacklog = herramienta.getId();
            herramienta = herramientaRepository.EncontrarIdxHerramienta("Plan de Calidad");
            int idHerramientaPlanDeCalidad = herramienta.getId();
            herramienta = herramientaRepository.EncontrarIdxHerramienta("EDT");
            int idHerramientaEDT = herramienta.getId();
            herramienta = herramientaRepository.EncontrarIdxHerramienta("Presupuesto");
            int idHerramientaPresupuesto = herramienta.getId();
            herramienta = herramientaRepository.EncontrarIdxHerramienta("Catálogo de Riesgos");
            int idHerramientaCatalogoRiesgos = herramienta.getId();
            System.out.println(idHerramientaCatalogoRiesgos);
            herramienta = herramientaRepository.EncontrarIdxHerramienta("Matriz de responsabilidades");
            int idHerramientaMatrizResp = herramienta.getId();
            herramienta = herramientaRepository.EncontrarIdxHerramienta("Matriz de comunicaciones");
            int idHerramientaMatrizCom = herramienta.getId();
            herramienta = herramientaRepository.EncontrarIdxHerramienta("Tablero Kanban");
            int idHerramientaTabKanban = herramienta.getId();
            herramienta = herramientaRepository.EncontrarIdxHerramienta("Catálogo de interesados");
            int idHerramientaCatalogoInteresados = herramienta.getId();
            // System.out.println("Id herramienta:"+idHerramientaTabKanban);

            // System.out.println("HerramientaXProyecto id:"+herramientaXProyecto.getId());

            int idherramienta = plantilla.getHerramienta().getId();
            // System.out.println("Id herramienta 2:"+idherramienta);
            if (idherramienta == idHerramientaActaConstitucion) {
                acta = actaDeConstitucionRepository.findActaDeConstitucionByHerramientaXProyecto(herramientaXProyecto);
            } else {
                if (idherramienta == idHerramientaCronograma) {
                    cronograma = cronogramaRepository.findCronogramaByHerramientaXProyecto(herramientaXProyecto);
                } else {
                    if (idherramienta == idHerramientaProductBacklog) {
                        productBacklog = productBacklogRepository
                                .findProudctBacklogByHerramientaXProyecto(herramientaXProyecto);
                    } else {
                        if (idherramienta == idHerramientaPlanDeCalidad) {
                            planDeCalidad = planDeCalidadRepository
                                    .findPlanDeCalidadByHerramientaXProyecto(herramientaXProyecto);
                        } else {
                            if (idherramienta == idHerramientaEDT) {
                                edt = edtRepository.findEDTByHerramientaXProyecto(herramientaXProyecto);
                            } else {
                                if (idherramienta == idHerramientaPresupuesto) {
                                    presupuesto = presupuestoRepository
                                            .findPresupuestoByHerramientaXProyecto(herramientaXProyecto);
                                } else {
                                    if (idherramienta == idHerramientaCatalogoRiesgos) {
                                        catalogoDeRiesgos = catalogoRiesgosRepository
                                                .findCatalogoDeRiesgosByHerramientaXProyecto(herramientaXProyecto);
                                    } else {
                                        if (idherramienta == idHerramientaMatrizResp) {
                                            // System.out.println("Estoy buscando una matriz de responsabilidades");
                                            matrizResp = matrizDeResponsabilidadesRepository
                                                    .findMatrizDeResponsabilidadesByHerramientaXProyecto(
                                                            herramientaXProyecto);
                                        } else {
                                            if (idherramienta == idHerramientaMatrizCom) {
                                                // System.out.println("Estoy buscando una matriz de comunicaciones");
                                                matrizCom = matrizDeComunicacionesRepository
                                                        .findMatrizDeComunicacionesByHerramientaXProyecto(
                                                                herramientaXProyecto);
                                            } else {
                                                if (idherramienta == idHerramientaTabKanban) {
                                                    System.out.println("Estoy buscando un tablero kanban");
                                                    tabKanban = tableroKanbanRepository
                                                            .findTableroKanbanByHerramientaXProyecto(
                                                                    herramientaXProyecto);
                                                } else {
                                                    if (idherramienta == idHerramientaCatalogoInteresados) {
                                                        System.out.println("Estoy buscando un catalogo de interesados");
                                                        catalogoDeInteresados = catalogoDeInteresadosRepository
                                                                .findCatalogoDeInteresadosByHerramientaXProyecto(
                                                                        herramientaXProyecto);
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            /*
             * switch (plantilla.getHerramienta().getId()) {
             * case 8:
             * acta =
             * actaDeConstitucionRepository.findActaDeConstitucionByHerramientaXProyecto(
             * herramientaXProyecto);
             * break;
             * case idHerramientaCronograma:
             * cronograma = cronogramaRepository.findCronogramaByHerramientaXProyecto(
             * herramientaXProyecto);
             * break;
             * case idHerramientaProductBacklog:
             * productBacklog =
             * productBacklogRepository.findProudctBacklogByHerramientaXProyecto(
             * herramientaXProyecto);
             * break;
             * case idHerramientaPlanDeCalidad:
             * planDeCalidad =
             * planDeCalidadRepository.findPlanDeCalidadByHerramientaXProyecto(
             * herramientaXProyecto);
             * break;
             * case idHerramientaEDT:
             * edt = edtRepository.findEDTByHerramientaXProyecto(herramientaXProyecto);
             * break;
             * default:
             * 
             * }
             */

            tablas = tablaRepository.findTablaByPlantilla(plantilla);
            for (Tabla tabla : tablas) {
                Tabla nuevaTabla = new Tabla();
                nuevaTabla.setNombre(tabla.getNombre());
                nuevaTabla.setFactorDeOrden(tabla.getFactorDeOrden());
                nuevaTabla.setNumeroDeColumnas(tabla.getNumeroDeColumnas());
                nuevaTabla.setNumeroDeFilas(tabla.getNumeroDeFilas());
                nuevaTabla.setEsSeccion(tabla.isEsSeccion());
                nuevaTabla.setEsTabla(tabla.isEsTabla());
                if (acta != null)
                    nuevaTabla.setActaDeConstitucion(acta);
                if (cronograma != null)
                    nuevaTabla.setCronograma(cronograma);
                if (productBacklog != null)
                    nuevaTabla.setProductBacklog(productBacklog);
                if (planDeCalidad != null)
                    nuevaTabla.setPlanDeCalidad(planDeCalidad);
                if (edt != null)
                    nuevaTabla.setEdt(edt);
                if (matrizResp != null) {
                    // System.out.println("Estoy seteando una matriz de responsabilidades");
                    nuevaTabla.setMatrizDeResponsabilidades(matrizResp);
                }
                if (matrizCom != null) {
                    // System.out.println("Estoy seteando una matriz de comunicaciones");
                    nuevaTabla.setMatrizDeComunicaciones(matrizCom);
                }
                if (tabKanban != null) {
                    System.out.println("Estoy seteando un tablero kanban");
                    nuevaTabla.setTableroKanban(tabKanban);
                }
                if (catalogoDeInteresados != null) {
                    nuevaTabla.setCatalogoDeInteresados(catalogoDeInteresados);
                }
                if (presupuesto != null)
                    nuevaTabla.setPresupuesto(presupuesto);
                if (catalogoDeRiesgos != null)
                    nuevaTabla.setCatalogoDeRiesgos(catalogoDeRiesgos);
                Tabla tab = tablaRepository.save(nuevaTabla);
                nuevasTablas.add(tab);
                // atributos
                List<Atributo> atributos = new ArrayList<Atributo>();
                /* List<Atributo> */ atributos = atributoRepository.findAtributoByTabla(tabla);
                for (Atributo atributo : atributos) {
                    Atributo nuevoAtributo = new Atributo();
                    // System.out.println(atributo.getNombre());
                    nuevoAtributo.setNombre(atributo.getNombre());
                    nuevoAtributo.setFactorDeOrden(atributo.getFactorDeOrden());
                    nuevoAtributo.setSeMuestraEnPantallaPrincipal(atributo.isSeMuestraEnPantallaPrincipal());
                    nuevoAtributo.setTipoDeColumna(atributo.getTipoDeColumna());
                    nuevoAtributo.setSeMuestraEnReporte(atributo.isSeMuestraEnReporte());
                    nuevoAtributo.setEsEliminable(atributo.isEsEliminable());
                    nuevoAtributo.setEsParrafo(atributo.isEsParrafo());
                    nuevoAtributo.setTabla(tab);
                    atributoRepository.save(nuevoAtributo);
                    List<EtiquetaAtributo> etiquetaAtributos = etiquetaAtributoService
                            .listarEtiquetasAtributosPorAtributo(atributo.getId());

                    for (EtiquetaAtributo etiquetaAtributo : etiquetaAtributos) {
                        /*if (nuevoAtributo.isEsEliminabl(  )){
                            Etiqueta etiqueta = etiquetaService.get(etiquetaAtributo.getEtiqueta().getId());
                            Etiqueta nuevaEtiqueta = new Etiqueta();
                            nuevaEtiqueta.setColor(etiqueta.getColor());
                            nuevaEtiqueta.setValor(etiqueta.getValor());
                            nuevaEtiqueta.setContenido(etiqueta.getContenido());
                            etiquetaService.register(nuevaEtiqueta);
                            EtiquetaAtributo nuevaEtiquetaAtributo = new EtiquetaAtributo();
                            nuevaEtiquetaAtributo.setEtiqueta(nuevaEtiqueta);
                            nuevaEtiquetaAtributo.setAtributo(nuevoAtributo);
                            etiquetaAtributoService.register(nuevaEtiquetaAtributo);
                        } else {
                            EtiquetaAtributo nuevoEtiquetaAtributo = new EtiquetaAtributo();
                            nuevoEtiquetaAtributo.setEtiqueta(etiquetaAtributo.getEtiqueta());
                            nuevoEtiquetaAtributo.setAtributo(nuevoAtributo);
                            etiquetaAtributoService.register(nuevoEtiquetaAtributo);
                        }*/
                        EtiquetaAtributo nuevoEtiquetaAtributo = new EtiquetaAtributo();
                        nuevoEtiquetaAtributo.setEtiqueta(etiquetaAtributo.getEtiqueta());
                        nuevoEtiquetaAtributo.setAtributo(nuevoAtributo);
                        etiquetaAtributoService.register(nuevoEtiquetaAtributo);
                    }

                    // para trear los valores
                    List<Valor> valores = valorService.listarValorPorAtributo(atributo.getId());
                    for (Valor valor : valores) {
                        Valor nuevoValor = new Valor();

                        nuevoValor.setFactorOrden(valor.getFactorOrden());
                        nuevoValor.setContenido(valor.getContenido());
                        nuevoValor.setEsPrimario(valor.isEsPrimario());
                        nuevoValor.setAtributo(nuevoAtributo);
                        valorService.register(nuevoValor);
                    }
                }
            }

            return nuevasTablas;
        } catch (

        Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // update

    public Plantilla update(Plantilla plantilla) {
        try {
            return plantillaRepository.save(plantilla);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    // delete

    public void delete(int id) {
        try {
            plantillaRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
