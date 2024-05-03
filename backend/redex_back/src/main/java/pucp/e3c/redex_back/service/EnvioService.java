package pucp.e3c.redex_back.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pucp.e3c.redex_back.model.Envio;
import pucp.e3c.redex_back.repository.EnvioRepository;

import org.springframework.stereotype.Component;

@Component
public class EnvioService {
    @Autowired
    private EnvioRepository envioRepository; //Inyecta la dependencia

    public Envio register(Envio envio) {
        return envioRepository.save(envio);
    }

    public Envio get(Integer id) {
        Optional<Envio> optional_envio = envioRepository.findById(id);
        return optional_envio.get();
    }

    public List<Envio> getAll() {
        return envioRepository.findAll();
    }

    public void delete(Integer id) {
        envioRepository.deleteById(id);
    }

    public Envio update(Envio envio) {
        return envioRepository.save(envio);
    }

    public Envio findByCodigo_seguridad(String codigoSeguridad) {
        return envioRepository.findByCodigoSeguridad(codigoSeguridad);
    }    
    
}
