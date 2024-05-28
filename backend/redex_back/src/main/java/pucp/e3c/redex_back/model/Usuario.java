package pucp.e3c.redex_back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(length = 40)
  private String nombre;

  @Column(length = 50)
  private String correo;

  @Column(length = 1)
  private String tipo;

  // Getters
  public Integer getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getCorreo() {
    return correo;
  }

  public String getTipo() {
    return tipo;
  }

  // Setters
  public void setId(Integer id) {
    this.id = id;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void setCorreo(String correo) {
    this.correo = correo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  // Empty constructor
  public Usuario() {
  }

}
