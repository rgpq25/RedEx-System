package pe.edu.pucp.packetsoft.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

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

import pe.edu.pucp.packetsoft.models.Valor;
import pe.edu.pucp.packetsoft.services.ValorService;

@RestController
@RequestMapping("/valor")
@CrossOrigin
public class ValorController {
    
    @Autowired
    private ValorService valorService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Valor> getAll(){
        return valorService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Valor get(@PathVariable int id){
        return valorService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Valor register(@RequestBody Valor valor) throws SQLException{  
        Valor resultado = valorService.register(valor);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;  
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    Valor update(@RequestBody Valor valor) throws SQLException{
        //System.out.println(valor.getAtributo().getId());
        Valor resultado = valorService.update(valor);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        valorService.delete(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarValoresPorAtributo/{idAtributo}")
    List<Valor> listarValoresPorAtributo(@PathVariable int idAtributo){
        return valorService.findValorByAtributo(idAtributo);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/listarValoresPorAtributoOrdernarPorFactor/{idAtributo}")
    List<Valor> listarValoresPorAtributoOrdernarPorFactor(@PathVariable int idAtributo){
        List<Valor> valores = new ArrayList<>();
        valores = valorService.findValorByAtributo(idAtributo);
        if(valores == null) return null;
        Collections.sort(valores, new Comparator<Valor>(){
            @Override
            public int compare(Valor v1, Valor v2){
                return Double.compare(v1.getFactorOrden(), v2.getFactorOrden());
            }
        });
        return valores;
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/registrarActualizarValores/")
    List <Valor> registrarActualizarValores(@RequestBody List<Valor> valores) throws SQLException{
        for (Valor valor : valores) {
            if(valor.getId() > 0){
                //Se va a actualizar el valor
                Valor valorActualizado = valorService.update(valor);
                if(valorActualizado == null){
                    throw new SQLException();
                }
            }
            else{
                //Se va a registrar el valor
                Valor valorRegistrado = valorService.register(valor);
                if(valorRegistrado == null){
                    throw new SQLException();
                }
                valor.setId(valorRegistrado.getId());
            }
        }
        return valores;
    }



}
