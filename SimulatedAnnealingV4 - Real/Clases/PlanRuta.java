package Clases;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

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
        this.vuelos.sort(Comparator.comparing(Vuelo::getFecha_salida));
    }

    public PlanRuta(ArrayList<Vuelo> vuelos) {
        this.vuelos.sort(Comparator.comparing(Vuelo::getFecha_salida));
    }

    public ArrayList<Vuelo> getVuelos() {
        return vuelos;
    }

    public int getId() {
        return id;
    }

    public void agregarVuelo(Vuelo vuelo) {
        int index = 0;
        while (index < vuelos.size() && vuelos.get(index).getFecha_salida().compareTo(vuelo.getFecha_salida()) < 0) {
            index++;
        }
        vuelos.add(index, vuelo);
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

    public Date getInicio() {
        return vuelos.get(0).getFecha_salida();
    }

    public Date getFin() {
        return vuelos.get(vuelos.size() - 1).getFecha_llegada();
    }
}
