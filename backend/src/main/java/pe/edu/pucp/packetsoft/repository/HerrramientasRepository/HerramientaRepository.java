package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import io.lettuce.core.dynamic.annotation.Param;
import pe.edu.pucp.packetsoft.models.CategoriaHerramienta;
import pe.edu.pucp.packetsoft.models.Herramienta;

import java.util.List;

@Repository
@Transactional
public interface HerramientaRepository extends JpaRepository<Herramienta, Integer>{
    public List<Herramienta> findAll();
    public Herramienta findHerramientaById(int id);
    public List<Herramienta> findHerramientaByCategoriaHerramienta(CategoriaHerramienta categoriaHerramienta);

    @Query("FROM Herramienta a WHERE a.nombre = :nombre")
    public Herramienta EncontrarIdxHerramienta(String nombre);
    

}
