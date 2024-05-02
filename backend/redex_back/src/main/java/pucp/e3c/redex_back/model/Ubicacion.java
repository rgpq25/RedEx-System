package pucp.e3c.redex_back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ubicacion")
public class Ubicacion {
    @Id
    @Column(length = 8)
    String id;

    @Column(length = 64)
    String continente;
    @Column(length = 64)
    String pais;
    @Column(length = 64)
    String ciudad;
    @Column(length = 64)
    String ciudad_abreviada;
    @Column(length = 8)
    String zona_horaria;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getContinente() {
        return continente;
    }
    public void setContinente(String continente) {
        this.continente = continente;
    }
    public String getPais() {
        return pais;
    }
    public void setPais(String pais) {
        this.pais = pais;
    }
    public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    public String getCiudad_abreviada() {
        return ciudad_abreviada;
    }
    public void setCiudad_abreviada(String ciudad_abreviada) {
        this.ciudad_abreviada = ciudad_abreviada;
    }
    public String getZona_horaria() {
        return zona_horaria;
    }
    public void setZona_horaria(String zona_horaria) {
        this.zona_horaria = zona_horaria;
    }

    
}
