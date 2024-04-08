package pe.edu.pucp.packetsoft.controllers.PersonasController;

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

import pe.edu.pucp.packetsoft.models.PersonasModel.Permiso;
import pe.edu.pucp.packetsoft.services.PersonasService.PermisoService;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/permiso")
@CrossOrigin
public class PermisoController {
    @Autowired
    private PermisoService permisoService;

    //Trae todas los permisos
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Permiso> getAll(){
        return permisoService.getAll();
    }

    //Trae a un permiso
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Permiso get(@PathVariable int id){
        return permisoService.get(id);
    }

    //Registra un permiso
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Permiso register(@RequestBody Permiso permiso) throws SQLException{
        return permisoService.register(permiso);
    }

    //Actualiza un permiso
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value ="/update")
    public Permiso update(@RequestBody Permiso permiso){
        return permisoService.update(permiso);
    }

    //Borra a un permiso
    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        permisoService.delete(id);
    }


}
