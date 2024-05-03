package pucp.e3c.redex_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pucp.e3c.redex_back.model.Envio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Integer>{
    //private String codigoSeguridad;
    public Envio findByCodigoSeguridad(String codigoSeguridad);
}
