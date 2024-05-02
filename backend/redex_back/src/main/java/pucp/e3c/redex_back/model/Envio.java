package pucp.e3c.redex_back.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "envio")
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ubicacion_origen")
    private Ubicacion ubicacion_origen;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ubicacion_destino")
    private Ubicacion ubicacion_destino;
    private Date fecha_recepcion;
    private Date fecha_limite_entrega;
    @Column(length = 16)
    private String estado;
    private Integer cantidad_paquetes;
    @Column(length = 32)
    private String codigo_seguridad;
    //TO DO id emisor
    //TO DO id receptor
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Ubicacion getUbicacion_origen() {
        return ubicacion_origen;
    }
    public void setUbicacion_origen(Ubicacion ubicacion_origen) {
        this.ubicacion_origen = ubicacion_origen;
    }
    public Ubicacion getUbicacion_destino() {
        return ubicacion_destino;
    }
    public void setUbicacion_destino(Ubicacion ubicacion_destino) {
        this.ubicacion_destino = ubicacion_destino;
    }
    public Date getFecha_recepcion() {
        return fecha_recepcion;
    }
    public void setFecha_recepcion(Date fecha_recepcion) {
        this.fecha_recepcion = fecha_recepcion;
    }
    public Date getFecha_limite_entrega() {
        return fecha_limite_entrega;
    }
    public void setFecha_limite_entrega(Date fecha_limite_entrega) {
        this.fecha_limite_entrega = fecha_limite_entrega;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public Integer getCantidad_paquetes() {
        return cantidad_paquetes;
    }
    public void setCantidad_paquetes(Integer cantidad_paquetes) {
        this.cantidad_paquetes = cantidad_paquetes;
    }
    public String getCodigo_seguridad() {
        return codigo_seguridad;
    }
    public void setCodigo_seguridad(String codigo_seguridad) {
        this.codigo_seguridad = codigo_seguridad;
    }
    
}
