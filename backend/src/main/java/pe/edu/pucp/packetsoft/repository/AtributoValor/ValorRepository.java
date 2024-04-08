package pe.edu.pucp.packetsoft.repository.AtributoValor;
import pe.edu.pucp.packetsoft.models.Atributo;
import pe.edu.pucp.packetsoft.models.Valor;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
@Transactional
public interface ValorRepository extends JpaRepository<Valor, Integer>{
    public List<Valor> findAll();
    public Valor findValorById(int id);
    public List<Valor> findValorByAtributo(Atributo atributo);
}
