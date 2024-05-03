package pucp.e3c.redex_back.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pucp.e3c.redex_back.model.PaqueteXEstadoPaquete;
import pucp.e3c.redex_back.repository.PaqueteXEstadoPaqueteRepository;

@Component
public class PaqueteXEstadoPaqueteService {
    @Autowired
    private PaqueteXEstadoPaqueteRepository paqueteXEstadoPaqueteRepository; //Inyecta la dependencia
    
    public List<PaqueteXEstadoPaquete> findByPaqueteId(Integer id) {
        return paqueteXEstadoPaqueteRepository.findByPaqueteId(id);
    }

    public List<PaqueteXEstadoPaquete> findByEstadoPaqueteId(Integer id) {
        return paqueteXEstadoPaqueteRepository.findByEstadoPaqueteId(id);
    }

    public PaqueteXEstadoPaquete register(PaqueteXEstadoPaquete paqueteXEstadoPaquete) {
        return paqueteXEstadoPaqueteRepository.save(paqueteXEstadoPaquete);
    }

    public PaqueteXEstadoPaquete get(Integer id) {
        Optional<PaqueteXEstadoPaquete> optional_paqueteXEstadoPaquete = paqueteXEstadoPaqueteRepository.findById(id);
        return optional_paqueteXEstadoPaquete.get();
    }

    public List<PaqueteXEstadoPaquete> getAll() {
        return paqueteXEstadoPaqueteRepository.findAll();
    }

    public void delete(Integer id) {
        paqueteXEstadoPaqueteRepository.deleteById(id);
    }

    public PaqueteXEstadoPaquete update(PaqueteXEstadoPaquete paqueteXEstadoPaquete) {
        return paqueteXEstadoPaqueteRepository.save(paqueteXEstadoPaquete);
    }
}
