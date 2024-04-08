package pe.edu.pucp.packetsoft.models.PersonasModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.pucp.packetsoft.models.BaseEntity;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "tipo_usuario")
@SQLDelete(sql = "UPDATE tipo_usuario SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class TipoUsuario extends BaseEntity{

    @Column(name = "nombre")
    private String nombre;

    //@ManyToOne(fetch = FetchType.LAZY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_permiso")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // si borramos esto el login de sistema no funciona
    private Permiso permiso;

}