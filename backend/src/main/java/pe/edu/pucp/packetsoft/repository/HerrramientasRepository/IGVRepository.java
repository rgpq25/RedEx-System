package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;

import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.IGV;

import java.util.List;

@Repository
@Transactional
public interface IGVRepository extends JpaRepository<IGV,Integer>{
    
    public List<IGV> findAll();
    public IGV findIGVById(int id);

    @Query("FROM IGV x WHERE x.id = :idIngresado")
    public IGV duplicadoPropio(int idIngresado);
}
