package Clases;

import java.util.Date;

public class Paquete {
    private int id;
    private Ubicacion ciudad_almacen; // ciudad donde se encuentra actualmente el paquete
    private Ubicacion ciudad_origen;
    private Ubicacion ciudad_destino;
    private Date fecha_maxima_entrega;
    private Date fecha_recepcion;
    private Vuelo[] lista_vuelos;

    public Paquete(int id, Ubicacion ciudad_almacen, PlanRuta plan_rutas, Ubicacion ciudad_origen,
            Ubicacion ciudad_destino, Date fecha_maxima_entrega,
            Date fecha_recepcion) {
        this.id = id;
        this.ciudad_almacen = ciudad_almacen;
        this.ciudad_origen = ciudad_origen;
        this.ciudad_destino = ciudad_destino;
        this.fecha_maxima_entrega = fecha_maxima_entrega;
        this.fecha_recepcion = fecha_recepcion;
    }

    public Paquete(Ubicacion ciudad_almacen,
            Ubicacion ciudad_origen, Ubicacion ciudad_destino,
            Date fecha_recepcion) {
        this.id = ContadorID.obtenerSiguienteID();
        this.ciudad_almacen = ciudad_almacen;
        this.ciudad_origen = ciudad_origen;
        this.ciudad_destino = ciudad_destino;
        try {
            String gmd = ciudad_destino.getZonaHoraria();
            int diferenciaHoraria = Integer.parseInt(gmd);
            long tiempoDiferenciaHoraria = diferenciaHoraria * 60 * 60 * 1000;
            this.fecha_maxima_entrega = new Date(
                    fecha_recepcion.getTime() + (2 * 24 * 60 * 60 * 1000) - tiempoDiferenciaHoraria);
        } catch (Exception e) {
            System.out.println("Error al calcular la fecha de entrega");
        }
        this.fecha_maxima_entrega = new Date();
        this.fecha_recepcion = fecha_recepcion;
    }

    public Paquete(Paquete _paquete) {
        this.id = _paquete.id;
        this.ciudad_almacen = _paquete.ciudad_almacen;
        this.ciudad_origen = _paquete.ciudad_origen;
        this.ciudad_destino = _paquete.ciudad_destino;
        this.fecha_maxima_entrega = _paquete.fecha_maxima_entrega;
        this.fecha_recepcion = _paquete.fecha_recepcion;
    }

    public Paquete() {

    }

    public void agregar_vuelo(Vuelo vuelo) {
        if (lista_vuelos == null) {
            lista_vuelos = new Vuelo[1];
            lista_vuelos[0] = vuelo;
        } else {
            Vuelo[] nueva_lista = new Vuelo[lista_vuelos.length + 1];
            for (int i = 0; i < lista_vuelos.length; i++) {
                nueva_lista[i] = lista_vuelos[i];
            }
            nueva_lista[lista_vuelos.length] = vuelo;
            lista_vuelos = nueva_lista;
        }
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

    public void print() {
        System.out.println("Paquete " + id + " - " + ciudad_origen.getId() + " - " + ciudad_destino.getId() + " - "
                + fecha_recepcion + " - " + fecha_maxima_entrega);
    }

    public Vuelo[] getListaVuelos() {
        return lista_vuelos;
    }

    public void setListaVuelos(Vuelo[] lista_vuelos) {
        this.lista_vuelos = lista_vuelos;
    }

}
