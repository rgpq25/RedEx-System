package pe.edu.pucp.packetsoft.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.pucp.packetsoft.models.PersonasModel.Persona;

@Entity
@Table(name = "plantilla")
@SQLDelete(sql = "UPDATE plantilla SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Plantilla extends BaseEntity{
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "esPlantillaBase")
    private int esPlantillaBase; //1 si es plantilla base, 0 si no lo es

    @ManyToOne (optional = true)
    @JoinColumn(name = "id_herramienta", referencedColumnName = "id")
    Herramienta herramienta;

    @ManyToOne (optional = true)
    @JoinColumn(name = "id_persona", referencedColumnName = "id")
    Persona persona;

}
