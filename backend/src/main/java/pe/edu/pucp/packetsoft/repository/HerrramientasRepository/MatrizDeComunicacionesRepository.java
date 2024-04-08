package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;

import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.MatrizDeComunicaciones;

import java.util.List;

@Repository
@Transactional
public interface MatrizDeComunicacionesRepository extends JpaRepository<MatrizDeComunicaciones,Integer>{
    
    public List<MatrizDeComunicaciones> findAll();
    public MatrizDeComunicaciones findMatrizDeComunicacionesById(int id);

    @Query("FROM MatrizDeComunicaciones p WHERE p.id = :idIngresado")
    public MatrizDeComunicaciones duplicadoPropio(int idIngresado);

    public MatrizDeComunicaciones findMatrizDeComunicacionesByHerramientaXProyecto(HerramientaXProyecto herramientaXProyecto);
}
