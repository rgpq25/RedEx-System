package pucp.e3c.redex_back.model;

import java.util.List;

public class RespuestaReportePaquete {
    private Paquete paquete;
    private List<Vuelo> vuelos;

    

    public RespuestaReportePaquete() {
    }
    
    public Paquete getPaquete() {
        return paquete;
    }
    public void setPaquete(Paquete paquete) {
        this.paquete = paquete;
    }
    public List<Vuelo> getVuelos() {
        return vuelos;
    }
    public void setVuelos(List<Vuelo> vuelos) {
        this.vuelos = vuelos;
    }
    
}
