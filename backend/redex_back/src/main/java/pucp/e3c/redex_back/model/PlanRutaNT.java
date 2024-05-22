package pucp.e3c.redex_back.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class PlanRutaNT {
    Integer id;
    ArrayList<Vuelo> vuelos;
    String codigo;

    public PlanRutaNT() {
        this.id = ContadorID.obtenerSiguienteID();
        this.vuelos = new ArrayList<Vuelo>();
        this.codigo = "";
    }

    public PlanRutaNT(int id) {
        this.id = id;
        this.vuelos = new ArrayList<Vuelo>();

    }

    public void updateCodigo() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm"); // Formato para las fechas

        for (Vuelo v : vuelos) {
            String ciudadOrigen = v.getPlanVuelo().getHoraCiudadOrigen();
            String fechaPartida = sdf.format(v.getFechaSalida());
            String ciudadDestino = v.getPlanVuelo().getHoraCiudadDestino();
            String fechaLlegada = sdf.format(v.getFechaLlegada());

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

    public PlanRutaNT(ArrayList<Vuelo> vuelos) {
        this.vuelos.sort(Comparator.comparing(Vuelo::getFechaSalida));
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
        while (index < vuelos.size() && vuelos.get(index).getFechaSalida().compareTo(vuelo.getFechaSalida()) < 0) {
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
                    .append(vuelo.getPlanVuelo().getCiudadOrigen().getId())
                    .append(" (")
                    .append(Funciones.getFormattedDate(vuelo.getFechaSalida()))
                    .append(") -> ")
                    .append(vuelo.getPlanVuelo().getCiudadDestino().getId())
                    .append(" (")
                    .append(Funciones.getFormattedDate(vuelo.getFechaLlegada()))
                    .append(") ")

                    .append(" (ID: ")
                    .append(vuelo.getId())
                    .append(") | ");
        }
        return descripcion.toString();
    }

    public Date getInicio() {
        if (!vuelos.isEmpty()) {
            return vuelos.get(0).getFechaSalida();
        } else {
            return null;
        }
    }

    public Date getFin() {
        if (!vuelos.isEmpty()) {
            return vuelos.get(vuelos.size() - 1).getFechaLlegada();
        } else {
            return null;
        }
    }
}
