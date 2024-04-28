package com.pucp.pe.Clases;

public class PlanVuelo {
    private int id;
    private Ubicacion ciudad_origen;
    private Ubicacion ciudad_destino;
    private String hora_ciudad_origen;
    private String hora_ciudad_destino;
    private int capacidad_maxima;

    public PlanVuelo(int id, Ubicacion ciudad_origen, Ubicacion ciudad_destino, String hora_ciudad_origen,
            String hora_ciudad_destino, int capacidad_maxima) {
        this.id = id;
        this.ciudad_origen = ciudad_origen;
        this.ciudad_destino = ciudad_destino;
        this.hora_ciudad_origen = hora_ciudad_origen;
        this.hora_ciudad_destino = hora_ciudad_destino;
        this.capacidad_maxima = capacidad_maxima;
    }

    public PlanVuelo(PlanVuelo _plan_vuelo) {
        this.id = _plan_vuelo.id;
        this.ciudad_origen = _plan_vuelo.ciudad_origen;
        this.ciudad_destino = _plan_vuelo.ciudad_destino;
        this.hora_ciudad_origen = _plan_vuelo.hora_ciudad_origen;
        this.hora_ciudad_destino = _plan_vuelo.hora_ciudad_destino;
        this.capacidad_maxima = _plan_vuelo.capacidad_maxima;
    }

    public boolean es_continental() {
        return ciudad_origen.getContinente().equals(ciudad_destino.getContinente());
    }

    public PlanVuelo() {
    }

    public int getId() {
        return id;
    }

    public Ubicacion getCiudadOrigen() {
        return ciudad_origen;
    }

    public Ubicacion getCiudadDestino() {
        return ciudad_destino;
    }

    public String getHora_ciudad_origen() {
        return hora_ciudad_origen;
    }

    public String getHora_ciudad_destino() {
        return hora_ciudad_destino;
    }

    public int getCapacidad_maxima() {
        return capacidad_maxima;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCiudadOrigen(Ubicacion ciudad_origen) {
        this.ciudad_origen = ciudad_origen;
    }

    public void setCiudadDestino(Ubicacion ciudad_destino) {
        this.ciudad_destino = ciudad_destino;
    }

    public void setHora_ciudad_origen(String hora_ciudad_origen) {
        this.hora_ciudad_origen = hora_ciudad_origen;
    }

    public void setHora_ciudad_destino(String hora_ciudad_destino) {
        this.hora_ciudad_destino = hora_ciudad_destino;
    }

    public void setCapacidad_maxima(int capacidad_maxima) {
        this.capacidad_maxima = capacidad_maxima;
    }

    // override method to do a toString
    @Override
    public String toString() {
        return "PlanVuelo [capacidad_maxima=" + capacidad_maxima + ", ciudad_destino=" + ciudad_destino.getId()
                + ", ciudad_origen=" + ciudad_origen.getId() + ", hora_ciudad_destino=" + hora_ciudad_destino
                + ", hora_ciudad_origen=" + hora_ciudad_origen + ", id=" + id + "]";
    }
}
