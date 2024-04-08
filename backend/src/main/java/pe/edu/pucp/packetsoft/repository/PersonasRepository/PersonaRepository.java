package pe.edu.pucp.packetsoft.repository.PersonasRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;


@Repository
@Transactional
public interface PersonaRepository extends JpaRepository<Persona, Integer>{
    /*el findPorParamatro se declara porque es option <Entity> lo que devuelve
        entonces debemos decirle qsi devuelve o no y que*/
    Persona findPersonaById(int id);
    public Persona findPersonaByCorreo(String coreo);

    @Query("SELECT p FROM Persona p WHERE p.correo LIKE CONCAT('%', :correo, '%')")
    public List<Persona> encontrarPersonasPorCorreoParcial(String correo);
}
