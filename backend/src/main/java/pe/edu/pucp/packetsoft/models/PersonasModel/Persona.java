package pe.edu.pucp.packetsoft.models.PersonasModel;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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

@Entity
@Table(name = "persona")
@SQLDelete(sql = "UPDATE persona SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Persona extends BaseEntity {

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "habilitado")
    private int habilitado;

    @Column(name = "primerApellido")
    private String primerApellido;

    @Column(name = "segundoApellido")
    private String segundoApellido;

    @Column(name = "correo")
    private String correo;

    @Column(name = "telefono")
    private String telefono;

    // no sabía si trabajar con un booleano o un int (1 y 0)
    // escogi int 1 y 0
    @Column(name = "usuarioPucp")
    private int usuarioPucp;

    /*
     * @Column(name = "foto")
     * private String foto;
     */

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB", name = "fotoPerfil")
    private String fotoPerfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipoUsuario")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" }) // si borramos esto el login de sistema no funciona
    private TipoUsuario tipoUsuario;

}