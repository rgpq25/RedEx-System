package pucp.e3c.redex_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pucp.e3c.redex_back.model.EstadoPaquete;

@Repository
public interface EstadoPaqueteRepository extends JpaRepository<EstadoPaquete, Integer>{
    
}
