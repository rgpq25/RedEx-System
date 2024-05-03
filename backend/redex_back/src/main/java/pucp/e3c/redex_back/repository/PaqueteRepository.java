package pucp.e3c.redex_back.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.model.Paquete;

@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Integer>{
    public List<Paquete> findByAeropuertoActualId(Integer id);
    public List<Paquete> findByEnvioId(Integer id);
}
