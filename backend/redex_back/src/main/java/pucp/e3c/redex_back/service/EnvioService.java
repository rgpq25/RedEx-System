package pucp.e3c.redex_back.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pucp.e3c.redex_back.model.Envio;
import pucp.e3c.redex_back.model.Funciones;
import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.RegistrarEnvio;
import pucp.e3c.redex_back.model.Simulacion;
import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.repository.AeropuertoRepository;
import pucp.e3c.redex_back.repository.EnvioRepository;
import pucp.e3c.redex_back.repository.PaqueteRepository;
import pucp.e3c.redex_back.repository.SimulacionRepository;
import pucp.e3c.redex_back.repository.UbicacionRepository;

@Service
public class EnvioService {
    @Autowired
    private EnvioRepository envioRepository; // Inyecta la dependencia

    @Autowired
    UbicacionRepository ubicacionRepository;

    @Autowired
    AeropuertoRepository aeropuertoRepository;

    @Autowired
    SimulacionRepository simulacionRepository;

    @Autowired
    PaqueteRepository paqueteRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvioService.class);

    public Envio register(Envio envio) {
        // return envioRepository.save(envio);
        try {
            return envioRepository.save(envio);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Envio get(Integer id) {
        // Optional<Envio> optional_envio = envioRepository.findById(id);
        // return optional_envio.get();
        try {
            Optional<Envio> optional_envio = envioRepository.findById(id);
            return optional_envio.get();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Envio> getAll() {
        // return envioRepository.findAll();
        try {
            return envioRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(Integer id) {
        // envioRepository.deleteById(id);
        try {
            envioRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Envio update(Envio envio) {
        // return envioRepository.save(envio);
        try {
            return envioRepository.save(envio);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;

        }
    }

    public Envio registerByString(RegistrarEnvio registrarEnvio) {
        List<Ubicacion> ubicaciones = ubicacionRepository.findAll();
        HashMap<String, Ubicacion> ubicacionMap = new HashMap<String, Ubicacion>();
        for (Ubicacion u : ubicaciones) {
            ubicacionMap.put(u.getId(), u);
        }
        Simulacion simulacion = simulacionRepository.findById(registrarEnvio.getSimulacion().getId()).get();

        Envio envio = Funciones.stringToEnvio(registrarEnvio.getCodigo(), ubicacionMap,
                registrarEnvio.getSimulacion().getId(),
                aeropuertoRepository);
        Envio auxEnvio = envioRepository.save(envio);
        for (int i = 0; i < auxEnvio.getCantidadPaquetes(); i++) {
            Paquete paquete = new Paquete();
            paquete.setAeropuertoActual(aeropuertoRepository.findByUbicacionId(envio.getUbicacionOrigen().getId()));
            paquete.setEnAeropuerto(true);
            paquete.setEntregado(false);
            paquete.setEnvio(envio);
            paquete.setSimulacionActual(simulacion);
            Paquete _paquete = paqueteRepository.save(paquete);
        }
        auxEnvio.setSimulacionActual(simulacion);
        return auxEnvio;
    }

    public Envio findByCodigo_seguridad(String codigoSeguridad) {
        // return envioRepository.findByCodigoSeguridad(codigoSeguridad);
        try {
            return envioRepository.findByCodigoSeguridad(codigoSeguridad);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Envio> findBySimulacionActualID(Integer id) {
        try {
            return envioRepository.findBySimulacionActualId(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Envio> findSinSimulacionActual() {
        try {
            return envioRepository.findEnviosSinSimulacion();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

}
