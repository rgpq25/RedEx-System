package pucp.e3c.redex_back.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "simulacion")
public class Simulacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer Id;

    Date fechaInicioSim;
    Date fechaFinSim;
    Date fechaInicioSistema;
    double multiplicadorTiempo;
    int estado;
    // tiempoActual = fechaInicioSim + (time - fechaInicioSistema) * multiplicador
    // String resultado;

    public void fillData() {
        fechaInicioSim = new Date();
        fechaFinSim = new Date();
        fechaInicioSistema = new Date();
        multiplicadorTiempo = 1;
        estado = 0;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Date getFechaInicioSim() {
        return fechaInicioSim;
    }

    public void setFechaInicioSim(Date fechaInicioSim) {
        this.fechaInicioSim = fechaInicioSim;
    }

    public Date getFechaFinSim() {
        return fechaFinSim;
    }

    public void setFechaFinSim(Date fechaFinSim) {
        this.fechaFinSim = fechaFinSim;
    }

    public Date getFechaInicioSistema() {
        return fechaInicioSistema;
    }

    public void setFechaInicioSistema(Date fechaInicioSistema) {
        this.fechaInicioSistema = fechaInicioSistema;
    }

    public double getMultiplicadorTiempo() {
        return multiplicadorTiempo;
    }

    public void setMultiplicadorTiempo(double multiplicadorTiempo) {
        this.multiplicadorTiempo = multiplicadorTiempo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Simulacion{" +
                "Id=" + Id +
                ", fechaInicioSim=" + fechaInicioSim +
                ", fechaFinSim=" + fechaFinSim +
                ", fechaInicioSistema=" + fechaInicioSistema +
                ", multiplicadorTiempo=" + multiplicadorTiempo +
                ", estado=" + estado +
                '}';
    }
}
