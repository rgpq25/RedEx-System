package Clases;

import java.util.Date;

public class FlujoCapacidad {
    private int id;
    private int capacidad;
    private Date fecha; 
    private String id_ciudad;

    public FlujoCapacidad(int id, int capacidad, Date fecha, String id_ciudad) {
        this.id = id;
        this.capacidad = capacidad;
        this.fecha = fecha;
        this.id_ciudad = id_ciudad;
    }

    public FlujoCapacidad(int capacidad, Date fecha, String id_ciudad) {
        this.capacidad = capacidad;
        this.fecha = fecha;
        this.id_ciudad = id_ciudad;
    }

    public int getId() {
        return id;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public Date getFecha() {
        return fecha;
    }   

    public String getId_ciudad() {
        return id_ciudad;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setId_ciudad(String id_ciudad) {
        this.id_ciudad = id_ciudad;
    }

}
