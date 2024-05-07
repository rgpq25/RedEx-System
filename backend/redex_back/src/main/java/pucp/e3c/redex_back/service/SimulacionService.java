package pucp.e3c.redex_back.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.Simulacion;
import pucp.e3c.redex_back.repository.SimulacionRepository;

@Service
public class SimulacionService {
    @Autowired
    private SimulacionRepository simulacionRepository;

    public Simulacion register(Simulacion simulacion) {
        return simulacionRepository.save(simulacion);
    }

    public Simulacion get(int id) {
        return simulacionRepository.findById(id).orElse(null);
    }

    public Simulacion update(Simulacion simulacion) {
        return simulacionRepository.save(simulacion);
    }

    public void delete(int id) {
        simulacionRepository.deleteById(id);
    }

    public List<Simulacion> getAll() {
        return simulacionRepository.findAll();
    }
}
