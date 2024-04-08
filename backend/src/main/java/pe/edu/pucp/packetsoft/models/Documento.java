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
import javax.persistence.Column;

@Entity
@Table(name = "documento")
@SQLDelete(sql = "UPDATE documento SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Documento extends BaseEntity{
    @Column(name="nombre")
    private String nombre;

    @Column(name = "tamano")
    private long tamano; 

    @Column(name="contenido")
    private String contenido; // es una URL para el s3

    @Column(name ="extension")
    private String extension;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proyecto", referencedColumnName = "id")
    private Proyecto proyecto;
}
