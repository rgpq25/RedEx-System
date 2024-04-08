package pe.edu.pucp.packetsoft.repository.HerrramientasRepository;
import pe.edu.pucp.packetsoft.models.Atributo;
import pe.edu.pucp.packetsoft.models.Herramienta;
import pe.edu.pucp.packetsoft.models.Plantilla;
import pe.edu.pucp.packetsoft.models.Tabla;
import pe.edu.pucp.packetsoft.models.Valor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface PlantillaRepository extends JpaRepository<Plantilla, Integer>{
    public List<Plantilla> findAll();
    public Plantilla findPlantillaById(int id);
    public List<Plantilla> findPlantillaByHerramienta(Herramienta herramienta);

}
