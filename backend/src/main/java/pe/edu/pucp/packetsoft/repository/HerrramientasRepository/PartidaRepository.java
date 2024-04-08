package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;


import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.Partida;
import pe.edu.pucp.packetsoft.models.Presupuesto;

import java.util.List;

@Repository
@Transactional
public interface PartidaRepository extends JpaRepository<Partida,Integer> {
    
    public List<Partida> findAll();
    public Partida findPartidaById(int id);

    @Query("FROM Partida p WHERE p.id = :idIngresado")
    public Partida duplicadoPropio(int idIngresado);

    //public Partida findPartidaByPresupuesto(Presupuesto presupuesto);
    public List<Partida> findPartidaByPresupuestoId(int id);
}
