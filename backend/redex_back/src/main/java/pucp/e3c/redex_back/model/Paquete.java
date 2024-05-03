package pucp.e3c.redex_back.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "paquete")
public class Paquete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 64)
    private String coordenadaActual;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_aeropuerto_actual", referencedColumnName = "id")
    private Aeropuerto aeropuertoActual;

    private boolean enAeropuerto;
    private boolean entregado;
    private Date fechaDeEntrega;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_envio", referencedColumnName = "id")
    private Envio envio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCoordenadaActual() {
        return coordenadaActual;
    }

    public void setCoordenadaActual(String coordenadaActual) {
        this.coordenadaActual = coordenadaActual;
    }

    public Aeropuerto getAeropuertoActual() {
        return aeropuertoActual;
    }

    public void setAeropuertoActual(Aeropuerto aeropuertoActual) {
        this.aeropuertoActual = aeropuertoActual;
    }

    public boolean isEnAeropuerto() {
        return enAeropuerto;
    }

    public void setEnAeropuerto(boolean enAeropuerto) {
        this.enAeropuerto = enAeropuerto;
    }

    public boolean isEntregado() {
        return entregado;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }

    public Date getFechaDeEntrega() {
        return fechaDeEntrega;
    }

    public void setFechaDeEntrega(Date fechaDeEntrega) {
        this.fechaDeEntrega = fechaDeEntrega;
    }

    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
    }
    
   
    
}
