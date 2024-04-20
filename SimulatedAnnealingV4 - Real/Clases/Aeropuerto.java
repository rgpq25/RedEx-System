package Clases;

import java.util.ArrayList;
import java.util.Date;

public class Aeropuerto {
    private Ubicacion ubicacion;
    private int capacidad_utilizada;
    private int capacidad_maxima;
    
    private ArrayList<CapacidadPorTiempo> capacidadPorTiempo;

    public Aeropuerto(Ubicacion ubicacion, int capacidad_utilizada, int capacidad_maxima) {
        this.ubicacion = ubicacion;
        this.capacidad_utilizada = capacidad_utilizada;
        this.capacidad_maxima = capacidad_maxima;
    }

    public Aeropuerto() {

    }

    public String getId() {
        return ubicacion.getId();
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public int getCapacidad_utilizada() {
        return capacidad_utilizada;
    }

    public int getCapacidad_maxima() {
        return capacidad_maxima;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void setCapacidad_utilizada(int capacidad_utilizada) {
        this.capacidad_utilizada = capacidad_utilizada;
    }

    public void setCapacidad_maxima(int capacidad_maxima) {
        this.capacidad_maxima = capacidad_maxima;
    }

    public void print() {
        System.out.println("Aeropuerto: " + this.ubicacion.getId() + " | GMT: " + this.ubicacion.getZonaHoraria()
                + " | " + this.capacidad_utilizada + " " + this.capacidad_maxima);
    }


    public boolean tieneCapacidadDisponible(Date momento) {
        int capacidadUtilizada = 0;
        for (CapacidadPorTiempo c : capacidadPorTiempo) {
            if (c.tiempoInicio.before(momento) && c.tiempoFin.after(momento)) {
                capacidadUtilizada = c.capacidadUtilizada;
                break;
            }
        }
        return capacidad_maxima - capacidadUtilizada > 0;
    }

    public void ocuparCapacidad(Date inicio, Date fin, int capacidad) {
        capacidadPorTiempo.add(new CapacidadPorTiempo(inicio, fin, capacidad));
    }

    private class CapacidadPorTiempo {
        private Date tiempoInicio;
        private Date tiempoFin;
        private int capacidadUtilizada;

        public CapacidadPorTiempo(Date tiempoInicio, Date tiempoFin, int capacidadUtilizada) {
            this.tiempoInicio = tiempoInicio;
            this.tiempoFin = tiempoFin;
            this.capacidadUtilizada = capacidadUtilizada;
        }
    }
}
