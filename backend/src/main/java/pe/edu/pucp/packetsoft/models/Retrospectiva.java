package pe.edu.pucp.packetsoft.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;

@Entity
@Table(name = "retrospectiva")
@SQLDelete(sql = "UPDATE retrospectiva SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Retrospectiva extends BaseEntity{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_persona", referencedColumnName = "id")
    private Persona persona;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proyecto", referencedColumnName = "id")
    private Proyecto proyecto;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_documento", referencedColumnName = "id")
    private Documento documento;

    @Column(name="compromisos")
    private String compromisos;

    @Column(name="observaciones")
    private String observaciones;
}