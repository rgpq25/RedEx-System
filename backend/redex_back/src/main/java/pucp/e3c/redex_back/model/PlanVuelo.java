package pucp.e3c.redex_back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "plan_vuelo")
public class PlanVuelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ubicacion_origen", referencedColumnName = "id")
    private Ubicacion ciudadOrigen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ubicacion_destino", referencedColumnName = "id")
    private Ubicacion ciudadDestino;

    @Column(length = 20)
    private String horaCiudadOrigen;

    @Column(length = 20)
    private String horaCiudadDestino;

    private int capacidadMaxima;

    public PlanVuelo(int id, Ubicacion ciudadOrigen, Ubicacion ciudadDestino, String horaCiudadOrigen,
            String horaCiudadDestino, int capacidadMaxima) {
        this.id = id;
        this.ciudadOrigen = ciudadOrigen;
        this.ciudadDestino = ciudadDestino;
        this.horaCiudadOrigen = horaCiudadOrigen;
        this.horaCiudadDestino = horaCiudadDestino;
        this.capacidadMaxima = capacidadMaxima;
    }

    public PlanVuelo(PlanVuelo _plan_vuelo) {
        this.id = _plan_vuelo.id;
        this.ciudadOrigen = _plan_vuelo.ciudadOrigen;
        this.ciudadDestino = _plan_vuelo.ciudadDestino;
        this.horaCiudadOrigen = _plan_vuelo.horaCiudadOrigen;
        this.horaCiudadDestino = _plan_vuelo.horaCiudadDestino;
        this.capacidadMaxima = _plan_vuelo.capacidadMaxima;
    }

    public boolean es_continental() {
        return ciudadOrigen.getContinente().equals(ciudadDestino.getContinente());
    }

    public PlanVuelo() {
    }

    public int getId() {
        return id;
    }

    public Ubicacion getCiudadOrigen() {
        return ciudadOrigen;
    }

    public Ubicacion getCiudadDestino() {
        return ciudadDestino;
    }

    public String getHoraCiudadOrigen() {
        return horaCiudadOrigen;
    }

    public String getHoraCiudadDestino() {
        return horaCiudadDestino;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCiudadOrigen(Ubicacion ciudadOrigen) {
        this.ciudadOrigen = ciudadOrigen;
    }

    public void setCiudadDestino(Ubicacion ciudadDestino) {
        this.ciudadDestino = ciudadDestino;
    }

    public void setHoraCiudadOrigen(String horaCiudadOrigen) {
        this.horaCiudadOrigen = horaCiudadOrigen;
    }

    public void setHoraCiudadDestino(String horaCiudadDestino) {
        this.horaCiudadDestino = horaCiudadDestino;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    // override method to do a toString
    @Override
    public String toString() {
        return "PlanVuelo [capacidadMaxima=" + capacidadMaxima + ", ciudadDestino=" + ciudadDestino.getId()
                + ", ciudadOrigen=" + ciudadOrigen.getId() + ", horaCiudadDestino=" + horaCiudadDestino
                + ", horaCiudadOrigen=" + horaCiudadOrigen + ", id=" + id + "]";
    }
}
