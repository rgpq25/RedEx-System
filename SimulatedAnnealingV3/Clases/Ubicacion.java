package Clases;

public class Ubicacion {
    String id;
    String continente;
    String pais;
    String ciudad;
    String ciudad_abreviada;
    String zona_horaria;

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
        return "Ubicacion{" +
                "id='" + id + '\'' +
                ", continente='" + continente + '\'' +
                ", pais='" + pais + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", ciudadAbreviada='" + ciudad_abreviada + '\'' +
                ", zonaHoraria='" + zona_horaria + '\'' +
                '}';
    }

    public String getID() {
        return this.id;
    }

}
