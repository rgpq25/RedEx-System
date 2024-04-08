package pe.edu.pucp.packetsoft.models;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;

@Entity
@Table(name = "grupodeproyectos")
@SQLDelete(sql = "UPDATE grupodeproyectos SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class GrupoDeProyectos extends BaseEntity{
    @Column(name="nombre")
    private String nombre;

    @Column(name="numeroDeProyectos")
    private int numeroDeProyectos;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_persona", referencedColumnName = "id")
    private Persona persona;

    
}
