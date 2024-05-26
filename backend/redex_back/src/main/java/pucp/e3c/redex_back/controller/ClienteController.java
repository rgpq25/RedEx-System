package pucp.e3c.redex_back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.model.Cliente;
import pucp.e3c.redex_back.service.ClienteService;

@RestController
@RequestMapping("back/cliente")
public class ClienteController {

  @Autowired
  private ClienteService clienteService;

  @PostMapping(value = "/")
  public Cliente register(@RequestBody Cliente cliente) {
    return clienteService.register(cliente);
  }

  @PutMapping(value = "/")
  public Cliente update(@RequestBody Cliente cliente) {
    return clienteService.update(cliente);
  }

  @GetMapping(value = "/")
  public List<Cliente> getAll() {
    return clienteService.getAll();
  }

  @GetMapping(value = "/{id}")
  public Cliente get(@PathVariable("id") Integer id) {
    return clienteService.get(id);
  }

  @DeleteMapping(value = "/{id}")
  public void delete(@PathVariable("id") Integer id) {
    clienteService.delete(id);
  }
}