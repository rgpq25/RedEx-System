package pucp.e3c.redex_back.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.repository.UbicacionRepository;

@Component
public class UbicacionService {
    @Autowired
    private UbicacionRepository ubicacionRepository; //Inyecta la dependencia

    public Ubicacion register(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    public Ubicacion get(String id) {
        Optional<Ubicacion> optional_ubicacion = ubicacionRepository.findById(id);
        return optional_ubicacion.get();
    }

    public List<Ubicacion> getAll() {
        return ubicacionRepository.findAll();
    }

    public void delete(String id) {
        ubicacionRepository.deleteById(id);
    }

    public Ubicacion update(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }
}
