package pucp.e3c.redex_back.repository;

import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pucp.e3c.redex_back.model.Paquete;

@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Integer> {
    public ArrayList<Paquete> findByAeropuertoActualId(Integer id);

    public ArrayList<Paquete> findByEnvioId(Integer id);

    public ArrayList<Paquete> findBySimulacionActualId(Integer id);
}
