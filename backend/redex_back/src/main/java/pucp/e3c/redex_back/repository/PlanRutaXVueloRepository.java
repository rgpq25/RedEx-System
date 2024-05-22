package pucp.e3c.redex_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pucp.e3c.redex_back.model.PlanRutaXVuelo;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface PlanRutaXVueloRepository extends JpaRepository<PlanRutaXVuelo, Integer> {
    public List<PlanRutaXVuelo> findByPlanRutaId(Integer id);

    public List<PlanRutaXVuelo> findByVueloId(Integer id);
}