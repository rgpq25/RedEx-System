package pe.edu.pucp.packetsoft.models;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
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
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;

@Entity
@Table(name = "actadereunion")
@SQLDelete(sql = "UPDATE actadereunion SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ActaDeReunion extends BaseEntity{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idpersona", referencedColumnName = "id")
    private Persona persona;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idproyecto", referencedColumnName = "id")
    private Proyecto proyecto;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "iddocumento", referencedColumnName = "id")
    private Documento documento;

    @Column(name ="principalesAcuerdos")
    private String principalesAcuerdos;

    @Column(name ="temasPendientes")
    private String temasPendientes;
}