package pucp.e3c.redex_back.repository;

import java.sql.Date;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pucp.e3c.redex_back.model.PlanRuta;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface PlanRutaRepository extends JpaRepository<PlanRuta, Integer> {
    public Optional<PlanRuta> findById(int id);
}
