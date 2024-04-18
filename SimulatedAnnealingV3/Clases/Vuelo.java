package Clases;

import java.util.Date;

public class Vuelo {
    private int id;
    private PlanVuelo plan_vuelo;
    private int capacidad_utilizada;
    private Date fecha_salida; // GMT
    private Date fecha_llegada; // GMT

    public Vuelo(int id, PlanVuelo plan_vuelo, int capacidad_utilizada, Date fecha_salida, Date fecha_llegada) {
        this.id = id;
        this.plan_vuelo = plan_vuelo;
        this.capacidad_utilizada = capacidad_utilizada;
        this.fecha_salida = fecha_salida;
        this.fecha_llegada = fecha_llegada;
    }

    public Vuelo(PlanVuelo plan_vuelo, Date fecha_salida, Date fecha_llegada) {
        this.id = ContadorID.obtenerSiguienteID();
        this.plan_vuelo = plan_vuelo;
        this.capacidad_utilizada = 0;
        this.fecha_salida = fecha_salida;
        this.fecha_llegada = fecha_llegada;
    }

    public Vuelo(Vuelo _vuelo) {
        this.id = _vuelo.id;
        this.plan_vuelo = new PlanVuelo(_vuelo.plan_vuelo);
        this.capacidad_utilizada = _vuelo.capacidad_utilizada;
        this.fecha_salida = _vuelo.fecha_salida;
        this.fecha_llegada = _vuelo.fecha_llegada;
    }

    public Vuelo() {
    }

    public int getId() {
        return id;
    }

    public PlanVuelo getPlan_vuelo() {
        return plan_vuelo;
    }

    public int getCapacidad_utilizada() {
        return capacidad_utilizada;
    }

    public Date getFecha_salida() {
        return fecha_salida;
    }

    public Date getFecha_llegada() {
        return fecha_llegada;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlan_vuelo(PlanVuelo plan_vuelo) {
        this.plan_vuelo = plan_vuelo;
    }

    public void setCapacidad_utilizada(int capacidad_utilizada) {
        this.capacidad_utilizada = capacidad_utilizada;
    }

    public void setFecha_salida(Date fecha_salida) {
        this.fecha_salida = fecha_salida;
    }

    public void setFecha_llegada(Date fecha_llegada) {
        this.fecha_llegada = fecha_llegada;
    }

    public void aumentar_capacidad_utilizada(int capacidad) {
        this.capacidad_utilizada += capacidad;
    }

}
