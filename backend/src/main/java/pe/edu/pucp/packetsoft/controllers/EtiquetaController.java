package pe.edu.pucp.packetsoft.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
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

import pe.edu.pucp.packetsoft.models.Etiqueta;
import pe.edu.pucp.packetsoft.models.EtiquetaAtributo;
import pe.edu.pucp.packetsoft.services.EtiquetaAtributoService;
import pe.edu.pucp.packetsoft.services.EtiquetaService;

@RestController
@RequestMapping("/etiqueta")
@CrossOrigin
public class EtiquetaController {
    @Autowired
    private EtiquetaService etiquetaService;

    @Autowired
    private EtiquetaAtributoService etiquetaAtributoService;

    //Trae todas los etiquetas
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Etiqueta> getAll(){
        return etiquetaService.getAll();
    }

    //Trae una etiqueta
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Etiqueta get(@PathVariable int id){
        return etiquetaService.get(id);
    }

    //Registra una etiqueta
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Etiqueta register(@RequestBody Etiqueta etiqueta) throws SQLException{
            
        Etiqueta resultado = etiquetaService.register(etiqueta);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;  
    }

    //Actualiza una etiqueta
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    Etiqueta update(@RequestBody Etiqueta data)throws SQLException{
        return etiquetaService.update(data);
    }
    
    //Eliminar una etiqueta con id dado
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        etiquetaService.delete(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarEtiquetasPorAtributo/{idAtributo}")
    List<Etiqueta> listarEtiquetasPorAtributo(@PathVariable int idAtributo){
        List<Etiqueta> etiquetas = new ArrayList<>();
        List<EtiquetaAtributo> etiquetasAtributos = etiquetaAtributoService.listarEtiquetasAtributosPorAtributo(idAtributo);
        if(etiquetasAtributos == null) return null;
        for (EtiquetaAtributo etiquetaAtributo : etiquetasAtributos) {
            Etiqueta etiqueta = etiquetaService.get(etiquetaAtributo.getEtiqueta().getId());
            etiquetas.add(etiqueta);
        }
        return etiquetas;
        //return etiquetaService.listarEtiquetasPorAtributo(idAtributo);
    }
    
}
