package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;

import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.CatalogoDeInteresados;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;


import java.util.List;

@Repository
@Transactional

public interface CatalogoDeInteresadosRepository extends JpaRepository<CatalogoDeInteresados,Integer> {

    public List<CatalogoDeInteresados> findAll();
    public CatalogoDeInteresados findCatalogoDeInteresadosById(int id);

    @Query("FROM CatalogoDeInteresados p WHERE p.id = :idIngresado")
    public CatalogoDeInteresados duplicadoPropio(int idIngresado);

    public CatalogoDeInteresados findCatalogoDeInteresadosByHerramientaXProyecto(HerramientaXProyecto herramientaXProyecto);
    
}
