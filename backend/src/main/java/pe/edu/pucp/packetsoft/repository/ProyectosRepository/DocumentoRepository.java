package pe.edu.pucp.packetsoft.repository.ProyectosRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.pucp.packetsoft.models.Documento;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
@Transactional
public interface DocumentoRepository extends JpaRepository<Documento, Integer>{
    Documento findDocumentoById(int id);
}
