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

import pucp.e3c.redex_back.model.Usuario;
import pucp.e3c.redex_back.service.UsuarioService;

@RestController
@CrossOrigin(origins = "https://inf226-981-3c.inf.pucp.edu.pe")
@RequestMapping("back/usuario")
public class UsuarioController {

  @Autowired
  private UsuarioService usuarioService;

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
}
