package pucp.e3c.redex_back.model;

import java.util.Objects;

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

    private double latitud;
    private double longitud;

    public Ubicacion() {
    }

    public void fillData(String id, String continente, String pais, String ciudad, String ciudadAbreviada,
            String zonaHoraria, double latitud, double logitud) {
        this.setId(id);
        this.setContinente(continente);
        this.setPais(pais);
        this.setCiudad(ciudad);
        this.setCiudadAbreviada(ciudadAbreviada);
        this.setZonaHoraria(zonaHoraria);
        this.setLatitud(latitud);
        this.setLongitud(logitud);
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

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Ubicacion ubicacion = (Ubicacion) o;
        return Objects.equals(id, ubicacion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
