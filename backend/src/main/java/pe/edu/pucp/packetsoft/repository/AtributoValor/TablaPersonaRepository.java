package pe.edu.pucp.packetsoft.repository.AtributoValor;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.pucp.packetsoft.models.TablaPersona;

@Repository
@Transactional
public interface TablaPersonaRepository extends JpaRepository<TablaPersona, Integer>{
    public List<TablaPersona> findAll();
    public TablaPersona findRegById(int id);
}
