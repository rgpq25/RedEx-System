package Clases;

import java.util.ArrayList;

public class PlanRuta {
    Integer id;
    ArrayList<Vuelo> vuelos;

    public PlanRuta(int id) {
        this.id = id;
        this.vuelos = new ArrayList<Vuelo>();
    }

    public PlanRuta(ArrayList<Vuelo> vuelos) {
        this.vuelos = vuelos;
    }

    public ArrayList<Vuelo> getVuelos() {
        return vuelos;
    }

    public void setVuelos(ArrayList<Vuelo> vuelos) {
        this.vuelos = vuelos;
    }

    public int length() {
        return vuelos.size();
    }

    public String toString() {
        StringBuilder descripcion = new StringBuilder("Ruta " + id + ": ");
        for (Vuelo vuelo : vuelos) {
            descripcion.append(vuelo.getPlan_vuelo().getId_ubicacion_origen())
                    .append(" -> ")
                    .append(vuelo.getPlan_vuelo().getId_ubicacion_destino())
                    .append(" (ID: ")
                    .append(vuelo.getId())
                    .append(") | ");
        }
        return descripcion.toString();
    }
}
