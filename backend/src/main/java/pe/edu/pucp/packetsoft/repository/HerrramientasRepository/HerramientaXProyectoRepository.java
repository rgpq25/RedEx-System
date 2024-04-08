package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;



import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.Herramienta;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.Proyecto;

import java.util.List;

@Repository
@Transactional
public interface HerramientaXProyectoRepository extends JpaRepository<HerramientaXProyecto, Integer>{
    public List<HerramientaXProyecto> findAll();
    public HerramientaXProyecto findHerramientaXProyectoById(int id);
    public List<HerramientaXProyecto> findHerramientaXProyectoByHerramienta(Herramienta herramienta);
    public List<HerramientaXProyecto> findHerramientaXProyectoByProyecto(Proyecto proyecto);
    //private Proyecto proyecto;
    //private Herramienta herramienta;
}
