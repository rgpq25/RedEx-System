package pucp.e3c.redex_back.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.model.Aeropuerto;
import pucp.e3c.redex_back.model.Envio;
import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.RegistrarEnvio;
import pucp.e3c.redex_back.service.AeropuertoService;
import pucp.e3c.redex_back.service.EnvioService;

@RestController
@CrossOrigin(origins = "https://inf226-981-3c.inf.pucp.edu.pe")
@RequestMapping("back/envio")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @Autowired
    private AeropuertoService aeropuertoService;

    @PostMapping(value = "/")
    public Envio register(@RequestBody Envio envio) {
        envio = envioService.register(envio);
        for (int i = 0; i < envio.getCantidadPaquetes(); i++) {
            Paquete paquete = new Paquete();
            paquete.setEnvio(envio);
        }
        return envioService.register(envio);
    }

    @PostMapping(value = "/codigo")
    public Envio registerByString(@RequestBody RegistrarEnvio registrarEnvio) {
        return envioService.registerByString(registrarEnvio);
    }

    @PostMapping("/codigoAll")
    public ResponseEntity<ArrayList<Envio>> registrarEnvios(@RequestBody ArrayList<String> enviosString) {
        ArrayList<Aeropuerto> aeropuertos = (ArrayList<Aeropuerto>) aeropuertoService.getAll();

        HashMap<String, Aeropuerto> aeropuertoMap = new HashMap<>();
        for (Aeropuerto aeropuerto : aeropuertos) {
            aeropuertoMap.put(aeropuerto.getUbicacion().getId(), aeropuerto);
        }

        ArrayList<Envio> envios = envioService.registerAllEnviosByString(enviosString, aeropuertoMap);

        int totalPaquetes = envios.stream()
                .mapToInt(envio -> envio.getCantidadPaquetes())
                .sum();
        System.out.println("Se generaron " + totalPaquetes + " paquetes.");

        return new ResponseEntity<>(envios, HttpStatus.CREATED);
    }

    @PutMapping(value = "/")
    public Envio update(@RequestBody Envio envio) {
        return envioService.update(envio);
    }

    @GetMapping(value = "/")
    public List<Envio> getAll() {
        return envioService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Envio get(@PathVariable("id") Integer id) {
        return envioService.get(id);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id") Integer id) {
        envioService.delete(id);
    }

    @GetMapping(value = "/codigo_seguridad/{codigoSeguridad}")
    public Envio findByCodigo_seguridad(@PathVariable("codigoSeguridad") String codigoSeguridad) {
        return envioService.findByCodigo_seguridad(codigoSeguridad);
    }

    @GetMapping(value = "/simulacion/{id}")
    public ArrayList<Envio> findByCodigo_seguridad(@PathVariable("id") Integer id) {
        return envioService.findBySimulacionActualID(id);
    }

    @GetMapping(value = "/sin_simulacion")
    public ArrayList<Envio> findSinSimuacionActual() {
        return envioService.findSinSimulacionActual();
    }

    @GetMapping(value = "/emisor/{id}")
    public ArrayList<Envio> findByEmisorID(@PathVariable("id") Integer id) {
        return envioService.findByEmisorId(id);
    }

    @GetMapping(value = "/receptor/{id}")
    public ArrayList<Envio> findByReceptorID(@PathVariable("id") Integer id) {
        return envioService.findByReceptorId(id);
    }

}
