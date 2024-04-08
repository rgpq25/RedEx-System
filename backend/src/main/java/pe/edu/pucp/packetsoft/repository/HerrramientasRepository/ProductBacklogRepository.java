package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.EDT;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.ProductBacklog;

import java.util.List;

@Repository
@Transactional
public interface ProductBacklogRepository extends JpaRepository<ProductBacklog, Integer>{
    public List<ProductBacklog> findAll();
    public ProductBacklog findProductBacklogById(int id);

    @Query("FROM ProductBacklog p WHERE p.id = :idIngresado")
    public ProductBacklog duplicadoPropio(int idIngresado);

    public ProductBacklog findProudctBacklogByHerramientaXProyecto(HerramientaXProyecto herramientaXProyecto);
}
