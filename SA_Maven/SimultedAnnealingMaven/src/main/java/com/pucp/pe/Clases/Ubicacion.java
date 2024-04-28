package com.pucp.pe.Clases;

import java.util.Objects;

public class Ubicacion {
    String id;
    String continente;
    String pais;
    String ciudad;
    String ciudad_abreviada;
    String zona_horaria;

    public Ubicacion(String id, String zona_horaria) {
        this.id = id;
        this.zona_horaria = zona_horaria;
    }

    public int diferenciaHoraria() {
        return Integer.parseInt(zona_horaria.replaceAll("GMT", ""));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Ubicacion ubicacion = (Ubicacion) o;
        return Objects.equals(id, ubicacion.id) &&
                Objects.equals(continente, ubicacion.continente) &&
                Objects.equals(pais, ubicacion.pais) &&
                Objects.equals(ciudad, ubicacion.ciudad) &&
                Objects.equals(ciudad_abreviada, ubicacion.ciudad_abreviada) &&
                Objects.equals(zona_horaria, ubicacion.zona_horaria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, continente, pais, ciudad, ciudad_abreviada, zona_horaria);
    }

    public Ubicacion(String id, String continente, String pais, String ciudad, String ciudadAbreviada,
            String zonaHoraria) {
        this.id = id;
        this.continente = continente;
        this.pais = pais;
        this.ciudad = ciudad;
        this.ciudad_abreviada = ciudadAbreviada;
        this.zona_horaria = zonaHoraria;
    }

    public String toString() {
        return id + " zona " + zona_horaria;
    }

    public String getId() {
        return id;
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
        return ciudad_abreviada;
    }

    public void setCiudadAbreviada(String ciudad_abreviada) {
        this.ciudad_abreviada = ciudad_abreviada;
    }

    public String getZonaHoraria() {
        return zona_horaria;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setZonaHoraria(String zona_horaria) {
        this.zona_horaria = zona_horaria;
    }

}
