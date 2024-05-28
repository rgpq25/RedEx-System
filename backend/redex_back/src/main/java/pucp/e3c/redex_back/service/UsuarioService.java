package pucp.e3c.redex_back.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.Usuario;
import pucp.e3c.redex_back.repository.UsuarioRepository;

@Service
public class UsuarioService {
  @Autowired
  private UsuarioRepository usuarioRepository;

  public Usuario get(Integer id) {
    return usuarioRepository.findById(id).orElse(null);
  }

  public List<Usuario> getAll() {
    return usuarioRepository.findAll();
  }

  public Usuario register(Usuario usuario) {
    return usuarioRepository.save(usuario);
  }

  public Usuario update(Usuario usuario) {
    return usuarioRepository.save(usuario);
  }

  public void delete(Integer id) {
    usuarioRepository.deleteById(id);
  }
}
