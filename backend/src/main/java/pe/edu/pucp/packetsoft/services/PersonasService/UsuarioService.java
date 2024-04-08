package pe.edu.pucp.packetsoft.services.PersonasService;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pe.edu.pucp.packetsoft.models.PersonasModel.Usuario;
import pe.edu.pucp.packetsoft.repository.PersonasRepository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service //Esta anotación marca la clase UsuarioService como un componente de servicio de Spring. Los componentes de servicio en Spring se utilizan para encapsular la lógica de negocio y proporcionar métodos para interactuar con los datos o realizar operaciones específicas.
public class UsuarioService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioService.class);
    //Esta anotación se utiliza para inyectar una instancia de UsuarioRepository en el servicio.
    // El UsuarioRepository generalmente es un componente de acceso a datos que proporciona métodos para interactuar con la base de datos y 
    //obtener información sobre los usuarios.
    @Autowired 
    private UsuarioRepository usuarioRepository;

    @PersistenceContext
    EntityManager entityManager;

    //CRUDS

    //create

    public Usuario register(Usuario user){
        try {
            if(user.getUsuario() == null || user.getUsuario().isEmpty()){
                throw new RuntimeException("El usuario no puede estar vacío");
            }
            return usuarioRepository.save(user);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    //read
    
    public List<Usuario> getAll(){
        try {
            return usuarioRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return usuarioRepository.findAll();
    }

    
    public Usuario get(int id){
        try {
            return usuarioRepository.findUsuarioById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return usuarioRepository.findUsuarioById(id);
    }

    //update
    
    public Usuario update(Usuario usuario){
        try {
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return usuarioRepository.save(usuario);
    }

    //delete
   
    public void delete(int id){
        try {
            usuarioRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        // usuarioRepository.deleteById(id);
    }

    //OTROS METODOS

    public boolean duplicadoExterno(String usuario, int proveedor){
        try {
            return usuarioRepository.duplicadoExterno(usuario, proveedor);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        // return usuarioRepository.duplicadoExterno(usuario, proveedor);
    }

    public Usuario duplicadoPropio(String usuario, int id){
        try {
            Usuario usu = null;
            Usuario usu1 = new Usuario();
            usu1.setId(id);
            usu = usuarioRepository.duplicadoPropio(usuario,id);

            if(usu==null){
                //NO SE ENCONTRO AL CLIENTE NO ESTÁ REGISTRADO EN LA BD
                return null;
            }else{
                //SE ENCONTRO AL USUARIO
                return usu;
            }
            //return usuarioRepository.duplicadoPropio(usuario, id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    //Esto si es seguro pues el password lo tiene el proveedor third party
    
    public Usuario getGoogleUserByUsername(String usuario){
        try {
            return usuarioRepository.getGoogleUserByUsername(usuario);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        // return usuarioRepository.getGoogleUserByUsername(usuario);
    }

    public Usuario login(Usuario dto) {
        try {
            boolean isAuthenticated = false;
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
            
            Usuario result = usuarioRepository.findUsuarioByUsuario(dto.getUsuario());
            
            if(result.getUsuario() == null) return null;

            Usuario user = result;
            if(!(dto.getPassword()==null || dto.getPassword().isEmpty())){
                isAuthenticated = argon2.verify(user.getPassword(), dto.getPassword());
            }

            if(isAuthenticated) return user;

            return null;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
    
    //Modificar el password no afecta la data almacenada en cache porque el password no es visible
    public boolean updatePassword(int id_usuario, String username, String old_password, String new_password){
        boolean resultado = false;
        
        try{
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
            Usuario user = get(id_usuario);

            // Validar que el username y old password coincidan con el usuario actual
            if(user.getUsuario().compareTo(username) == 0 &&  argon2.verify(user.getPassword(), old_password)){
                //Hashear el nuevo password y actualizar el usuario
                user.setPassword(argon2.hash(1, 1024*1, 1, new_password));
                if(user.getFecha_modificacion() != null)
                    user.setFecha_modificacion(new Date());

                entityManager.merge(user);
                resultado = true;
            }
        }
        catch(Exception ex){ 
            LOGGER.error(ex.getMessage());
            System.out.println(ex.getMessage()); 
        }

        return resultado;
        
    }

    /*public boolean forgotPassword(String username, String generated_password){
        return daoUsuario.forgotPassword(username, generated_password);
    }*/

}
