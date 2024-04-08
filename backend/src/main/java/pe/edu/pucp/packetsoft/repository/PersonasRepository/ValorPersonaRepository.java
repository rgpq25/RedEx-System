package pe.edu.pucp.packetsoft.repository.PersonasRepository;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pe.edu.pucp.packetsoft.models.Valor;
import pe.edu.pucp.packetsoft.models.PersonasModel.ValorPersona;

@Repository
@Transactional
public interface ValorPersonaRepository extends JpaRepository<ValorPersona, Integer>{
    public List<ValorPersona> findAll();
    public ValorPersona findValorPersonaById(int id);    
    public List<ValorPersona> findValorPersonaByValor(Valor valor);
}
