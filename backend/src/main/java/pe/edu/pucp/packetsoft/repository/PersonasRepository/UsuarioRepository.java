package pe.edu.pucp.packetsoft.repository.PersonasRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.PersonasModel.Usuario;

import java.util.List;

@Repository
@Transactional
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

    //CRUDS
    //read
    public List<Usuario> findAll();
    public Usuario findUsuarioById(int id);

    //public boolean updatePassword(int id_usuario, String username, String old_password, String new_password);
    
    //public boolean forgotPassword(String username, String generated_password);
    public Usuario findUsuarioByUsuario(String usuario);
    
    @Query ("FROM Usuario u WHERE u.usuario = :usuario and proveedor = 1")
    public Usuario getGoogleUserByUsername(String usuario);

    @Query("SELECT CASE WHEN COUNT(*) = 0 THEN false ELSE true END FROM Usuario u WHERE u.usuario = :usuario AND u.proveedor <> :proveedor")
    public boolean duplicadoExterno(@Param("usuario") String usuario, @Param("proveedor") int proveedor);

    //@Query("SELECT CASE WHEN COUNT(u) = 0 THEN false ELSE true END FROM Usuario u WHERE u.usuario = :usuario AND u.id <> :id")
    //public boolean duplicadoPropio(@Param("usuario") String usuario, @Param("id") long id);
    
    @Query("FROM Usuario u WHERE u.usuario = :usuarioIngresado AND u.id =:idIngresado")
    public Usuario duplicadoPropio(String usuarioIngresado, int idIngresado);

}