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
    private String id;

    @Column(length = 64)
    private String continente;
    @Column(length = 64)
    private String pais;
    @Column(length = 64)
    private String ciudad;
    @Column(length = 64)
    private String ciudadAbreviada;
    @Column(length = 8)
    private String zonaHoraria;

    public Ubicacion(String id, String continente, String pais, String ciudad, String ciudadAbreviada,
            String zonaHoraria) {
        this.id = id;
        this.continente = continente;
        this.pais = pais;
        this.ciudad = ciudad;
        this.ciudadAbreviada = ciudadAbreviada;
        this.zonaHoraria = zonaHoraria;
    }

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

    public String getCiudadAbreviada() {
        return ciudadAbreviada;
    }

    public void setCiudadAbreviada(String ciudadAbreviada) {
        this.ciudadAbreviada = ciudadAbreviada;
    }

    public String getZonaHoraria() {
        return zonaHoraria;
    }

    public void setZonaHoraria(String zonaHoraria) {
        this.zonaHoraria = zonaHoraria;
    }

}
