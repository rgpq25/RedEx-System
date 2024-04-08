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
import pe.edu.pucp.packetsoft.models.MatrizDeResponsabilidades;

import java.util.List;

@Repository
@Transactional
public interface MatrizDeResponsabilidadesRepository extends JpaRepository<MatrizDeResponsabilidades,Integer>{
    
    public List<MatrizDeResponsabilidades> findAll();
    public MatrizDeResponsabilidades findMatrizDeResponsabilidadesById(int id);

    @Query("FROM MatrizDeResponsabilidades p WHERE p.id = :idIngresado")
    public MatrizDeResponsabilidades duplicadoPropio(int idIngresado);

    public MatrizDeResponsabilidades findMatrizDeResponsabilidadesByHerramientaXProyecto(HerramientaXProyecto herramientaXProyecto);
    public MatrizDeResponsabilidades findMatrizDeResponsabilidadesByEdt(EDT edt);
}
