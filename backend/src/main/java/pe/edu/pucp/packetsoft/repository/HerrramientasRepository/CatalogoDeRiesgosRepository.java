package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;


import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.CatalogoDeRiesgos;
import pe.edu.pucp.packetsoft.models.Cronograma;

import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;

import java.util.List;

@Repository
@Transactional
public interface CatalogoDeRiesgosRepository extends JpaRepository<CatalogoDeRiesgos, Integer>{
    
    public List<CatalogoDeRiesgos> findAll();
    public CatalogoDeRiesgos findCatalogoDeRiesgosById(int id);

    @Query("FROM CatalogoDeRiesgos c WHERE c.id = :idIngresado")
    public CatalogoDeRiesgos duplicadoPropio(int idIngresado);

    public CatalogoDeRiesgos findCatalogoDeRiesgosByHerramientaXProyecto(HerramientaXProyecto herramientaXProyecto);

}
