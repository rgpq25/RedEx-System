package pucp.e3c.redex_back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ubicacion")
public class Ubicacion {
    @Id
    String id;
    String continente;
    String pais;
    String ciudad;
    String ciudad_abreviada;
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
