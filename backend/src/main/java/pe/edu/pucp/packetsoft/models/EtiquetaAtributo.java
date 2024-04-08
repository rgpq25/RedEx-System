package pe.edu.pucp.packetsoft.models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "etiqueta_atributo")
@SQLDelete(sql = "UPDATE etiqueta_atributo SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class EtiquetaAtributo extends BaseEntity{

    @ManyToOne(optional = true)
    @JoinColumn(name = "idEtiqueta", referencedColumnName = "id")
    private Etiqueta etiqueta;

    @ManyToOne(optional = true)
    @JoinColumn(name = "idAtributo", referencedColumnName = "id")
    private Atributo atributo;

}
