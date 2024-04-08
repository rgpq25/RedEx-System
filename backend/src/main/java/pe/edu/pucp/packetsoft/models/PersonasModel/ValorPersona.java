package pe.edu.pucp.packetsoft.models.PersonasModel;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.databind.JsonSerializable.Base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.pucp.packetsoft.models.BaseEntity;
import pe.edu.pucp.packetsoft.models.Valor;

@Entity
@Table(name = "valor_persona")
@SQLDelete(sql = "UPDATE valor_persona SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class ValorPersona extends BaseEntity{
    
    @ManyToOne(optional = true)
    @JoinColumn(name = "idPersona", referencedColumnName = "id")
    private Persona persona;

    @ManyToOne(optional = true)
    @JoinColumn(name = "idValor", referencedColumnName = "id")
    private Valor valor;

}
