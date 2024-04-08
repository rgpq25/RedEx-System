package pe.edu.pucp.packetsoft.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "etiqueta")
@SQLDelete(sql = "UPDATE etiqueta SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Etiqueta extends BaseEntity{

    @Column(name = "contenido")
    private String contenido;

    @Column(name = "valor")
    private String valor;

    @Column(name = "color")
    private String color;

}