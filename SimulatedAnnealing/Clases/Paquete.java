package Clases;

import java.util.Date;

public class Paquete {
    private int id;
    private boolean en_almacen;
    private String id_ciudad_almacen; //ciudad donde se encuentra actualmente el paquete
    private boolean entregado;
    private PlanRuta plan_rutas;
    private String id_ciudad_origen;
    private String id_ciudad_destino;
    private Date fecha_maxima_entrega;
    private Date fecha_recepcion;

    public Paquete(int id, boolean en_almacen, String codigo_ciudad_almacen, boolean entregado, PlanRuta plan_rutas, String id_ciudad_origen, String id_ciudad_destino, Date fecha_maxima_entrega,
            Date fecha_recepcion) {
        this.id = id;
        this.en_almacen = en_almacen;
        this.id_ciudad_almacen = codigo_ciudad_almacen;
        this.entregado = entregado;
        this.plan_rutas = plan_rutas;
        this.id_ciudad_origen = id_ciudad_origen;
        this.id_ciudad_destino = id_ciudad_destino;
        this.fecha_maxima_entrega = fecha_maxima_entrega;
        this.fecha_recepcion = fecha_recepcion;
    }

    public Paquete(){
        
    }

    public int getId() {
        return id;
    }

    public boolean isEn_almacen() {
        return en_almacen;
    }

    public String getId_ciudad_almacen() {
        return id_ciudad_almacen;
    }

    public boolean isEntregado() {
        return entregado;
    }

    public PlanRuta getPlan_rutas() {
        return plan_rutas;
    }

    public String getId_ciudad_origen() {
        return id_ciudad_origen;
    }

    public String getId_ciudad_destino() {
        return id_ciudad_destino;
    }

    public Date getFecha_maxima_entrega() {
        return fecha_maxima_entrega;
    }

    public Date getFecha_recepcion() {
        return fecha_recepcion;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEn_almacen(boolean en_almacen) {
        this.en_almacen = en_almacen;
    }

    public void setId_ciudad_almacen(String codigo_ciudad_almacen) {
        this.id_ciudad_almacen = codigo_ciudad_almacen;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }

    public void setPlan_rutas(PlanRuta plan_rutas) {
        this.plan_rutas = plan_rutas;
    }

    public void setId_ciudad_destino(String id_ciudad_destino) {
        this.id_ciudad_destino = id_ciudad_destino;
    }

    public void setId_ciudad_origen(String id_ciudad_origen) {
        this.id_ciudad_origen = id_ciudad_origen;
    }

    public void setFecha_maxima_entrega(Date fecha_maxima_entrega) {
        this.fecha_maxima_entrega = fecha_maxima_entrega;
    }

    public void setFecha_recepcion(Date fecha_recepcion) {
        this.fecha_recepcion = fecha_recepcion;
    }

}
