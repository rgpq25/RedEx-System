package Clases;

import java.util.Date;

public class RegistroAlmacenamiento {
    int paqueteId;
    Date fechaInicio;
    Date fechaFin;
    String aeropuerto;

    public RegistroAlmacenamiento(int paqueteId, Date fechaInicio, Date fechaFin,
            String aeropuerto) {
        this.paqueteId = paqueteId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.aeropuerto = aeropuerto;
    }

    public int getPaqueteId() {
        return paqueteId;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public String getAeropuerto() {
        return aeropuerto;
    }
}
