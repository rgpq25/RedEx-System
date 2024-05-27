package pucp.e3c.redex_back.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.e3c.redex_back.model.Envio;
import pucp.e3c.redex_back.model.Funciones;
import pucp.e3c.redex_back.model.Paquete;
import pucp.e3c.redex_back.model.RegistrarEnvio;
import pucp.e3c.redex_back.model.Simulacion;
import pucp.e3c.redex_back.model.Ubicacion;
import pucp.e3c.redex_back.service.AeropuertoService;
import pucp.e3c.redex_back.service.EnvioService;
import pucp.e3c.redex_back.service.PaqueteService;
import pucp.e3c.redex_back.service.SimulacionService;
import pucp.e3c.redex_back.service.UbicacionService;

@RestController
@RequestMapping("back/envio")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private AeropuertoService aeropuertoService;

    @Autowired
    private PaqueteService paqueteService;

    @Autowired
    private SimulacionService simulacionService;

    @PostMapping(value = "/")
    public Envio register(@RequestBody Envio envio) {
        return envioService.register(envio);
    }

    @PostMapping(value = "/codigo")
    public Envio registerByString(@RequestBody RegistrarEnvio registrarEnvio) {
        return envioService.registerByString(registrarEnvio);
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

    @GetMapping(value = "/getPaquetesDelEvioNoSimulacion/{id}")
    public ArrayList<Paquete> getPaquetesDelEvioNoSimulacion(@PathVariable("id") Integer id) {
        ArrayList<Paquete> paquetes = (ArrayList<Paquete>) paqueteService.findByEnvioId(id);
        for (Paquete paquete : paquetes) {
            paquete = paqueteService.getPaqueteNoSimulacion(paquete.getId());
        }
        return paquetes;
    }

}
