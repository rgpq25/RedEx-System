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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Sinks.One;

@Entity
@Table(name = "tabla")
@SQLDelete(sql = "UPDATE tabla SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class Tabla extends BaseEntity{

    /*
    @OneToMany(mappedBy = "tabla")
    private List <Atributo> atributos;
    */

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_plantilla", referencedColumnName = "id")
    private Plantilla plantilla;
    //Plan de calidad
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_planDeCalidad", referencedColumnName = "id")
    private PlanDeCalidad planDeCalidad;
    

    //Acta de constitucion
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_actaDeConstitucion", referencedColumnName = "id")
    private ActaDeConstitucion actaDeConstitucion;
    

    //Cronograma
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cronograma", referencedColumnName = "id")
    private Cronograma cronograma;

    //Catalogo de Riesgos

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_catalogoDeRiesgos", referencedColumnName = "id")
    private CatalogoDeRiesgos catalogoDeRiesgos;
    

    //EDT
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_edt", referencedColumnName = "id")
    private EDT edt;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_productbacklog", referencedColumnName = "id")
    private ProductBacklog productBacklog;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_presupuesto", referencedColumnName = "id")
    private Presupuesto presupuesto;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_matriz_de_responsabilidades", referencedColumnName = "id")
    private MatrizDeResponsabilidades matrizDeResponsabilidades;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_matriz_de_comunicaciones", referencedColumnName = "id")
    private MatrizDeComunicaciones matrizDeComunicaciones;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tablero_kanban", referencedColumnName = "id")
    private TableroKanban tableroKanban;
    
    //CATALOGO DE INTERESADOS
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_catalogoDeInteresados", referencedColumnName = "id")
    private CatalogoDeInteresados catalogoDeInteresados;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_autoevaluacion", referencedColumnName = "id")
    private Autoevaluacion autoevaluacion;

    //FOREIGN KEY PARA EL BACKLOG
    /*@OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "idBacklog", referencedColumnName = "id")
    private Backlog backlog;
    */

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "factorDeOrden")
    private double factorDeOrden;

    @Column(name = "numeroDeColumnas")
    private int numeroDeColumnas;

    @Column(name = "numeroDeFilas")
    private int numeroDeFilas;

    @Column(name = "esSeccion")
    private boolean esSeccion;

    @Column(name = "esTabla")
    private boolean esTabla;
}
