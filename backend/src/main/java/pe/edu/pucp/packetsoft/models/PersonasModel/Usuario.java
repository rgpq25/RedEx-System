package pe.edu.pucp.packetsoft.models.PersonasModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.pucp.packetsoft.models.BaseEntity;

@Entity
@Table(name = "usuario")
@SQLDelete(sql = "UPDATE usuario SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Usuario extends BaseEntity{
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Column(name = "usuario")
    private String usuario;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    //estes atributo indica si el usuario es propio del sistema o si es proveniente de google 
    // 0 = es del sistema 
    // 1 = es de google
    @Column(name = "proveedor")
    private int proveedor;

}
