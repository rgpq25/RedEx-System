package pucp.e3c.redex_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pucp.e3c.redex_back.model.PaqueteXEstadoPaquete;

@Repository
public interface PaqueteXEstadoPaqueteRepository extends JpaRepository<PaqueteXEstadoPaquete, Integer>{
    public List<PaqueteXEstadoPaquete> findByPaqueteId(Integer id);
    public List<PaqueteXEstadoPaquete> findByEstadoPaqueteId(Integer id);
}
