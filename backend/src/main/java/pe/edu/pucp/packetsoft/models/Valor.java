package pe.edu.pucp.packetsoft.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "valor")
@SQLDelete(sql = "UPDATE valor SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Valor extends BaseEntity{

    @Column(name = "factorOrden")
    private double factorOrden;

    @Column(name = "contenido")
    private String contenido;

    @Column(name = "esPrimario")
    private boolean esPrimario;
    /* 
    @ManyToOne(optional = true)
    @JoinColumn(name = "idPadre", referencedColumnName = "id")
    private Valor valor;
    */
    
    @ManyToOne (optional = true)
    @JoinColumn(name = "idAtributo", referencedColumnName = "id")
    Atributo atributo;

    @ManyToOne (optional = true)
    @JoinColumn(name = "idEtiqueta", referencedColumnName = "id")
    Etiqueta etiqueta;

}