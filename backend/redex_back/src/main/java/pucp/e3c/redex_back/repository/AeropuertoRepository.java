package pucp.e3c.redex_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import pucp.e3c.redex_back.model.Aeropuerto;

import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface AeropuertoRepository extends JpaRepository<Aeropuerto, Integer> {
    // public Aeropuerto findByUbicacion(Ubicacion ubicacion);
    public Aeropuerto findByUbicacionId(String id);
}
