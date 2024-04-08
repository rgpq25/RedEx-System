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

import pe.edu.pucp.packetsoft.models.EtiquetaAtributo;
import pe.edu.pucp.packetsoft.services.EtiquetaAtributoService;

@RestController
@RequestMapping("/etiquetaAtributo")
@CrossOrigin
public class EtiquetaAtributoController {
    @Autowired
    private EtiquetaAtributoService etiquetaAtributoService;

    //Trae todas los etiquetas
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<EtiquetaAtributo> getAll(){
        return etiquetaAtributoService.getAll();
    }

    //Trae una etiqueta
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    EtiquetaAtributo get(@PathVariable int id){
        return etiquetaAtributoService.get(id);
    }

    //Registra una etiqueta
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    EtiquetaAtributo register(@RequestBody EtiquetaAtributo reg) throws SQLException{
            
        EtiquetaAtributo resultado = etiquetaAtributoService.register(reg);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;  
    }

    //Actualiza una etiqueta
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    EtiquetaAtributo update(@RequestBody EtiquetaAtributo data)throws SQLException{
        return etiquetaAtributoService.update(data);
    }
    
    //Eliminar una etiqueta con id dado
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        etiquetaAtributoService.delete(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarEtiquetaAtributoPorEtiqueta/{idEtiqueta}")
    List<EtiquetaAtributo> listarEtiquetaAtributoPorEtiqueta(@PathVariable int idEtiqueta){
        return etiquetaAtributoService.listarEtiquetasAtributosPorEtiqueta(idEtiqueta);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarEtiquetaAtributoPorAtributo/{idAtributo}")
    List<EtiquetaAtributo> listarEtiquetaAtributoPorAtributo(@PathVariable int idAtributo){
        return etiquetaAtributoService.listarEtiquetasAtributosPorAtributo(idAtributo);
    }
    
}
