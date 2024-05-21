package pucp.e3c.redex_back.model;

import java.util.ArrayList;
import java.util.HashMap;

public class RespuestaAlgoritmo {
    private ArrayList<Vuelo> vuelos;
    private EstadoAlmacen estadoAlmacen;
    private ArrayList<PlanRutaNT> planesRutas;
    private Simulacion simulacion;

    public RespuestaAlgoritmo() {
        this.vuelos = new ArrayList<>();
        this.estadoAlmacen = new EstadoAlmacen();
        this.planesRutas = new ArrayList<>();
    }

    public RespuestaAlgoritmo(ArrayList<Vuelo> vuelos, EstadoAlmacen estadoAlmacen,
            ArrayList<PlanRutaNT> planesRutas) {
        this.vuelos = vuelos;
        this.estadoAlmacen = estadoAlmacen;
        this.planesRutas = planesRutas;
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
}
