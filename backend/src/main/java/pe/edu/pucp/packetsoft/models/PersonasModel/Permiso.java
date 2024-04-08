package pe.edu.pucp.packetsoft.models.PersonasModel;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.pucp.packetsoft.models.BaseEntity;

@Entity
@Table(name = "permiso")
@SQLDelete(sql = "UPDATE permiso SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Permiso extends BaseEntity{
    //1 = tiene el permiso 
    //0 = no tiene el permiso
    @Column(name = "gestionarProyectos")
    private int gestionarProyectos;

    @Column(name = "visualizarProyectos")
    private int visualizarProyectos;

    @Column(name = "gestionarUsuarios")
    private int gestionarUsuarios;

    @Column(name = "gestionarReporteAdmin")
    private int gestionarReporteAdmin;

    @Column(name = "gestionarGruposDeProyecto")
    private int gestionarGruposDeProyecto;

}
