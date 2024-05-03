package pucp.e3c.redex_back.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.model.Paquete;

@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, String>{
    public List<Paquete> findByAeropuertoActual(Aeropuerto aeropuertoActual);
    public List<Paquete> findByEnvioId(Integer id);
}
