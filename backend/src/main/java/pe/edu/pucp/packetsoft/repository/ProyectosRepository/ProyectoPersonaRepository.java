package pe.edu.pucp.packetsoft.repository.ProyectosRepository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.pucp.packetsoft.models.Proyecto;
import pe.edu.pucp.packetsoft.models.ProyectoPersona;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;

import java.util.List;

@Repository
@Transactional
public interface ProyectoPersonaRepository extends JpaRepository<ProyectoPersona, Integer>{
    public List<ProyectoPersona> findAll();
    public ProyectoPersona findProyectoPersonaById(int id);
    //private Proyecto proyecto;
    public List<ProyectoPersona> findProyectoPersonaByProyecto(Proyecto proyecto);

    //private Persona persona;
    public List<ProyectoPersona> findProyectoPersonaByPersona(Persona persona);
}
