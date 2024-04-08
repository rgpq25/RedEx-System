package pe.edu.pucp.packetsoft.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "herramientaxproyecto")
@SQLDelete(sql = "UPDATE herramientaxproyecto SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class HerramientaXProyecto extends BaseEntity{
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_Herramienta")
    private Herramienta herramienta;

    @Column(name="fechaDeAsignacion")
    private Date fechaDeAsignacion;

    @Column(name="usuarioCreacion")
    private String usuarioCreacion;

    @Column(name="usuarioActualizacion")
    private String usuarioActualizacion;

    //@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ManyToOne()
    @JoinColumn(name = "id_Proyecto")
    private Proyecto proyecto;

    
}
