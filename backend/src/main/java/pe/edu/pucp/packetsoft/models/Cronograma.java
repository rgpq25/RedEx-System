package pe.edu.pucp.packetsoft.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cronograma")
@SQLDelete(sql = "UPDATE cronograma SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter


public class Cronograma extends BaseEntity{
    @Column(name="fechaInicio")
    private Date fechaInicio;

    @Column(name="fechaFin")
    private Date fechaFin;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_herramientaxproyecto")
    private HerramientaXProyecto herramientaXProyecto;
}
