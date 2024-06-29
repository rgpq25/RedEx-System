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
@Table(name = "paquetexestadopaquete")
public class PaqueteXEstadoPaquete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_paquete", referencedColumnName = "id")
    private Paquete paquete;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estadoPaquete", referencedColumnName = "id")
    private EstadoPaquete estadoPaquete;

    public PaqueteXEstadoPaquete() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Paquete getPaquete() {
        return paquete;
    }

    public void setPaquete(Paquete paquete) {
        this.paquete = paquete;
    }

    public EstadoPaquete getEstadoPaquete() {
        return estadoPaquete;
    }

    public void setEstadoPaquete(EstadoPaquete estadoPaquete) {
        this.estadoPaquete = estadoPaquete;
    }

    
}
