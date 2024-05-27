package pucp.e3c.redex_back.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.Cliente;
import pucp.e3c.redex_back.repository.ClienteRepository;

@Service
public class ClienteService {
  @Autowired
  private ClienteRepository clienteRepository;

  public Cliente get(Integer id) {
    return clienteRepository.findById(id).orElse(null);
  }

  public List<Cliente> getAll() {
    return clienteRepository.findAll();
  }

  public Cliente register(Cliente usuario) {
    return clienteRepository.save(usuario);
  }

  public Cliente update(Cliente usuario) {
    return clienteRepository.save(usuario);
  }

  public void delete(Integer id) {
    clienteRepository.deleteById(id);
  }
}
