package Clases;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistroAlmacenamiento {
    int paqueteId;
    Date fechaInicio;
    Date fechaFin;
    Aeropuerto aeropuerto;

    public RegistroAlmacenamiento(int paqueteId, Date fechaInicio, Date fechaFin,
            Aeropuerto aeropuerto) {
        this.paqueteId = paqueteId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.aeropuerto = aeropuerto;
    }

    public RegistroAlmacenamiento() {

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

    public Aeropuerto getAeropuerto() {
        return aeropuerto;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.format("RegistroAlmacenamiento[paqueteId=%d, fechaInicio='%s', fechaFin='%s', aeropuerto='%s']",
                paqueteId,
                sdf.format(fechaInicio),
                sdf.format(fechaFin),
                aeropuerto);
    }
}
