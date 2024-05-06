package pucp.e3c.redex_back.model;

import java.util.ArrayList;
import java.util.HashMap;

public class RespuestaAlgoritmo {
    private HashMap<Integer, Integer> ocupacionVuelos;
    private EstadoAlmacen estadoAlmacen;
    private ArrayList<PlanRutaNT> planesRutas;

    public RespuestaAlgoritmo() {
        this.ocupacionVuelos = new HashMap<>();
        this.estadoAlmacen = new EstadoAlmacen();
        this.planesRutas = new ArrayList<>();
    }

    public RespuestaAlgoritmo(HashMap<Integer, Integer> ocupacionVuelos, EstadoAlmacen estadoAlmacen,
            ArrayList<PlanRutaNT> planesRutas) {
        this.ocupacionVuelos = ocupacionVuelos;
        this.estadoAlmacen = estadoAlmacen;
        this.planesRutas = planesRutas;
    }

    public ArrayList<PlanRutaNT> getPlanesRutas() {
        return planesRutas;
    }

    public void setPlanesRutas(ArrayList<PlanRutaNT> planesRutas) {
        this.planesRutas = planesRutas;
    }

    public HashMap<Integer, Integer> getOcupacionVuelos() {
        return ocupacionVuelos;
    }

    public void setOcupacionVuelos(HashMap<Integer, Integer> ocupacionVuelos) {
        this.ocupacionVuelos = ocupacionVuelos;
    }

    public EstadoAlmacen getEstadoAlmacen() {
        return estadoAlmacen;
    }

    public void setEstadoAlmacen(EstadoAlmacen estadoAlmacen) {
        this.estadoAlmacen = estadoAlmacen;
    }
}
