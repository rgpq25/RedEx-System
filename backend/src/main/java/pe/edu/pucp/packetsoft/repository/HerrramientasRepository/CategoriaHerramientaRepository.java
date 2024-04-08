package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;

import pe.edu.pucp.packetsoft.models.CategoriaHerramienta;

@Repository
@Transactional
public interface CategoriaHerramientaRepository extends JpaRepository<CategoriaHerramienta, Integer> {
    public List<CategoriaHerramienta> findAll();
    public CategoriaHerramienta findCategoriaHerramientaById(int id);
}
