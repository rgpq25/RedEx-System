package pucp.e3c.redex_back.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.repository.AeropuertoRepository;
import pucp.e3c.redex_back.repository.UbicacionRepository;

@Component
public class AeropuertoService {
    @Autowired
    private AeropuertoRepository aeropuertoRepository; //Inyecta la dependencia

    @Autowired
    private UbicacionRepository ubicacionRepository;

    public Aeropuerto register(Aeropuerto aeropuerto) {
        return aeropuertoRepository.save(aeropuerto);
    }

    public Aeropuerto get(Integer id) {
        Optional<Aeropuerto> optional_aeropuerto = aeropuertoRepository.findById(id);
        return optional_aeropuerto.get();
    }

    public List<Aeropuerto> getAll() {
        return aeropuertoRepository.findAll();
    }

    public void delete(Integer id) {
        aeropuertoRepository.deleteById(id);
    }

    public Aeropuerto update(Aeropuerto aeropuerto) {
        return aeropuertoRepository.save(aeropuerto);
    }

    public Aeropuerto findByUbicacion(String idUbicacion) {
        Optional<Ubicacion> optional_ubicacion = ubicacionRepository.findById(idUbicacion);
        return aeropuertoRepository.findByUbicacion(optional_ubicacion.get());
    }
}
