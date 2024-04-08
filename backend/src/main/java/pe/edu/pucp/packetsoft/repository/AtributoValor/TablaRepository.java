package pe.edu.pucp.packetsoft.repository.AtributoValor;
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
import pe.edu.pucp.packetsoft.models.ProductBacklog;
import pe.edu.pucp.packetsoft.models.Presupuesto;
import pe.edu.pucp.packetsoft.models.Tabla;
import pe.edu.pucp.packetsoft.models.TableroKanban;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface TablaRepository extends JpaRepository<Tabla, Integer>{    
    public List<Tabla> findAll();
    public Tabla findTablaById(int id);

    @Query("FROM Tabla t WHERE t.id = :idIngresado")
    public Tabla duplicadoPropio(int idIngresado);

    public List<Tabla> findTablaByPlantilla(Plantilla plantilla);
    public List<Tabla> findTablaByPlanDeCalidad(PlanDeCalidad planDeCalidad);
    public List<Tabla> findTablaByActaDeConstitucion(ActaDeConstitucion actaDeConstitucion);
    public Tabla findTablaByCronograma(Cronograma cronograma);
    public Tabla findTablaByEdt(EDT edt);
    public Tabla findTablaByProductBacklog(ProductBacklog productBacklog);
    public Tabla findTablaByPresupuesto(Presupuesto presupuesto);
    public Tabla findTablaByCatalogoDeRiesgos(CatalogoDeRiesgos catalogoDeRiesgos);
    public Tabla findTablaByMatrizDeResponsabilidades(MatrizDeResponsabilidades matrizDeResponsabilidades);
    public Tabla findTablaByMatrizDeComunicaciones(MatrizDeComunicaciones matrizDeComunicaciones);
    public List<Tabla> findTablaByTableroKanban(TableroKanban tableroKanban);

    public Tabla findTablaByCatalogoDeInteresados(CatalogoDeInteresados catalogoDeInteresados);

    public Tabla findTablaByAutoevaluacion(Autoevaluacion autoevaluacion);

    //private Plantilla plantilla;
    //private PlanDeCalidad planDeCalidad;
    //private ActaDeConstitucion actaDeConstitucion;
    //private Cronograma cronograma;
    //private EDT edt;
    //private ProductBacklog productBacklog;
}
