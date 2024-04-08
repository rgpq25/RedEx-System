package pe.edu.pucp.packetsoft.repository.AtributoValor;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.pucp.packetsoft.models.Atributo;
import pe.edu.pucp.packetsoft.models.Etiqueta;
import pe.edu.pucp.packetsoft.models.EtiquetaAtributo;

@Repository
@Transactional
public interface EtiquetaAtributoRepository extends JpaRepository<EtiquetaAtributo, Integer>{
    public List<EtiquetaAtributo> findAll();
    public EtiquetaAtributo findRegisterById(int id);
    //private Etiqueta etiqueta;
    //private Atributo atributo;
    public List<EtiquetaAtributo> findEtiquetaAtributoByEtiqueta(Etiqueta etiqueta);
    public List<EtiquetaAtributo> findEtiquetaAtributoByAtributo(Atributo atributo);    
}
