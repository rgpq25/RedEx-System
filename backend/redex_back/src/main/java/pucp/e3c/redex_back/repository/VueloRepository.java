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

    /*@Query("SELECT v FROM Vuelo v WHERE v.planVuelo.ciudadOrigen.id = :idUbicacion AND v.fechaSalida BETWEEN :fechaInicio AND :fechaFin AND v.simulacionActual.id = :idSimulacion")
    public ArrayList<Vuelo> findValidosAeropuertoSimulacion(Integer idSimulacion,String idUbicacion, Date fechaInicio, Date fechaFin);*/

}