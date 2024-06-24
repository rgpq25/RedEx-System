package pucp.e3c.redex_back.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.repository.UbicacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UbicacionService {
    @Autowired
    private UbicacionRepository ubicacionRepository; // Inyecta la dependencia

    private static final Logger LOGGER = LoggerFactory.getLogger(UbicacionService.class);

    public Ubicacion register(Ubicacion ubicacion) {
        // return ubicacionRepository.save(ubicacion);
        try {
            return ubicacionRepository.save(ubicacion);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Ubicacion get(String id) {
        // Optional<Ubicacion> optional_ubicacion = ubicacionRepository.findById(id);
        // return optional_ubicacion.get();
        try {
            Optional<Ubicacion> optional_ubicacion = ubicacionRepository.findById(id);
            return optional_ubicacion.get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Ubicacion> getAll() {
        // return ubicacionRepository.findAll();
        try {
            return ubicacionRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;

        }
    }

    public void delete(String id) {
        // ubicacionRepository.deleteById(id);
        try {
            ubicacionRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Ubicacion update(Ubicacion ubicacion) {
        // return ubicacionRepository.save(ubicacion);
        try {
            return ubicacionRepository.save(ubicacion);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void deleteAll() {
        try {
            ubicacionRepository.deleteAll();
            ubicacionRepository.flush();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
