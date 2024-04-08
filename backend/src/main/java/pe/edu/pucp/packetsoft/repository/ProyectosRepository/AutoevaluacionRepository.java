package pe.edu.pucp.packetsoft.repository.ProyectosRepository;

import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.Autoevaluacion;
import pe.edu.pucp.packetsoft.models.Cronograma;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.Proyecto;

import java.util.List;

@Repository
@Transactional
public interface AutoevaluacionRepository extends JpaRepository<Autoevaluacion, Integer>{
    public List<Autoevaluacion> findAll();
    public Autoevaluacion findAutoevaluacionById(int id);
    public Autoevaluacion findAutoevaluacionByProyecto(Proyecto proyecto);
}
