package pe.edu.pucp.packetsoft.controllers;

import java.sql.SQLException;
import java.util.List;

import pe.edu.pucp.packetsoft.models.Atributo;
import pe.edu.pucp.packetsoft.models.Autoevaluacion;
import pe.edu.pucp.packetsoft.models.Cronograma;
import pe.edu.pucp.packetsoft.models.ProyectoPersona;
import pe.edu.pucp.packetsoft.models.Tabla;
import pe.edu.pucp.packetsoft.models.Valor;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;
import pe.edu.pucp.packetsoft.services.AtributoService;
import pe.edu.pucp.packetsoft.services.AutoevaluacionService;
import pe.edu.pucp.packetsoft.services.CronogramaService;
import pe.edu.pucp.packetsoft.services.ProyectoPersonaService;
import pe.edu.pucp.packetsoft.services.TablaService;
import pe.edu.pucp.packetsoft.services.ValorService;
import pe.edu.pucp.packetsoft.services.PersonasService.PersonaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

@RestController
@RequestMapping("/autoevaluacion")
@CrossOrigin
public class AutoevaluacionController {
    @Autowired
    private AutoevaluacionService autoevaluacionService;

    @Autowired
    private TablaService tablaService;

    @Autowired
    private ProyectoPersonaService proyectoPersonaService;

    @Autowired
    private AtributoService atributoService;

    @Autowired
    private ValorService valorService;

    @Autowired
    private PersonaService personaService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Autoevaluacion> getAll(){
        return autoevaluacionService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Autoevaluacion get(@PathVariable int id){
        return autoevaluacionService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Autoevaluacion register(@RequestBody Autoevaluacion autoevaluacion) throws SQLException{
        Autoevaluacion autoev = autoevaluacionService.register(autoevaluacion);
        if(autoev != null){
            crearTablaDeAutoev(autoev);
        }
        return autoev;
    }


    void crearTablaDeAutoev(Autoevaluacion autoev){
        Tabla tabla = new Tabla();
        tabla.setAutoevaluacion(new Autoevaluacion());
        tabla.getAutoevaluacion().setId(autoev.getId());
        tabla.setEsSeccion(false);
        tabla.setEsTabla(true);
        tabla.setNombre("Tabla de autoevaluación");

        Tabla tablaReg = tablaService.register(tabla);
        if(tablaReg != null){
            int idProyecto = autoev.getProyecto().getId();
            crearAtributosDeAutoev(tablaReg, idProyecto);
        }
    }

    void crearAtributosDeAutoev(Tabla tabla,int idProyecto){
        //proyectoPersonaService
        //calculo proyectopersona
        int nParticipantes;
        List<ProyectoPersona> listaProyectoPersona = proyectoPersonaService.encontrarParticipantesProyectoPersonaByProyecto(idProyecto);
        if(listaProyectoPersona == null) nParticipantes =0;
        else nParticipantes = listaProyectoPersona.size();
        crearAtributoId(tabla, listaProyectoPersona, nParticipantes);
        crearAtributoNombre(tabla, listaProyectoPersona, nParticipantes);
        crearAtributoDominioTecnico(tabla, nParticipantes);
        crearAtributoCompromiso(tabla, nParticipantes);
        crearAtributoComunicacion(tabla, nParticipantes);
        crearAtributoComprension(tabla, nParticipantes);
    }

    void crearAtributoId(Tabla tabla, List<ProyectoPersona> listaProyectoPersona, int nParticipantes){
        Atributo atributo = new Atributo();
        atributo.setTabla(new Tabla());
        atributo.getTabla().setId(tabla.getId());
        atributo.setFactorDeOrden(1);
        atributo.setTipoDeColumna(1); //es entero
        atributo.setSeMuestraEnPantallaPrincipal(false);
        atributo.setNombre("Id");
        atributo.setSeMuestraEnReporte(false);
        atributo.setEsEliminable(false);
        Atributo atributoReg = atributoService.register(atributo);
        if(atributoReg == null) return;
        for(int i=0;i<nParticipantes;i++){
            Valor valor = new Valor();
            valor.setAtributo(new Atributo());
            valor.getAtributo().setId(atributoReg.getId());
            valor.setFactorOrden(i+1);
            valor.setContenido(String.valueOf(listaProyectoPersona.get(i).getPersona().getId()));
            valor.setEsPrimario(false);
            Valor valorReg = valorService.register(valor);
        }
        Valor ultimoValor = new Valor();
        ultimoValor.setAtributo(new Atributo());
        ultimoValor.getAtributo().setId(atributoReg.getId());
        ultimoValor.setFactorOrden(100);
        ultimoValor.setContenido("5");
        ultimoValor.setEsPrimario(false);
        Valor ultimoValorReg = valorService.register(ultimoValor);
    }

    void crearAtributoNombre(Tabla tabla, List<ProyectoPersona> listaProyectoPersona, int nParticipantes){
        Atributo atributo = new Atributo();
        atributo.setTabla(new Tabla());
        atributo.getTabla().setId(tabla.getId());
        atributo.setFactorDeOrden(2);
        atributo.setTipoDeColumna(3); //es texto
        atributo.setSeMuestraEnPantallaPrincipal(true);
        atributo.setNombre("Nombre");
        atributo.setSeMuestraEnReporte(false);
        atributo.setEsEliminable(false);
        Atributo atributoReg = atributoService.register(atributo);
        if(atributoReg == null) return;
        for(int i=0;i<nParticipantes;i++){
            Valor valor = new Valor();
            valor.setAtributo(new Atributo());
            valor.getAtributo().setId(atributoReg.getId());
            valor.setFactorOrden(i+1);
            Persona persona = personaService.get(listaProyectoPersona.get(i).getPersona().getId());
            valor.setContenido(persona.getNombres()+ " "+ persona.getPrimerApellido());
            valor.setEsPrimario(true);
            Valor valorReg = valorService.register(valor);
        }
        Valor ultimoValor = new Valor();
        ultimoValor.setAtributo(new Atributo());
        ultimoValor.getAtributo().setId(atributoReg.getId());
        ultimoValor.setFactorOrden(100);
        ultimoValor.setContenido("5");
        ultimoValor.setEsPrimario(false);
        Valor ultimoValorReg = valorService.register(ultimoValor);
    }

    void crearAtributoDominioTecnico(Tabla tabla, int nParticipantes){
        Atributo atributo = new Atributo();
        atributo.setTabla(new Tabla());
        atributo.getTabla().setId(tabla.getId());
        atributo.setFactorDeOrden(3);
        atributo.setTipoDeColumna(1); //es entero
        atributo.setSeMuestraEnPantallaPrincipal(true);
        atributo.setNombre("Dominio técnico");
        atributo.setSeMuestraEnReporte(false);
        atributo.setEsEliminable(false);
        Atributo atributoReg = atributoService.register(atributo);
        if(atributoReg == null) return;
        for(int i=0;i<nParticipantes;i++){
            Valor valor = new Valor();
            valor.setAtributo(new Atributo());
            valor.getAtributo().setId(atributoReg.getId());
            valor.setFactorOrden(i+1);
            valor.setContenido("0");
            valor.setEsPrimario(true);
            Valor valorReg = valorService.register(valor);
        }
        Valor ultimoValor = new Valor();
        ultimoValor.setAtributo(new Atributo());
        ultimoValor.getAtributo().setId(atributoReg.getId());
        ultimoValor.setFactorOrden(100);
        ultimoValor.setContenido("5");
        ultimoValor.setEsPrimario(false);
        Valor ultimoValorReg = valorService.register(ultimoValor);
    }

    void crearAtributoCompromiso(Tabla tabla, int nParticipantes){
        Atributo atributo = new Atributo();
        atributo.setTabla(new Tabla());
        atributo.getTabla().setId(tabla.getId());
        atributo.setFactorDeOrden(4);
        atributo.setTipoDeColumna(1); //es entero
        atributo.setSeMuestraEnPantallaPrincipal(true);
        atributo.setNombre("Compromiso con los trabajos");
        atributo.setSeMuestraEnReporte(false);
        atributo.setEsEliminable(false);
        Atributo atributoReg = atributoService.register(atributo);
        if(atributoReg == null) return;
        for(int i=0;i<nParticipantes;i++){
            Valor valor = new Valor();
            valor.setAtributo(new Atributo());
            valor.getAtributo().setId(atributoReg.getId());
            valor.setFactorOrden(i+1);
            valor.setContenido("0");
            valor.setEsPrimario(true);
            Valor valorReg = valorService.register(valor);
        }
        Valor ultimoValor = new Valor();
        ultimoValor.setAtributo(new Atributo());
        ultimoValor.getAtributo().setId(atributoReg.getId());
        ultimoValor.setFactorOrden(100);
        ultimoValor.setContenido("5");
        ultimoValor.setEsPrimario(false);
        Valor ultimoValorReg = valorService.register(ultimoValor);
    }

    void crearAtributoComunicacion(Tabla tabla, int nParticipantes){
        Atributo atributo = new Atributo();
        atributo.setTabla(new Tabla());
        atributo.getTabla().setId(tabla.getId());
        atributo.setFactorDeOrden(5);
        atributo.setTipoDeColumna(1); //es entero
        atributo.setSeMuestraEnPantallaPrincipal(true);
        atributo.setNombre("Comunicación con los compañeros");
        atributo.setSeMuestraEnReporte(false);
        atributo.setEsEliminable(false);
        Atributo atributoReg = atributoService.register(atributo);
        if(atributoReg == null) return;
        for(int i=0;i<nParticipantes;i++){
            Valor valor = new Valor();
            valor.setAtributo(new Atributo());
            valor.getAtributo().setId(atributoReg.getId());
            valor.setFactorOrden(i+1);
            valor.setContenido("0");
            valor.setEsPrimario(true);
            Valor valorReg = valorService.register(valor);
        }
        Valor ultimoValor = new Valor();
        ultimoValor.setAtributo(new Atributo());
        ultimoValor.getAtributo().setId(atributoReg.getId());
        ultimoValor.setFactorOrden(100);
        ultimoValor.setContenido("5");
        ultimoValor.setEsPrimario(false);
        Valor ultimoValorReg = valorService.register(ultimoValor);
    }

    void crearAtributoComprension(Tabla tabla, int nParticipantes){
        Atributo atributo = new Atributo();
        atributo.setTabla(new Tabla());
        atributo.getTabla().setId(tabla.getId());
        atributo.setFactorDeOrden(6);
        atributo.setTipoDeColumna(1); //es entero
        atributo.setSeMuestraEnPantallaPrincipal(true);
        atributo.setNombre("Comprensión del proyecto");
        atributo.setSeMuestraEnReporte(false);
        atributo.setEsEliminable(false);
        Atributo atributoReg = atributoService.register(atributo);
        if(atributoReg == null) return;
        for(int i=0;i<nParticipantes;i++){
            Valor valor = new Valor();
            valor.setAtributo(new Atributo());
            valor.getAtributo().setId(atributoReg.getId());
            valor.setFactorOrden(i+1);
            valor.setContenido("0");
            valor.setEsPrimario(true);
            Valor valorReg = valorService.register(valor);
        }
        Valor ultimoValor = new Valor();
        ultimoValor.setAtributo(new Atributo());
        ultimoValor.getAtributo().setId(atributoReg.getId());
        ultimoValor.setFactorOrden(100);
        ultimoValor.setContenido("5");
        ultimoValor.setEsPrimario(false);
        Valor ultimoValorReg = valorService.register(ultimoValor);
    }


    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    Autoevaluacion update(@RequestBody Autoevaluacion autoevaluacion){
        return autoevaluacionService.update(autoevaluacion);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        autoevaluacionService.delete(id);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

    
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarAutoevaluacionPorProyecto/{idProyecto}")
    Autoevaluacion seleccionarAutoevaluacionPorProyecto(@PathVariable int idProyecto){
        return autoevaluacionService.seleccionarAutoevaluacionPorProyecto(idProyecto);
    }

}
