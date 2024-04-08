package pe.edu.pucp.packetsoft.models;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;
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

@Entity
@Table(name = "proyecto_persona")
@SQLDelete(sql = "UPDATE proyecto_persona SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ProyectoPersona extends BaseEntity {
    
    @ManyToOne(optional = true)
    @JoinColumn(name = "idProyecto", referencedColumnName = "id")
    private Proyecto proyecto;

    @ManyToOne(optional = true)
    @JoinColumn(name = "idPersona", referencedColumnName = "id")
    private Persona persona;

    @Column(name = "RolProyecto")
    private String RolProyecto;
}
