package pe.edu.pucp.packetsoft.repository.AtributoValor;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.pucp.packetsoft.models.Etiqueta;

@Repository
@Transactional
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Integer> {
    public List<Etiqueta> findAll();
    public Etiqueta findTagById(int id);
}