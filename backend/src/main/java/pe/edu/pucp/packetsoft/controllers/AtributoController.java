package pe.edu.pucp.packetsoft.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.pucp.packetsoft.models.Atributo;
import pe.edu.pucp.packetsoft.models.Valor;
import pe.edu.pucp.packetsoft.services.AtributoService;

@RestController
@RequestMapping("/atributo")
@CrossOrigin
public class AtributoController {

    @Autowired
    private AtributoService atributoService;

    //Trae todas los atributos
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Atributo> getAll(){
        return atributoService.getAll();
    }

    //Trae un atributo
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Atributo get(@PathVariable int id){
        return atributoService.get(id);
    }

    //Registra una atributo
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Atributo register(@RequestBody Atributo atributo) throws SQLException{
        
        Atributo resultado = atributoService.register(atributo);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;
    }

    //Actualiza una atributo (se puede utilizar PATCH es más eficiente)
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    Atributo update(@RequestBody Atributo data){
        return atributoService.update(data);
    }
    
    //Eliminar un atributo con id dado
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        atributoService.delete(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarAtributosPorTabla/{idTabla}")
    List<Atributo> listarAtributosPorTabla(@PathVariable int idTabla){
        return atributoService.findAtributoByTabla(idTabla);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarAtributosPorTablaOrdenarPorFactor/{idTabla}")
    List<Atributo> listarAtributosPorTablaOrdenarPorFactor(@PathVariable int idTabla){
        List<Atributo> atributos = new ArrayList<>();
        atributos = atributoService.findAtributoByTabla(idTabla);
        Collections.sort(atributos, new Comparator<Atributo>(){
            @Override
            public int compare(Atributo a1, Atributo a2) {
                return Double.compare(a1.getFactorDeOrden(), a2.getFactorDeOrden());
            }
        });
        return atributos;
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/registrarActualizarAtributos/")
    List <Atributo> registrarActualizarAtributos(@RequestBody List<Atributo> atributos) throws SQLException{
        for (Atributo atributo : atributos) {
            if(atributo.getId() > 0){
                //Se va a actualizar el atributo
                Atributo atributoActualizado = atributoService.update(atributo);
                if(atributoActualizado == null){
                    throw new SQLException();
                }
            }
            else{
                //Se va a registrar el atributo
                Atributo atributoRegistrado = atributoService.register(atributo);
                if(atributoRegistrado == null){
                    throw new SQLException();
                }
                atributo.setId(atributoRegistrado.getId());
            }
        }
        return atributos;
    }

}
