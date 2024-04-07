package Clases;

public class Aeropuerto {
    private String id;
    private Ubicacion ubicacion;
    private int capacidad_utilizada;
    private int capacidad_maxima;

    public Aeropuerto(String id, Ubicacion ubicacion, int capacidad_utilizada, int capacidad_maxima) {
        this.id = id;
        this.ubicacion = ubicacion;
        this.capacidad_utilizada = capacidad_utilizada;
        this.capacidad_maxima = capacidad_maxima;
    }
    
    public String getId() {
        return id;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void setCapacidad_utilizada(int capacidad_utilizada) {
        this.capacidad_utilizada = capacidad_utilizada;
    }
}
