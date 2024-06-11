package pucp.e3c.redex_back.service;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Env;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pucp.e3c.redex_back.model.Aeropuerto;
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

    public ArrayList<Envio> registerAll(ArrayList<Envio> envios) {
        try {
            return (ArrayList<Envio>) envioRepository.saveAll(envios);
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
                registrarEnvio.getSimulacion(),
                aeropuertoRepository);
        envio.setSimulacionActual(simulacion);
        Envio auxEnvio = envioRepository.save(envio);
        for (int i = 0; i < auxEnvio.getCantidadPaquetes(); i++) {
            Paquete paquete = new Paquete();
            paquete.setAeropuertoActual(aeropuertoRepository.findByUbicacionId(envio.getUbicacionOrigen().getId()));
            paquete.setEnAeropuerto(true);
            paquete.setEntregado(false);
            paquete.setEnvio(envio);
            paquete.setSimulacionActual(simulacion);
            paqueteRepository.save(paquete);
        }
        return auxEnvio;
    }

    public ArrayList<Envio> registerAllByStringEsp(ArrayList<RegistrarEnvio> registrarEnvios,
            HashMap<String, Aeropuerto> hashAeropuertos, Date fechaInicio, Date fechaFin, int cantidad) {
        List<Ubicacion> ubicaciones = ubicacionRepository.findAll();
        HashMap<String, Ubicacion> ubicacionMap = new HashMap<String, Ubicacion>();
        for (Ubicacion u : ubicaciones) {
            ubicacionMap.put(u.getId(), u);
        }
        ArrayList<Envio> envios = new ArrayList<>();
        int aux = 0;
        for (RegistrarEnvio registrarEnvio : registrarEnvios) {
            if (aux >= cantidad) {
                break;
            }

            String clave = registrarEnvio.getCodigo();

            clave = Funciones.asignarFechaAClave(clave, fechaInicio, fechaFin);
            registrarEnvio.setCodigo(clave);

            Envio envio = Funciones.stringToEnvio(registrarEnvio.getCodigo(), ubicacionMap,
                    registrarEnvio.getSimulacion(),
                    aeropuertoRepository);

            envio.setSimulacionActual(registrarEnvio.getSimulacion());
            envios.add(envio);
            aux++;
        }
        envios = (ArrayList<Envio>) envioRepository.saveAll(envios);

        ArrayList<Paquete> paquetes = new ArrayList<>();

        for (Envio envio : envios) {
            for (int i = 0; i < envio.getCantidadPaquetes(); i++) {
                Paquete paquete = new Paquete();
                paquete.setAeropuertoActual(hashAeropuertos.get(envio.getUbicacionOrigen().getId()));
                paquete.setEnAeropuerto(true);
                paquete.setEntregado(false);
                paquete.setEnvio(envio);
                paquete.setSimulacionActual(envio.getSimulacionActual());
                paquetes.add(paquete);
                //LOGGER.info("PAQUETE A GUARDAR: " + paquete.toString());
            }

        }

        paqueteRepository.saveAll(paquetes);

        return envios;
    }

    public ArrayList<Envio> registerAllByStringEspInicioFijo(ArrayList<RegistrarEnvio> registrarEnvios,
            HashMap<String, Aeropuerto> hashAeropuertos, Date fechaInicio, Date fechaFin, int cantidad) {
        List<Ubicacion> ubicaciones = ubicacionRepository.findAll();
        HashMap<String, Ubicacion> ubicacionMap = new HashMap<String, Ubicacion>();
        for (Ubicacion u : ubicaciones) {
            ubicacionMap.put(u.getId(), u);
        }
        ArrayList<Envio> envios = new ArrayList<>();
        int aux = 0;
        for (RegistrarEnvio registrarEnvio : registrarEnvios) {
            if (aux >= cantidad) {
                break;
            }

            String clave = registrarEnvio.getCodigo();

            clave = Funciones.asignarFechaAClave(clave, fechaInicio, fechaFin);
            registrarEnvio.setCodigo(clave);

            Envio envio = Funciones.stringToEnvioInicioFijo(registrarEnvio.getCodigo(), ubicacionMap,
                    registrarEnvio.getSimulacion(),
                    aeropuertoRepository,fechaInicio);

            envio.setSimulacionActual(registrarEnvio.getSimulacion());
            envios.add(envio);
            aux++;
        }
        envios = (ArrayList<Envio>) envioRepository.saveAll(envios);

        ArrayList<Paquete> paquetes = new ArrayList<>();

        for (Envio envio : envios) {
            for (int i = 0; i < envio.getCantidadPaquetes(); i++) {
                Paquete paquete = new Paquete();
                paquete.setAeropuertoActual(hashAeropuertos.get(envio.getUbicacionOrigen().getId()));
                paquete.setEnAeropuerto(true);
                paquete.setEntregado(false);
                paquete.setEnvio(envio);
                paquete.setSimulacionActual(envio.getSimulacionActual());
                paquetes.add(paquete);
                //LOGGER.info("PAQUETE A GUARDAR: " + paquete.toString());
            }

        }

        paqueteRepository.saveAll(paquetes);

        return envios;
    }

    public ArrayList<Envio> registerAllEnviosByString(ArrayList<String> enviosString,
            HashMap<String, Aeropuerto> hashAeropuertos) {
        List<Ubicacion> ubicaciones = ubicacionRepository.findAll();
        HashMap<String, Ubicacion> ubicacionMap = new HashMap<String, Ubicacion>();
        for (Ubicacion u : ubicaciones) {
            ubicacionMap.put(u.getId(), u);
        }
        ArrayList<Envio> envios = new ArrayList<>();
        for (String envioString : enviosString) {

            Envio envio = Funciones.stringToEnvio(envioString, ubicacionMap,
                    null,
                    aeropuertoRepository);

            envios.add(envio);
        }
        envios = (ArrayList<Envio>) envioRepository.saveAll(envios);

        ArrayList<Paquete> paquetes = new ArrayList<>();

        for (Envio envio : envios) {
            for (int i = 0; i < envio.getCantidadPaquetes(); i++) {
                Paquete paquete = new Paquete();
                paquete.setAeropuertoActual(hashAeropuertos.get(envio.getUbicacionOrigen().getId()));
                paquete.setEnAeropuerto(true);
                paquete.setEntregado(false);
                paquete.setEnvio(envio);
                paquete.setSimulacionActual(envio.getSimulacionActual());
                paquetes.add(paquete);
            }

        }

        paqueteRepository.saveAll(paquetes);

        return envios;
    }

    public ArrayList<Envio> registerAllByString(ArrayList<RegistrarEnvio> registrarEnvios,
            HashMap<String, Aeropuerto> hashAeropuertos) {
        List<Ubicacion> ubicaciones = ubicacionRepository.findAll();
        HashMap<String, Ubicacion> ubicacionMap = new HashMap<String, Ubicacion>();
        for (Ubicacion u : ubicaciones) {
            ubicacionMap.put(u.getId(), u);
        }

        ArrayList<Envio> envios = new ArrayList<>();
        for (RegistrarEnvio registrarEnvio : registrarEnvios) {

            Envio envio = Funciones.stringToEnvio(registrarEnvio.getCodigo(), ubicacionMap,
                    registrarEnvio.getSimulacion(),
                    aeropuertoRepository);

            envio.setSimulacionActual(registrarEnvio.getSimulacion());
            envios.add(envio);
        }
        envios = (ArrayList<Envio>) envioRepository.saveAll(envios);

        ArrayList<Paquete> paquetes = new ArrayList<>();

        for (Envio envio : envios) {
            Paquete paquete = new Paquete();
            paquete.setAeropuertoActual(hashAeropuertos.get(envio.getUbicacionOrigen().getId()));
            paquete.setEnAeropuerto(true);
            paquete.setEntregado(false);
            paquete.setEnvio(envio);
            paquete.setSimulacionActual(envio.getSimulacionActual());
            paquetes.add(paquete);
        }

        paqueteRepository.saveAll(paquetes);

        return envios;
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

    public void deleteAll() {
        try {
            envioRepository.deleteAll();
            envioRepository.flush();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
