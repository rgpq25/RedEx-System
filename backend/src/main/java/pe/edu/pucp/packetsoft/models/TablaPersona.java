package pe.edu.pucp.packetsoft.models;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;
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
@Table(name = "tabla_persona")
@SQLDelete(sql = "UPDATE tabla_persona SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class TablaPersona extends BaseEntity{

    @ManyToOne(optional = true)
    @JoinColumn(name = "idPersona", referencedColumnName = "id")
    private Persona persona;

    @ManyToOne(optional = true)
    @JoinColumn(name = "idTabla", referencedColumnName = "id")
    private Tabla tabla;    
}
