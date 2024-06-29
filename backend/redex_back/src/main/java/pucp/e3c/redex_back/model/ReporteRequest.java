package pucp.e3c.redex_back.model;

import java.util.Date;

public class ReporteRequest {
    private int idSimulacion;
    private Date fechaCorte;

    public ReporteRequest() {
    }

    public int getIdSimulacion() {
        return idSimulacion;
    }
    public void setIdSimulacion(int idSimulacion) {
        this.idSimulacion = idSimulacion;
    }
    public Date getFechaCorte() {
        return fechaCorte;
    }
    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    
}
