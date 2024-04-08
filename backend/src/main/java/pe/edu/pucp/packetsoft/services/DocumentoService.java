package pe.edu.pucp.packetsoft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import pe.edu.pucp.packetsoft.models.Documento;
import pe.edu.pucp.packetsoft.repository.ProyectosRepository.DocumentoRepository;

@Service
public class DocumentoService {
    @Autowired
    private DocumentoRepository documentoRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentoService.class);

    public Documento register(Documento documento){
        try {
            return documentoRepository.save(documento);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Documento> getAll(){
        try {
            return documentoRepository.findAll();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Documento get(int id){
        try {
            return documentoRepository.findDocumentoById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void delete(int id){
        try {
            documentoRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Documento update(Documento documento){
        try {
            return documentoRepository.save(documento);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
