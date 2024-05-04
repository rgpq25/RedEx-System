package pucp.e3c.redex_back.repository;

import java.sql.Date;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.e3c.redex_back.model.PlanRuta;

public interface PlanRutaRepository extends JpaRepository<PlanRuta, Integer> {
    public PlanRuta findById(int id);
}
