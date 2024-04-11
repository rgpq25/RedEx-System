package Clases;

public class PlanRuta {
    Vuelo[]  vuelos;

    public PlanRuta(Vuelo[] vuelos) {
        this.vuelos = vuelos;
    }

    public Vuelo[] getVuelos() {
        return vuelos;
    }

    public void setVuelos(Vuelo[] vuelos) {
        this.vuelos = vuelos;
    }

    public int length(){
        return vuelos.length;
    }
}
