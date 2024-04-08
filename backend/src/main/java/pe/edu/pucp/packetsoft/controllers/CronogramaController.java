package pe.edu.pucp.packetsoft.controllers;

import java.sql.SQLException;
import java.util.List;

import pe.edu.pucp.packetsoft.models.Cronograma;
import pe.edu.pucp.packetsoft.services.CronogramaService;
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
@RequestMapping("/cronograma")
@CrossOrigin
public class CronogramaController {
    @Autowired
    private CronogramaService cronogramaService;

    //Trae todos los cronogramas
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Cronograma> getAll(){
        /*List<Usuario> usuarios =new ArrayList<>();
        usuarios.add(new Usuario(null, null, null, 0));
        return usuarios;*/
        return cronogramaService.getAll();
    }

    //Trae a un cronograma
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Cronograma get(@PathVariable int id){
        return cronogramaService.get(id);
    }

    //Registra un cronograma
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Cronograma register(@RequestBody Cronograma cronograma) throws SQLException{
        
        if(cronogramaService.duplicadoPropio(cronograma.getId())!=null){
            throw new SQLException();
        }
        return cronogramaService.register(cronograma);
    }

    //Actualiza un cronograma
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    Cronograma update(@RequestBody Cronograma cronograma) throws SQLException{
        return cronogramaService.update(cronograma);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        cronogramaService.delete(id);
    }

    //Excepcion para data duplicada
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated data")
    public void conflict() {}

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/seleccionarCronogramaPorHerramientaXProyecto/{idHerramientaXProyecto}")
    Cronograma seleccionarCronogramaPorHerramientaXProyecto(@PathVariable int idHerramientaXProyecto){
        return cronogramaService.seleccionarCronogramaPorHerramientaXProyecto(idHerramientaXProyecto);
    }
}
