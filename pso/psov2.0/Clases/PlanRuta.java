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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm"); // Formato para las fechas

        for (Vuelo v : vuelos) {
            String ciudadOrigen = v.getPlan_vuelo().getHora_ciudad_origen();
            String fechaPartida = sdf.format(v.getFecha_salida());
            String ciudadDestino = v.getPlan_vuelo().getHora_ciudad_destino();
            String fechaLlegada = sdf.format(v.getFecha_llegada());

            sb.append(ciudadOrigen).append("-").append(fechaPartida).append("-")
                    .append(ciudadDestino).append("-").append(fechaLlegada).append(";");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // Elimina el último punto y coma
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
        /*if(vuelos.isEmpty()) {
            return descripcion.append("Ruta "+ this.id + "vacía").toString();
        }*/
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
