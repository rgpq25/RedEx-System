package Clases;

public class PlanVuelo {
    private int id;
    private String id_ubicacion_origen;
    private String id_ubicacion_destino;
    private String hora_ciudad_origen;
    private String hora_ciudad_destino;
    private int capacidad_maxima;

    public PlanVuelo(int id, String id_ubicacion_origen, String id_ubicacion_destino, String hora_ciudad_origen, String hora_ciudad_destino, int capacidad_maxima) {
        this.id = id;
        this.id_ubicacion_origen = id_ubicacion_origen;
        this.id_ubicacion_destino = id_ubicacion_destino;
        this.hora_ciudad_origen = hora_ciudad_origen;
        this.hora_ciudad_destino = hora_ciudad_destino;
        this.capacidad_maxima = capacidad_maxima;
    }

    public int getId() {
        return id;
    }

    public String getId_ubicacion_origen() {
        return id_ubicacion_origen;
    }

    public String getId_ubicacion_destino() {
        return id_ubicacion_destino;
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

    public void setId_ubicacion_origen(String id_ubicacion_origen) {
        this.id_ubicacion_origen = id_ubicacion_origen;
    }

    public void setId_ubicacion_destino(String id_ubicacion_destino) {
        this.id_ubicacion_destino = id_ubicacion_destino;
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
}
