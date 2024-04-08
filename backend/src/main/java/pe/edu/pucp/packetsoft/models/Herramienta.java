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
@Table(name = "herramienta")
@SQLDelete(sql = "UPDATE herramienta SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class Herramienta extends BaseEntity{
    /*
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria")
    private CategoriaHerramienta categoriaHerramienta;
     */
    
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private CategoriaHerramienta categoriaHerramienta;

    @Column (name = "nombre")
    private String nombre;

    @Column (name = "idDeOrden")
    private int idDeOrden;

    @Column (name = "esObligatoria")
    private int esObligatoria;
}
