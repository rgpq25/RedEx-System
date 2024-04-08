package pe.edu.pucp.packetsoft.models;

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
@Table(name = "actadeconstitucion")
@SQLDelete(sql = "UPDATE actadeconstitucion SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class ActaDeConstitucion extends BaseEntity{
    @Column(name="numeroDeSecciones")
    private int numeroDeSecciones;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_herramientaxproyecto")
    private HerramientaXProyecto herramientaXProyecto;
    
}
