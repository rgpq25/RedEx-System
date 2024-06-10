package pucp.e3c.redex_back.model;

public class RespuestaAlgoritmoEstado {
    private String estado;
    private Simulacion simulacion;

    public RespuestaAlgoritmoEstado() {
    }
    
    public RespuestaAlgoritmoEstado(String estado, Simulacion simulacion) {
        this.estado = estado;
        this.simulacion = simulacion;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public Simulacion getSimulacion() {
        return simulacion;
    }
    public void setSimulacion(Simulacion simulacion) {
        this.simulacion = simulacion;
    }
    
}
