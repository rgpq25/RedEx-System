package pucp.e3c.redex_back.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pucp.e3c.redex_back.model.PlanRuta;
import pucp.e3c.redex_back.repository.PlanRutaRepository;

@Component
public class PlanRutaService {
    @Autowired
    private PlanRutaRepository planRutaRepository;

    public void register(PlanRuta planRuta) {
        planRutaRepository.save(planRuta);
    }

    public void put(PlanRuta planRuta) {
        planRutaRepository.save(planRuta);
    }

    public PlanRuta get(int id) {
        return planRutaRepository.findById(id);
    }

    public List<PlanRuta> getAll() {
        return planRutaRepository.findAll();
    }

    public void delete(int id) {
        planRutaRepository.deleteById(id);
    }

    
}
