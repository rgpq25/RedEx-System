package pucp.e3c.redex_back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vuelo")
public class Cliente {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_usuario", referencedColumnName = "id")
  Usuario usuario;

  @Column(length = 40)
  private String informacionContacto;

  @Column(length = 100)
  private String preferenciasNotificacion;

  public Cliente() {
    // Empty constructor
  }

  // Getters and setters for id
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  // Getters and setters for usuario
  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  // Getters and setters for informacionContacto
  public String getInformacionContacto() {
    return informacionContacto;
  }

  public void setInformacionContacto(String informacionContacto) {
    this.informacionContacto = informacionContacto;
  }

  // Getters and setters for preferenciasNotificacion
  public String getPreferenciasNotificacion() {
    return preferenciasNotificacion;
  }

  public void setPreferenciasNotificacion(String preferenciasNotificacion) {
    this.preferenciasNotificacion = preferenciasNotificacion;
  }
}
