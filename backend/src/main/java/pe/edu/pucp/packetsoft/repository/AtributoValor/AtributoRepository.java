package pe.edu.pucp.packetsoft.repository.AtributoValor;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.pucp.packetsoft.models.Atributo;
import pe.edu.pucp.packetsoft.models.Tabla;

@Repository
@Transactional
public interface AtributoRepository extends JpaRepository<Atributo, Integer>{
    public List<Atributo> findAll();
    public Atributo findAtributoById(int id);
    public List<Atributo> findAtributoByTabla(Tabla tabla);
}
