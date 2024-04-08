package pe.edu.pucp.packetsoft.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import pe.edu.pucp.packetsoft.models.Atributo;
import pe.edu.pucp.packetsoft.models.Tabla;
import pe.edu.pucp.packetsoft.models.Valor;
import pe.edu.pucp.packetsoft.services.AtributoService;
import pe.edu.pucp.packetsoft.services.TablaService;
import pe.edu.pucp.packetsoft.services.ValorService;

@RestController
@RequestMapping("/tabla")
@CrossOrigin
public class TablaController {

    @Autowired
    private TablaService tablaService;

    @Autowired
    private AtributoService atributoService;

    @Autowired ValorService valorService;

    //Trae todas los tablas
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Tabla> getAll(){
        return tablaService.getAll();
    }

    //Trae una tabla
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Tabla get(@PathVariable int id){
        return tablaService.get(id);
    }

    //Registra una tabla
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Tabla register(@RequestBody Tabla tabla) throws SQLException{
        if(tablaService.duplicadoPropio(tabla.getId())!=null){
            System.out.println("ERROR: Id de tabla duplicado");
            throw new SQLException();
        }      
        Tabla resultado = tablaService.register(tabla);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;
    }

    //Actualiza una tabla
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    Tabla update(@RequestBody Tabla data)throws SQLException{
        return tablaService.update(data);
    }
    
    //Eliminar una tabla con id dado
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        tablaService.delete(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarTablasPorPlantilla/{idPlantilla}")
    List<Tabla> listarTablasPorPlantilla(@PathVariable int idPlantilla){
        return tablaService.listarTablasPorPlantilla(idPlantilla);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarTablasPorPlanDeCalidad/{idPlanDeCalidad}")
    List<Tabla> listarTablasPorPlanDeCalidad(@PathVariable int idPlanDeCalidad){
        return tablaService.listarTablasPorPlanDeCalidad(idPlanDeCalidad);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarTablasPorActaDeConstitucion/{idActaDeConstitucion}")
    List<Tabla> listarTablasPorActaDeConstitucion(@PathVariable int idActaDeConstitucion){
        return tablaService.listarTablasPorActaDeConstitucion(idActaDeConstitucion);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarTablaPorCronograma/{idCronograma}")
    Tabla listarTablasPorCronograma(@PathVariable int idCronograma){
        return tablaService.listarTablasPorCronograma(idCronograma);
    }
 
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarTablaPorEDT/{idEDT}")
    Tabla listarTablasPorEDT(@PathVariable int idEDT){
        return tablaService.listarTablasPorEDT(idEDT);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarTablaPorProductBacklog/{idProductBacklog}")
    Tabla listarTablasPorProductBacklog(@PathVariable int idProductBacklog){
        return tablaService.listarTablasPorProductBacklog(idProductBacklog);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarTablaPorPresupuesto/{idPresupuesto}")
    Tabla listarTablasPorPresupuesto(@PathVariable int idPresupuesto){
        return tablaService.listarTablasPorPresupuesto(idPresupuesto);
    }   
    
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarTablaPorCatalogoDeRiesgos/{idCatalogoDeRiesgos}")
    Tabla listarTablasPorCatalogoDeRiesgos(@PathVariable int idCatalogoDeRiesgos){
        return tablaService.listarTablasPorCatalogoDeRiesgo(idCatalogoDeRiesgos);
    } 

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe") 
    @GetMapping(value = "/seleccionarTablaPorMatrizDeResponsabilidades/{idMatrizDeResponsabilidades}")
    Tabla seleccionarTablaPorMatrizDeResponsabilidades(@PathVariable int idMatrizDeResponsabilidades){
        return tablaService.listarTablasPorMatrizDeResponsabilidades(idMatrizDeResponsabilidades);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe") 
    @GetMapping(value = "/seleccionarTablaPorMatrizDeComunicaciones/{idMatrizDeComunicaciones}")
    Tabla seleccionarTablaPorMatrizDeComunicaciones(@PathVariable int idMatrizDeComunicaciones){
        return tablaService.listarTablasPorMatrizDeComunicaciones(idMatrizDeComunicaciones);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe") 
    @GetMapping(value = "/seleccionarTablasPorTableroKanban/{idTableroKanban}")
    List<Tabla> seleccionarTablasPorTableroKanban(@PathVariable int idTableroKanban){
        return tablaService.listarTablasPorTableroKanban(idTableroKanban);
    }    

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarTablaPorCatalogoDeInteresados/{idCatalogoDeInteresados}")
    Tabla seleccionarTablaPorCatalogoDeInteresados(@PathVariable int idCatalogoDeInteresados){
        return tablaService.listarTablasPorCatalogoDeInteresados(idCatalogoDeInteresados);
    }


    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarTablaPorAutoevaluacion/{idAutoevaluacion}")
    Tabla seleccionarTablaPorAutoevaluacion(@PathVariable int idAutoevaluacion){
        return tablaService.listarTablasPorAutoevaluacion(idAutoevaluacion);
    }
}
