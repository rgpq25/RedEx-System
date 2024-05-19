package pucp.e3c.redex_back.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pucp.e3c.redex_back.model.PlanVuelo;
import pucp.e3c.redex_back.model.Ubicacion;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface PlanVueloRepository extends JpaRepository<PlanVuelo, Integer> {
    public PlanVuelo findById(int id);

    public ArrayList<PlanVuelo> findByCiudadOrigen(Ubicacion ciudadOrigen);

    public ArrayList<PlanVuelo> findByCiudadDestino(Ubicacion ciudadDestino);

    public ArrayList<PlanVuelo> findByCiudadOrigenAndCiudadDestino(Ubicacion ciudadOrigen, Ubicacion ciudadDestino);
}
