package pucp.e3c.redex_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.e3c.redex_back.model.PlanRutaXVuelo;

public interface PlanRutaXVueloRepository extends JpaRepository<PlanRutaXVuelo, Integer> {
    public List<PlanRutaXVuelo> findByPlanRutaId(Integer id);

    public List<PlanRutaXVuelo> findByVueloId(Integer id);
}