package pucp.e3c.redex_back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.model.Cliente;
import pucp.e3c.redex_back.model.Usuario;
import pucp.e3c.redex_back.model.RegistroUsuario;
import pucp.e3c.redex_back.service.ClienteService;
import pucp.e3c.redex_back.service.UsuarioService;

@RestController
@CrossOrigin(origins = "https://inf226-981-3c.inf.pucp.edu.pe")
@RequestMapping("back/usuario")
public class UsuarioController {

  @Autowired
  private UsuarioService usuarioService;

  @Autowired
  private ClienteService clienteService;

  @PostMapping(value = "/")
  public Usuario register(@RequestBody Usuario usuario) {
    return usuarioService.register(usuario);
  }

  @PutMapping(value = "/")
  public Usuario update(@RequestBody Usuario usuario) {
    return usuarioService.update(usuario);
  }

  @GetMapping(value = "/")
  public List<Usuario> getAll() {
    return usuarioService.getAll();
  }

  @GetMapping(value = "/{id}")
  public Usuario get(@PathVariable("id") Integer id) {
    return usuarioService.get(id);
  }

  @DeleteMapping(value = "/{id}")
  public void delete(@PathVariable("id") Integer id) {
    usuarioService.delete(id);
  }
  @GetMapping(value = "/correo/{correo}")
  public Usuario findByCorreo(@PathVariable("correo")String correo){
    return usuarioService.findByCorreo(correo);
  }

  @PostMapping(value = "/validarRegistrar/")
  public RegistroUsuario validarRegistrar(@RequestBody Usuario usuario){
    Usuario usuarioBD = usuarioService.findByCorreo(usuario.getCorreo());
    if(usuarioBD == null){
      Usuario user = usuarioService.register(usuario);
      Cliente cliente = new Cliente();
      cliente.setUsuario(user);
      cliente.setInformacionContacto("Ninguna");
      cliente.setPreferenciasNotificacion("Ninguna");
      cliente = clienteService.register(cliente);
      return new RegistroUsuario(user, cliente);
    }
    else{
      Cliente cliente = clienteService.findByUsuarioId(usuarioBD.getId());
      if(cliente == null){
        cliente = new Cliente();
        cliente.setUsuario(usuarioBD);
        cliente.setInformacionContacto("Ninguna");
        cliente.setPreferenciasNotificacion("Ninguna");
        cliente = clienteService.register(cliente);
      }
      return new RegistroUsuario(usuarioBD, cliente);
    }
  }
}
