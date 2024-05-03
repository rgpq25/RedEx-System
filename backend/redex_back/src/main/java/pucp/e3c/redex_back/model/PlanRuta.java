package pucp.e3c.redex_back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "plan_ruta")
public class PlanRuta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String codigo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_simulacion", referencedColumnName = "id")
    Simulacion simulacionActual;

    public PlanRuta() {
        this.id = 0;
        this.codigo = "";
    }

    public PlanRuta(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        StringBuilder descripcion = new StringBuilder("Ruta " + id + ": " + codigo);
        return descripcion.toString();
    }

}
