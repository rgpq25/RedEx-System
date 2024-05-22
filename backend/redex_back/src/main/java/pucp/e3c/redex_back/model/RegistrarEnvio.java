package pucp.e3c.redex_back.model;

public class RegistrarEnvio {
    String codigo;
    Simulacion simulacion;

    public RegistrarEnvio() {
    }

    // Getters and Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Simulacion getSimulacion() {
        return simulacion;
    }

    public void setSimulacion(Simulacion simulacion) {
        this.simulacion = simulacion;
    }
}
