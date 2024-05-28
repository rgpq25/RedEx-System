package pucp.e3c.redex_back.model;

import java.util.ArrayList;

public class RespuestaAlgoritmo {
    private ArrayList<Vuelo> vuelos;
    private EstadoAlmacen estadoAlmacen;
    private ArrayList<PlanRutaNT> planesRutas;
    private Simulacion simulacion;
    private ArrayList<Paquete> paquetes;
    private boolean correcta;

    public RespuestaAlgoritmo() {
        this.vuelos = new ArrayList<>();
        this.estadoAlmacen = new EstadoAlmacen();
        this.planesRutas = new ArrayList<>();
        this.simulacion = new Simulacion();
        this.paquetes = new ArrayList<Paquete>();
        this.correcta = true;
    }

    public RespuestaAlgoritmo(ArrayList<Vuelo> vuelos, EstadoAlmacen estadoAlmacen,
            ArrayList<PlanRutaNT> planesRutas, Simulacion simulacion) {
        this.vuelos = vuelos;
        this.estadoAlmacen = estadoAlmacen;
        this.planesRutas = planesRutas;
        this.simulacion = simulacion;
        this.correcta = true;
    }

    public void filtrarVuelosSinPaquetes(){
        ArrayList<Vuelo> vuelosFiltrados = new ArrayList<>();
        for (Vuelo vuelo : vuelos) {
            if (vuelo.getCapacidadUtilizada()> 0) {
                vuelosFiltrados.add(vuelo);
            }
        }
        vuelos = vuelosFiltrados;
    }

    public ArrayList<Paquete> getPaquetes() {
        return paquetes;
    }

    public void setPaquetes(ArrayList<Paquete> paquetes) {
        this.paquetes = paquetes;
    }

    public Simulacion getSimulacion() {
        return this.simulacion;
    }

    public void setSimulacion(Simulacion simulacion) {
        this.simulacion = simulacion;
    }

    public ArrayList<PlanRutaNT> getPlanesRutas() {
        return planesRutas;
    }

    public void setPlanesRutas(ArrayList<PlanRutaNT> planesRutas) {
        this.planesRutas = planesRutas;
    }

    public ArrayList<Vuelo> getVuelos() {
        return this.vuelos;
    }

    public void setOcupacionVuelos(ArrayList<Vuelo> vuelos) {
        this.vuelos = vuelos;
    }

    public EstadoAlmacen getEstadoAlmacen() {
        return estadoAlmacen;
    }

    public void setEstadoAlmacen(EstadoAlmacen estadoAlmacen) {
        this.estadoAlmacen = estadoAlmacen;
    }

    public boolean isCorrecta() {
        return correcta;
    }

    public void setCorrecta(boolean correcta) {
        this.correcta = correcta;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RespuestaAlgoritmo {");
        sb.append(" vuelos=").append(vuelos.size());
        sb.append(", estadoAlmacen=").append(estadoAlmacen != null);
        sb.append(", planesRutas=").append(planesRutas.size());
        sb.append(", simulacion=").append(simulacion != null);
        sb.append(" }");
        return sb.toString();
    }

    public void verificarEstado() {
        if (vuelos.isEmpty() || estadoAlmacen == null || planesRutas.isEmpty() || simulacion == null) {
            correcta = false;
        }
    }
}
