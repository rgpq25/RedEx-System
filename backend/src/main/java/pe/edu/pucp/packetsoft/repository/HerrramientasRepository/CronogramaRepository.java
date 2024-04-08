package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;

import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.Cronograma;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;

import java.util.List;

@Repository
@Transactional
public interface CronogramaRepository extends JpaRepository<Cronograma, Integer>{
    public List<Cronograma> findAll();
    public Cronograma findCronogramaById(int id);

    @Query("FROM Cronograma c WHERE c.id = :idIngresado")
    public Cronograma duplicadoPropio(int idIngresado);

    //private HerramientaXProyecto herramientaXProyecto;
    public Cronograma findCronogramaByHerramientaXProyecto(HerramientaXProyecto herramientaXProyecto);
}
