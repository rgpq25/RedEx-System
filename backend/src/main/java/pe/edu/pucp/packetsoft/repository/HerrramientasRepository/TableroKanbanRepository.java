package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;

import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.ProductBacklog;
import pe.edu.pucp.packetsoft.models.TableroKanban;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;

import java.util.List;

@Repository
@Transactional
public interface TableroKanbanRepository extends JpaRepository<TableroKanban,Integer>{

    public List<TableroKanban> findAll();
    public TableroKanban findTableroKanbanById(int id);

    @Query("FROM TableroKanban p WHERE p.id = :idIngresado")
    public TableroKanban duplicadoPropio(int idIngresado);

    public TableroKanban findTableroKanbanByHerramientaXProyecto(HerramientaXProyecto herramientaXProyecto);
    public TableroKanban findTableroKanbanByPersona(Persona persona);
    public TableroKanban findTableroKanbanByProductBacklog(ProductBacklog productBacklog);
}