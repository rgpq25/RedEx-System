package pucp.e3c.redex_back.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vuelo")
public class Vuelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_planVuelo", referencedColumnName = "id")
    private PlanVuelo planVuelo;

    private int capacidadUtilizada;
    private Date fechaSalida;
    private Date fechaLlegada;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_simulacion", referencedColumnName = "id")
    Simulacion simulacionActual;


    public void fillData1(int id, PlanVuelo planVuelo, int capacidadUtilizada, Date fechaSalida,
            Date fechaLlegada) {
        this.setId(id);
        this.setPlanVuelo(planVuelo);
        this.setCapacidadUtilizada(capacidadUtilizada);
        this.setFechaSalida(fechaSalida);
        this.setFechaLlegada(fechaLlegada);
    }


    public void fillData2(PlanVuelo planVuelo, Date fechaSalida, Date fechaLlegada) {
        this.setId(ContadorID.obtenerSiguienteID());
        this.setPlanVuelo(planVuelo);
        this.setCapacidadUtilizada(0);
        this.setFechaSalida(fechaSalida);
        this.setFechaLlegada(fechaLlegada);
    }


    public void fillData3(Vuelo _vuelo) {
        this.setId(_vuelo.id);
        PlanVuelo planVuelo = new PlanVuelo();
        planVuelo.fillData2(_vuelo.planVuelo);
        this.setPlanVuelo(planVuelo);
        this.setCapacidadUtilizada(_vuelo.capacidadUtilizada);
        this.setFechaSalida(_vuelo.fechaSalida);
        this.setFechaLlegada(_vuelo.fechaLlegada);
    }

    public Vuelo() {
    }

    public int getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Simulacion getSimulacionActual() {
        return simulacionActual;
    }

    public void setSimulacionActual(Simulacion simulacionActual) {
        this.simulacionActual = simulacionActual;
    }

    public PlanVuelo getPlanVuelo() {
        return planVuelo;
    }

    public int getCapacidadUtilizada() {
        return capacidadUtilizada;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public Date getFechaLlegada() {
        return fechaLlegada;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlanVuelo(PlanVuelo planVuelo) {
        this.planVuelo = planVuelo;
    }

    public void setCapacidadUtilizada(int capacidadUtilizada) {
        this.capacidadUtilizada = capacidadUtilizada;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public void setFechaLlegada(Date fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public void aumentar_capacidadUtilizada(int capacidad) {
        this.capacidadUtilizada += capacidad;
    }

    public String toString() {
        return "Vuelo " + id + " -> " + planVuelo.getCiudadOrigen().getId() + " - "
                + planVuelo.getCiudadDestino().getId() + " - " + fechaSalida + " " + fechaLlegada;
    }

    
}
