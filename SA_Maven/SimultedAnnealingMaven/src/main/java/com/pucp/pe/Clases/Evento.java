package com.pucp.pe.Clases;

import java.util.Date;

public class Evento {
    private Date fechaHora;
    private Aeropuerto aeropuerto;
    private int cambio;
    private int cantidadAcumulada;

    public Evento(Date fechaHora, Aeropuerto aeropuerto, int cambio) {
        this.fechaHora = fechaHora;
        this.aeropuerto = aeropuerto;
        this.cambio = cambio;
        this.cantidadAcumulada = 0;
    }

    // Getters y Setters
    public Date getFechaHora() {
        return fechaHora;
    }

    public Aeropuerto getAeropuerto() {
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
