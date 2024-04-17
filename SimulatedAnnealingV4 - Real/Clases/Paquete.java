package Clases;

import java.util.Date;

public class Paquete {
    private int id;
    private Ubicacion ciudad_almacen; //ciudad donde se encuentra actualmente el paquete
    private Ubicacion ciudad_origen;
    private Ubicacion ciudad_destino;
    private Date fecha_maxima_entrega;  //deberia ser en destino
    private Date fecha_recepcion;       //en origen 2023-01-03 09:23:41

    public Paquete(int id,Ubicacion ciudad_almacen, PlanRuta plan_rutas, Ubicacion ciudad_origen, Ubicacion ciudad_destino, Date fecha_maxima_entrega,
            Date fecha_recepcion) {
        this.id = id;
        this.ciudad_almacen = ciudad_almacen;
        this.ciudad_origen = ciudad_origen;
        this.ciudad_destino = ciudad_destino;
        this.fecha_maxima_entrega = fecha_maxima_entrega;
        this.fecha_recepcion = fecha_recepcion;
    }

    public Paquete(Paquete _paquete){
        this.id = _paquete.id;
        this.ciudad_almacen = _paquete.ciudad_almacen;
        this.ciudad_origen = _paquete.ciudad_origen;
        this.ciudad_destino = _paquete.ciudad_destino;
        this.fecha_maxima_entrega = _paquete.fecha_maxima_entrega;
        this.fecha_recepcion = _paquete.fecha_recepcion;
    }


    public Paquete(){
        
    }

    public int getId() {
        return id;
    }

    public Ubicacion getCiudadAlmacen() {
        return ciudad_almacen;
    }

    public Ubicacion getCiudadOrigen() {
        return ciudad_origen;
    }

    public Ubicacion getCiudadDestino() {
        return ciudad_destino;
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

    public void setCiudadAlmacen(Ubicacion _ciudad_almacen) {
        this.ciudad_almacen = _ciudad_almacen;
    }

    public void setCiudadDestino(Ubicacion _ciudad_destino) {
        this.ciudad_destino = _ciudad_destino;
    }

    public void setCiudadOrigen(Ubicacion _ciudad_origen) {
        this.ciudad_origen = _ciudad_origen;
    }

    public void setFecha_maxima_entrega(Date fecha_maxima_entrega) {
        this.fecha_maxima_entrega = fecha_maxima_entrega;
    }

    public void setFecha_recepcion(Date fecha_recepcion) {
        this.fecha_recepcion = fecha_recepcion;
    }

    public void print(){
        System.out.println("Paquete " + id + " - " + ciudad_origen.getId() + " - " + ciudad_destino.getId() + " - " + fecha_recepcion + " - " + fecha_maxima_entrega);
    }

}
