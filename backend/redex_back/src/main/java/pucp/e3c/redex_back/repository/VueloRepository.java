package pucp.e3c.redex_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pucp.e3c.redex_back.model.Vuelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public interface VueloRepository extends JpaRepository<Vuelo, Integer> {
    public Optional<Vuelo> findById(Integer id);

    public ArrayList<Vuelo> findByPlanVueloId(Integer id);

    @Query("SELECT v FROM Vuelo v WHERE v.planVuelo.ciudadOrigen.id = :idUbicacion AND v.fechaSalida BETWEEN :fechaInicio AND :fechaFin")
    public ArrayList<Vuelo> findValidos(String idUbicacion, Date fechaInicio, Date fechaFin);

    @Query("SELECT v FROM Vuelo v WHERE (v.planVuelo.ciudadOrigen.id = :idUbicacion OR v.planVuelo.ciudadDestino.id = :idUbicacion) AND v.simulacionActual.id = :idSimulacion")
    public ArrayList<Vuelo> findValidosAeropuertoSimulacion(Integer idSimulacion,String idUbicacion);

    @Query("SELECT v FROM Vuelo v WHERE v.simulacionActual.id = :idSimulacion AND v.planVuelo.ciudadDestino.id = :idUbicacion AND v.fechaLlegada < :fechaCorte")
    public ArrayList<Vuelo> findVuelosDestinoAeropuertoSimulacionFechaCorte(Integer idSimulacion, String idUbicacion, Date fechaCorte);

    @Query("SELECT v FROM Vuelo v WHERE v.simulacionActual.id = :idSimulacion AND v.planVuelo.ciudadOrigen.id = :idUbicacion AND v.fechaSalida > :fechaCorte")
    public ArrayList<Vuelo> findVuelosOrigenAeropuertoSimulacionFechaCorte(Integer idSimulacion, String idUbicacion, Date fechaCorte);

}