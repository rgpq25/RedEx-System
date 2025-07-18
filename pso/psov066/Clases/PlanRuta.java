package Clases;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class PlanRuta {
    Integer id;
    ArrayList<Vuelo> vuelos;
    String codigo;

    public PlanRuta() {
        this.id = ContadorID.obtenerSiguienteID();
        this.vuelos = new ArrayList<Vuelo>();
        this.codigo = "";
    }

    public PlanRuta(int id) {
        this.id = id;
        this.vuelos = new ArrayList<Vuelo>();

    }

    private void updateCodigo() {
        StringBuilder sb = new StringBuilder();

        // Iterar sobre cada vuelo y añadir su ID al StringBuilder.
        for (Vuelo v : vuelos) {
            String idVuelo = String.valueOf(v.getId()); // Asumiendo que el ID es numérico y el método getId() lo
                                                        // obtiene.
            sb.append(idVuelo).append("-");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // Elimina el último guión.
        }
        this.codigo = sb.toString();
    }

    public String getCodigo() {
        return codigo;
    }

    public PlanRuta(PlanRuta plan) {
        this.id = plan.id;
        this.vuelos = new ArrayList<Vuelo>(plan.vuelos);
        this.vuelos.sort(Comparator.comparing(Vuelo::getFecha_salida));
        updateCodigo();
    }

    public PlanRuta(ArrayList<Vuelo> vuelos) {
        this.vuelos.sort(Comparator.comparing(Vuelo::getFecha_salida));
        updateCodigo();
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
        /*
         * if(vuelos.isEmpty()) {
         * return descripcion.append("Ruta "+ this.id + "vacía").toString();
         * }
         */
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
        if (!vuelos.isEmpty()) {
            return vuelos.get(0).getFecha_salida();
        } else {
            return null;
        }
    }

    public Date getFin() {
        if (!vuelos.isEmpty()) {
            return vuelos.get(vuelos.size() - 1).getFecha_llegada();
        } else {
            return null;
        }
    }
}
