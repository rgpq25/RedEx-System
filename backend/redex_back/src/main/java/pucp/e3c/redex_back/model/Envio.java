package pucp.e3c.redex_back.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "envio")
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ubicacionOrigen")
    private Ubicacion ubicacionOrigen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ubicacionDestino")
    private Ubicacion ubicacionDestino;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRecepcion;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaLimiteEntrega; // EN UTC

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaLimiteEntregaZonaHorariaDestino;

    @Column(length = 16)
    private String estado;
    private Integer cantidadPaquetes;
    @Column(length = 32)
    private String codigoSeguridad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_emisor", referencedColumnName = "id")
    private Cliente emisor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_receptor", referencedColumnName = "id")
    private Cliente receptor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_simulacion", referencedColumnName = "id")
    private Simulacion simulacionActual;

    /*
     * public Envio(Ubicacion origen, Ubicacion destino, Date fechaRecepcion, Date
     * fecha_maxima_entrega) {
     * this.ubicacionOrigen = origen;
     * this.ubicacionDestino = destino;
     * this.fechaRecepcion = fechaRecepcion;
     * this.fechaLimiteEntrega = fecha_maxima_entrega;
     * this.estado = "En proceso";
     * this.cantidadPaquetes = 1;
     * this.codigoSeguridad = "123456";
     * }
     */

    public void fillData(Ubicacion origen, Ubicacion destino, Date fecha_recepcion) {
        this.setUbicacionOrigen(origen);
        this.setUbicacionDestino(destino);

        this.setEstado("En proceso");

        int agregar = 0;
        if (origen.getContinente().equals(destino.getContinente())) {
            agregar = 1;
        } else {
            agregar = 2;
        }
        Date fecha_recepcion_GMT0 = Funciones.convertTimeZone(
                fecha_recepcion,
                origen.getZonaHoraria(),
                "UTC");

        this.setFechaRecepcion(fecha_recepcion_GMT0);
        Date fecha_recepcion_origen = Funciones.convertTimeZone(
                fecha_recepcion_GMT0, "UTC", origen.getZonaHoraria());
        Date fecha_maxima_entrega_GMTDestino = Funciones.addDays(fecha_recepcion_origen, agregar);
        Date fecha_maxima_entrega_GMT0 = Funciones.convertTimeZone(
                fecha_maxima_entrega_GMTDestino,
                destino.getZonaHoraria(),
                "UTC");
        this.setFechaLimiteEntrega(fecha_maxima_entrega_GMT0);
        this.setFechaLimiteEntregaZonaHorariaDestino(fecha_maxima_entrega_GMTDestino);
        this.setCantidadPaquetes(1);
        this.setCodigoSeguridad("123456");
    }

    public Cliente getEmisor() {
        return emisor;
    }

    public void setEmisor(Cliente emisor) {
        this.emisor = emisor;
    }

    public Cliente getReceptor() {
        return receptor;
    }

    public void setReceptor(Cliente receptor) {
        this.receptor = receptor;
    }

    // TO DO id emisor
    // TO DO id receptor
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Ubicacion getUbicacionOrigen() {
        return ubicacionOrigen;
    }

    public void setUbicacionOrigen(Ubicacion ubicacionOrigen) {
        this.ubicacionOrigen = ubicacionOrigen;
    }

    public Ubicacion getUbicacionDestino() {
        return ubicacionDestino;
    }

    public void setUbicacionDestino(Ubicacion ubicacionDestino) {
        this.ubicacionDestino = ubicacionDestino;
    }

    public Date getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(Date fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public Date getFechaLimiteEntrega() {
        return fechaLimiteEntrega;
    }

    public void setFechaLimiteEntrega(Date fechaLimiteEntrega) {
        this.fechaLimiteEntrega = fechaLimiteEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getCantidadPaquetes() {
        return cantidadPaquetes;
    }

    public void setCantidadPaquetes(Integer cantidadPaquetes) {
        this.cantidadPaquetes = cantidadPaquetes;
    }

    public String getCodigoSeguridad() {
        return codigoSeguridad;
    }

    public void setCodigoSeguridad(String codigoSeguridad) {
        this.codigoSeguridad = codigoSeguridad;
    }

    public String toString() {
        return "Envio: idEnvio: " + id + " - desde:  " + this.ubicacionOrigen.getId() + " hasta: "
                + this.ubicacionDestino.getId() + " - fechaRecepcion: "
                + fechaRecepcion + " - fechaLimiteEntrega: "
                + fechaLimiteEntrega + "\n";
    }

    public Simulacion getSimulacionActual() {
        return simulacionActual;
    }

    public void setSimulacionActual(Simulacion simulacionActual) {
        this.simulacionActual = simulacionActual;
    }

    public Date getFechaLimiteEntregaZonaHorariaDestino() {
        return fechaLimiteEntregaZonaHorariaDestino;
    }

    public void setFechaLimiteEntregaZonaHorariaDestino(Date fechaLimiteEntregaZonaHorariaDestino) {
        this.fechaLimiteEntregaZonaHorariaDestino = fechaLimiteEntregaZonaHorariaDestino;
    }

}
