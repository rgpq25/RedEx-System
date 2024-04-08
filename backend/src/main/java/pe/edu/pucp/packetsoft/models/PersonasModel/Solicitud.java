package pe.edu.pucp.packetsoft.models.PersonasModel;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "solicitud")
@SQLDelete(sql = "UPDATE solicitud SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class Solicitud extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipoUsuario")
    private TipoUsuario tipoUsuario;

    @Column(name = "estado")
    private String estado;

    @Column(name = "usuarioCreacion")
    private String usuarioCreacion;

    @Column(name = "usuarioActualizacion")
    private String usuarioActualizacion;

}

/*
 * estado: string
 * usuario creacion
 * usuarioactualizacion
 */