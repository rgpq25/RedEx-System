package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;

import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.Presupuesto;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;

import java.util.List;

@Repository
@Transactional
public interface PresupuestoRepository extends JpaRepository<Presupuesto,Integer>{
    
    public List<Presupuesto> findAll();
    public Presupuesto findPresupuestoById(int id);

    @Query("FROM Presupuesto p WHERE p.id = :idIngresado")
    public Presupuesto duplicadoPropio(int idIngresado);

    public Presupuesto findPresupuestoByHerramientaXProyecto(HerramientaXProyecto herramientaXProyecto);

}
