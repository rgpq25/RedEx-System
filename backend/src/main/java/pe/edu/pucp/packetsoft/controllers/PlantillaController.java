package pe.edu.pucp.packetsoft.controllers;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.pucp.packetsoft.models.Atributo;
import pe.edu.pucp.packetsoft.models.HerramientaXProyecto;
import pe.edu.pucp.packetsoft.models.Plantilla;
import pe.edu.pucp.packetsoft.models.Tabla;
import pe.edu.pucp.packetsoft.services.PlantillaService;


@RestController
@RequestMapping("/plantilla")
@CrossOrigin
public class PlantillaController {
    @Autowired
    private PlantillaService plantillaService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Plantilla> getAll(){
        return plantillaService.getAll();
    }
    
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Plantilla get(@PathVariable int id){
        return plantillaService.getPlantillaById(id);
    }
    
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarPlantillaPorHerramienta/{idHerramienta}")
    List<Plantilla> listarPlantillaPorHerramienta(@PathVariable int idHerramienta){
        return plantillaService.findPlantillaByHerramienta(idHerramienta);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/duplicarPlantilla/{idPlantilla}/{idHerramientaXProyecto}")
    List<Tabla> duplicarPlantilla(@PathVariable int idPlantilla,@PathVariable int idHerramientaXProyecto){
        return plantillaService.duplicarPlantilla(idPlantilla,idHerramientaXProyecto);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Plantilla register(@RequestBody Plantilla plantilla) throws SQLException{  
        Plantilla resultado = plantillaService.register(plantilla);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;  
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    Plantilla update(@RequestBody Plantilla plantilla) throws SQLException{
        //System.out.println(valor.getAtributo().getId());
        Plantilla resultado = plantillaService.update(plantilla);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        plantillaService.delete(id);
    }

}
