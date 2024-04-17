package Clases;

public class Ubicacion {
    String id;
    //String continente;
    //String pais;
    //String ciudad;
    //String ciudad_abreviada;
    String zona_horaria;

    public Ubicacion(String id, String zona_horaria){
        this.id = id;
        this.zona_horaria = zona_horaria;
    }

    public String getId() {
        return id;
    }

    public String getZonaHoraria(){
        return zona_horaria;
    }

    public void setId(String id) {
        this.id = id;
    }    

    public void setZonaHoraria(String zona_horaria){
        this.zona_horaria = zona_horaria;
    }

}
