package pucp.e3c.redex_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.e3c.redex_back.model.PlanRutaXPaquete;

public interface PlanRutaXPaqueteRepository extends JpaRepository<PlanRutaXPaquete, Integer> {
    public List<PlanRutaXPaquete> findByPlanRutaId(Integer id);

    public List<PlanRutaXPaquete> findByPaqueteId(Integer id);
}
