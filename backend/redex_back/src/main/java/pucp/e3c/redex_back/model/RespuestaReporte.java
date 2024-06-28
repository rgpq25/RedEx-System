package pucp.e3c.redex_back.model;

import java.util.List;

public class RespuestaReporte {
    private Envio envio;
    private List<RespuestaReportePaquete> infoPaquete;

    public RespuestaReporte() {
    }

    public Envio getEnvio() {
        return envio;
    }
    public void setEnvio(Envio envio) {
        this.envio = envio;
    }

    public List<RespuestaReportePaquete> getInfoPaquete() {
        return infoPaquete;
    }

    public void setInfoPaquete(List<RespuestaReportePaquete> infoPaquete) {
        this.infoPaquete = infoPaquete;
    }
    

    
}
