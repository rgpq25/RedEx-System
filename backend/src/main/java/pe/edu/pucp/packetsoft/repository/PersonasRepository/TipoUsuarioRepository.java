package pe.edu.pucp.packetsoft.repository.PersonasRepository;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.pucp.packetsoft.models.PersonasModel.TipoUsuario;
import java.util.List;

@Repository
@Transactional
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Integer>{
    public  List <TipoUsuario> findAllByPermisoIsNotNull( );
    TipoUsuario findTipoUsuarioById(int id);
    TipoUsuario findTipoUsuarioByNombre(String nombre);
}
