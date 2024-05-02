package pucp.e3c.redex_back.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pucp.e3c.redex_back.model.EstadoPaquete;
import pucp.e3c.redex_back.repository.EstadoPaqueteRepository;

@Component
public class EstadoPaqueteService {
    @Autowired
    private EstadoPaqueteRepository estadoPaqueteRepository; //Inyecta la dependencia

    public EstadoPaquete register(EstadoPaquete estadoPaquete) {
        return estadoPaqueteRepository.save(estadoPaquete);
    }

    public EstadoPaquete get(Integer id) {
        Optional<EstadoPaquete> optional_estado_paquete = estadoPaqueteRepository.findById(id);
        return optional_estado_paquete.get();
    }

    public List<EstadoPaquete> getAll() {
        return estadoPaqueteRepository.findAll();
    }

    public void delete(Integer id) {
        estadoPaqueteRepository.deleteById(id);
    }

    public EstadoPaquete update(EstadoPaquete estadoPaquete) {
        return estadoPaqueteRepository.save(estadoPaquete);
    }
}
