package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.repository.PaqueteRepository;

@Service
public class PaqueteService {
    @Autowired
    private PaqueteRepository paqueteRepository; // Inyecta la dependencia

    public Paquete register(Paquete paquete) {
        return paqueteRepository.save(paquete);
    }

    public Paquete get(Integer id) {
        Optional<Paquete> optional_paquete = paqueteRepository.findById(id);
        return optional_paquete.get();
    }

    public List<Paquete> getAll() {
        return paqueteRepository.findAll();
    }

    public void delete(Integer id) {
        paqueteRepository.deleteById(id);
    }

    public Paquete update(Paquete paquete) {
        return paqueteRepository.save(paquete);
    }

    public List<Paquete> findByAeropuertoActualId(Integer id) {
        return paqueteRepository.findByAeropuertoActualId(id);
    }

    public List<Paquete> findByEnvioId(Integer id) {
        return paqueteRepository.findByEnvioId(id);
    }

    public List<Paquete> findBySimulacionId(Integer id) {
        return paqueteRepository.findBySimulacionActualId(id);
    }

    public Paquete findByPlanRutaId(Integer id) {
        return paqueteRepository.findByPlanRutaActualId(id);
    }

    public ArrayList<Paquete> findPaquetesWithoutPlanRutaSimulacion(String idUbicacionOrigen, Integer idSimulacion, Date fechaCorte){
        return paqueteRepository.findPaquetesWithoutPlanRutaSimulacion(idUbicacionOrigen, idSimulacion, fechaCorte);
    }

}
