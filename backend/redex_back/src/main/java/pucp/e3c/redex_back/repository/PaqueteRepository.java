package pucp.e3c.redex_back.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.Vuelo;

import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Integer> {
    public ArrayList<Paquete> findByAeropuertoActualId(Integer id);

    public ArrayList<Paquete> findByEnvioId(Integer id);

    public ArrayList<Paquete> findBySimulacionActualId(Integer id);

    public Paquete findByPlanRutaActualId(Integer id);

    @Query("SELECT p FROM Paquete p WHERE p.planRutaActual IS NULL AND p.envio.ubicacionOrigen.id = :idUbicacionOrigen AND p.simulacionActual.id = :idSimulacion AND p.envio.fechaRecepcion < :fechaCorte")
    public ArrayList<Paquete> findPaquetesWithoutPlanRutaSimulacion(String idUbicacionOrigen, Integer idSimulacion,
            Date fechaCorte);

    @Query("SELECT p FROM Paquete p WHERE p.planRutaActual IS NULL AND p.envio.ubicacionOrigen.id = :idUbicacionOrigen AND p.simulacionActual IS NULL AND p.envio.fechaRecepcion < :fechaCorte")
    public ArrayList<Paquete> findPaquetesWithoutPlanRuta(String idUbicacionOrigen, Date fechaCorte);

    @Query("SELECT p FROM Paquete p WHERE p.simulacionActual IS NULL AND p.entregado = false")
    public ArrayList<Paquete> findPaquetesSinSimulacionYNoEntregados();

    @Query("SELECT p FROM Paquete p WHERE p.simulacionActual IS NULL")
    public ArrayList<Paquete> findPaquetesOperacionesDiaDia();

    // AND (p.fechaDeEntrega > :fechaCorte OR p.fechaDeEntrega IS NULL)
    @Query("SELECT p FROM Paquete p WHERE p.simulacionActual.id = :idSimulacion AND p.envio.fechaRecepcion < :fechaCorte ")
    public ArrayList<Paquete> findPaqueteSimulacionFechaCorte(int idSimulacion, Date fechaCorte);

    @Query("SELECT p FROM Paquete p WHERE p.simulacionActual.id = :idSimulacion AND p.envio.fechaRecepcion < :fechaCorte AND (p.entregado IS FALSE OR p.fechaDeEntrega > :fechaCorte)")
    public List<Paquete> findPaqueteSimulacionFechaCorteNoEntregados(int idSimulacion, Date fechaCorte);

}
