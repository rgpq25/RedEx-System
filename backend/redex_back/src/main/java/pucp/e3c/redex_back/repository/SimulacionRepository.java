package pucp.e3c.redex_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.e3c.redex_back.model.Simulacion;

public interface SimulacionRepository extends JpaRepository<Simulacion, Integer> {
    public Simulacion finById(Integer id);
}
