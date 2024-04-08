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
@Table(name = "presupuesto")
@SQLDelete(sql = "UPDATE presupuesto SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Presupuesto extends BaseEntity{
    @Column(name="presupuestoPlanificado")
    private double presupuestoPlanificado;

    @Column(name="presupuestoEjecutado")
    private double presupuestoEjecutado;

    @Column(name="reservaDeContingenciaPorcentaje")
    private double reservaDeContingenciaPorcentaje;

    @Column(name="reservaDeContingenciaMonto")
    private double reservaDeContingenciaMonto;

    @Column(name="reservaDeGestion")
    private double reservaDeGestion;

    @Column(name="ganancia")
    private double ganancia;
    
    @Column(name="aplicaGanancia")
    private boolean aplicaGanancia;
    
    @Column(name="aplicaIGV")
    private boolean aplicaIGV;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_herramientaxproyecto")
    private HerramientaXProyecto herramientaXProyecto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_IGV", referencedColumnName = "id")
    private IGV igv;
}
