package pe.edu.pucp.packetsoft.repository.PersonasRepository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure; 
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;

import pe.edu.pucp.packetsoft.models.PersonasModel.Solicitud;

import java.util.List;
@Repository
@Transactional
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
   
    public List<Solicitud> findAll();
    public Solicitud findSolicitudById(int id);
}
