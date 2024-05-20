package pucp.e3c.redex_back.repository;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pucp.e3c.redex_back.model.Paquete;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Integer> {
    public ArrayList<Paquete> findByAeropuertoActualId(Integer id);

    public ArrayList<Paquete> findByEnvioId(Integer id);

    public ArrayList<Paquete> findBySimulacionActualId(Integer id);

    public Paquete findByPlanRutaActualId(Integer id);

    @Query("SELECT p FROM Paquete p WHERE p.planRutaActual IS NULL AND p.envio.ubicacionOrigen.id = :idUbicacionOrigen AND p.simulacionActual.id = :idSimulacion AND p.envio.fechaRecepcion < :fechaCorte")
    public ArrayList<Paquete> findPaquetesWithoutPlanRutaSimulacion(String idUbicacionOrigen, Integer idSimulacion, Date fechaCorte);
}
