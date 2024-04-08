package pe.edu.pucp.packetsoft.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partida")
@SQLDelete(sql = "UPDATE partida SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class Partida extends BaseEntity{
    
    @Column(name="nombre")
    private String nombre;

    @Column(name="cantidadDeRecursos")
    private int cantidadDeRecursos;

    @Column(name="tarifa")
    private double tarifa;

    @Column(name="tiempoRequerido")
    private double tiempoRequerido;

    @Column(name="subTotal")
    private double subTotal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idPresupuesto",referencedColumnName = "id")
    private Presupuesto presupuesto;
}
