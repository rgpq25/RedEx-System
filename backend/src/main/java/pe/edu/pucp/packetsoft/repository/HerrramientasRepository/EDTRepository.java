package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;


import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.Cronograma;
import pe.edu.pucp.packetsoft.models.EDT;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;

import java.util.List;

@Repository
@Transactional
public interface EDTRepository extends JpaRepository<EDT, Integer>{
    public List<EDT> findAll();
    public EDT findEDTById(int id);

    @Query("FROM EDT e WHERE e.id = :idIngresado")
    public EDT duplicadoPropio(int idIngresado);

    public EDT findEDTByHerramientaXProyecto(HerramientaXProyecto herramientaXProyecto);

    //private Cronograma cronograma;

    public EDT findEDTByCronograma(Cronograma cronograma);

}
