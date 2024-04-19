package Clases;

import java.util.Date;

public class Evento {
    private Date fechaHora;
    private String aeropuerto;
    private int cambio; // +1 para inicio, -1 para fin
    private int cantidadAcumulada; // Agregar este campo

    public Evento(Date fechaHora, String aeropuerto, int cambio) {
        this.fechaHora = fechaHora;
        this.aeropuerto = aeropuerto;
        this.cambio = cambio;
        this.cantidadAcumulada = 0; // Inicializar en el constructor
    }

    // Getters y Setters
    public Date getFechaHora() {
        return fechaHora;
    }

    public String getAeropuerto() {
        return aeropuerto;
    }

    public int getCambio() {
        return cambio;
    }

    public int getCantidadAcumulada() {
        return cantidadAcumulada;
    }

    public void setCantidadAcumulada(int cantidad) {
        this.cantidadAcumulada = cantidad;
    }
}
