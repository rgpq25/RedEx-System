package Clases;

import java.util.ArrayList;

public class PlanRuta {
    Integer id;
    ArrayList<Vuelo> vuelos;

    public PlanRuta() {
        this.id = ContadorID.obtenerSiguienteID();
        this.vuelos = new ArrayList<Vuelo>();
    }

    public PlanRuta(int id) {
        this.id = id;
        this.vuelos = new ArrayList<Vuelo>();
    }

    public PlanRuta(PlanRuta plan) {
        this.id = plan.id;
        this.vuelos = new ArrayList<Vuelo>(plan.vuelos);
    }

    public PlanRuta(ArrayList<Vuelo> vuelos) {
        this.vuelos = vuelos;
    }

    public ArrayList<Vuelo> getVuelos() {
        return vuelos;
    }

    public int getId(){
        return id;
    }

    public void agregarVuelo(Vuelo vuelo) {
        vuelos.add(vuelo);
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
            descripcion
                    .append(vuelo.getPlan_vuelo().getCiudadOrigen().getId())
                    .append(" (")
                    .append(Funciones.getFormattedDate(vuelo.getFecha_salida()))
                    .append(") -> ")
                    .append(vuelo.getPlan_vuelo().getCiudadDestino().getId())   
                    .append(" (")
                    .append(Funciones.getFormattedDate(vuelo.getFecha_llegada()))
                    .append(") ")
                    
                    .append(" (ID: ")
                    .append(vuelo.getId())
                    .append(") | ");
        }
        return descripcion.toString();
    }
}
