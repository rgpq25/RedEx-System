package pe.edu.pucp.packetsoft.models;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "actividad")
@SQLDelete(sql = "UPDATE actividad SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Actividad extends BaseEntity{
    @Column(name="descripcion")
    private String descripcion;

    @Column(name ="usuarioCreacion")
    private String usuarioCreacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proyecto")
    private Proyecto proyecto;
}
