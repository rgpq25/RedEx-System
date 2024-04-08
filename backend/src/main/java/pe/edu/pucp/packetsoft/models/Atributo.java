package pe.edu.pucp.packetsoft.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "atributo")
@SQLDelete(sql = "UPDATE atributo SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Atributo extends BaseEntity{

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idTabla", referencedColumnName = "id")
    private Tabla tabla;

    @Column(name = "factorDeOrden")
    private double factorDeOrden;

    @Column(name = "tipoDeColumna")
    private int tipoDeColumna;

    @Column(name = "seMuestraEnPantallaPrincipal")
    private boolean seMuestraEnPantallaPrincipal;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "seMuestraEnReporte")
    private boolean seMuestraEnReporte;

    @Column(name = "esEliminable")
    private boolean esEliminable;

    @Column(name = "esParrafo")
    private boolean esParrafo;
}
