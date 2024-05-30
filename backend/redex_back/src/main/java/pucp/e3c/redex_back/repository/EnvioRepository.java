package pucp.e3c.redex_back.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pucp.e3c.redex_back.model.Envio;

@Transactional
@Repository
public interface EnvioRepository extends JpaRepository<Envio, Integer> {
    // private String codigoSeguridad;
    public Envio findByCodigoSeguridad(String codigoSeguridad);

    public ArrayList<Envio> findBySimulacionActualId(Integer id);

    @Query("SELECT e FROM Envio e WHERE e.simulacionActual IS NULL")
    public ArrayList<Envio> findEnviosSinSimulacion();
}
