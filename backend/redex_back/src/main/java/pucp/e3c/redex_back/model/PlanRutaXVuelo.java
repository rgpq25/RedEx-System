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
public class PlanRutaXVuelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_plan_ruta", referencedColumnName = "id")
    private PlanRuta planRuta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vuelo", referencedColumnName = "id")
    private Vuelo vuelo;

    int indiceDeOrden;

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

    public Vuelo getVuelo() {
        return vuelo;
    }

    public void setVuelo(Vuelo vuelo) {
        this.vuelo = vuelo;
    }

    public PlanRutaXVuelo() {
    }

    public void fillData(Integer id, PlanRuta planRuta, Vuelo vuelo, int indiceDeOrden) {
        this.id = id;
        this.planRuta = planRuta;
        this.vuelo = vuelo;
        this.indiceDeOrden = indiceDeOrden;
    }

    public int getIndiceDeOrden() {
        return indiceDeOrden;
    }

    public void setIndiceDeOrden(int indiceDeOrden) {
        this.indiceDeOrden = indiceDeOrden;
    }
}
