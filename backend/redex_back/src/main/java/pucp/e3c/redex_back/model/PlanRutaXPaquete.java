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
@Table(name = "plan_rutaxvuelo")
public class PlanRutaXPaquete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_plan_ruta", referencedColumnName = "id")
    private PlanRuta planRuta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vuelo", referencedColumnName = "id")
    private Paquete paquete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PlanRuta getPlanRuta() {
        return planRuta;
    }

    public void setPlanRuta(PlanRuta planRuta) {
        this.planRuta = planRuta;
    }

    public Paquete getPaquete() {
        return paquete;
    }

    public void setPaquete(Paquete paquete) {
        this.paquete = paquete;
    }

    public PlanRutaXPaquete(Integer id, PlanRuta planRuta, Paquete paquete, int indiceDeOrden) {
        this.id = id;
        this.planRuta = planRuta;
        this.paquete = paquete;
    }
}
