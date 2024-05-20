package pucp.e3c.redex_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pucp.e3c.redex_back.model.Ubicacion;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, String>{
    
}
