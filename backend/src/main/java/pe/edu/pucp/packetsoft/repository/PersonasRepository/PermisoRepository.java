package pe.edu.pucp.packetsoft.repository.PersonasRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.pucp.packetsoft.models.PersonasModel.Permiso;

@Repository
@Transactional
public interface PermisoRepository extends JpaRepository<Permiso, Integer>{
    Permiso findPermisoById(int id);
}
