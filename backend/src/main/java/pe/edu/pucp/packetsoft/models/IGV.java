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

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "igv")
@SQLDelete(sql = "UPDATE igv SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class IGV extends BaseEntity{
    
    @Column(name="esVigente")
    private boolean esVigente;

    @Column(name="valor")
    private double valor;

    //NO ES NECESARIO INDICAR LA FECHA DE REGISTRO COMO UN ATRIBUTO MÁS,
    //YA QUE ESTE HEREDA LOS ATRIBUTOS, ENTRE ELLOS LA FECHA DE CREACIÓN, DE BASE ENTITY
}
