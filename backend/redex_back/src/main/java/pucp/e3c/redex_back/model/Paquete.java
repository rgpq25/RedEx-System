package pucp.e3c.redex_back.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "paquete")
public class Paquete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_aeropuerto_actual", referencedColumnName = "id")
    private Aeropuerto aeropuertoActual;

    private boolean enAeropuerto;
    private boolean entregado;
    @Column(nullable = true)
    private Date fechaDeEntrega;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_envio", referencedColumnName = "id")
    private Envio envio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_simulacion", referencedColumnName = "id", nullable = true)
    Simulacion simulacionActual;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_plan_ruta", referencedColumnName = "id", nullable = true)
    PlanRuta planRutaActual;

    /*
     * public Paquete(Aeropuerto aeropuertoActual, Ubicacion origen, Ubicacion
     * destino, Date fechaRecepcion) {
     * this.id = ContadorID.obtenerSiguienteIDPaquet();
     * this.aeropuertoActual = aeropuertoActual;
     * this.coordenadaActual = origen.getId();
     * this.enAeropuerto = true;
     * this.entregado = false;
     * 
     * Date fecha_recepcion_GMT0 = Funciones.convertTimeZone(
     * fechaRecepcion,
     * origen.getZonaHoraria(),
     * "UTC");
     * Date fecha_maxima_entrega_GMTDestino = Funciones.addDays(fechaRecepcion, 2);
     * Date fecha_maxima_entrega_GMT0 = Funciones.convertTimeZone(
     * fecha_maxima_entrega_GMTDestino,
     * destino.getZonaHoraria(),
     * "UTC");
     * 
     * this.fechaDeEntrega = null;
     * this.envio = new Envio(origen, destino, fecha_recepcion_GMT0,
     * fecha_maxima_entrega_GMT0);
     * this.simulacionActual = null;
     * this.planRutaActual = null;
     * }
     */

    public void fillData(Aeropuerto aeropuertoActual, Ubicacion origen, Ubicacion destino, Date fechaRecepcion) {
        this.id = ContadorID.obtenerSiguienteIDPaquet();
        this.aeropuertoActual = aeropuertoActual;
        this.enAeropuerto = true;
        this.entregado = false;

        Date fecha_recepcion_GMT0 = Funciones.convertTimeZone(
                fechaRecepcion,
                origen.getZonaHoraria(),
                "UTC");
        int agregar = 0;
        if (origen.getContinente().equals(destino.getContinente())) {
            agregar = 1;
        } else {
            agregar = 2;
        }

        Date fecha_maxima_entrega_GMTDestino = Funciones.addDays(fechaRecepcion, agregar);
        Date fecha_maxima_entrega_GMT0 = Funciones.convertTimeZone(
                fecha_maxima_entrega_GMTDestino,
                destino.getZonaHoraria(),
                "UTC");

        this.fechaDeEntrega = null;
        Envio envio = new Envio();
        envio.fillData(origen, destino, fecha_recepcion_GMT0, fecha_maxima_entrega_GMT0);
        this.envio = envio;
        this.simulacionActual = null;
        this.planRutaActual = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Aeropuerto getAeropuertoActual() {
        return aeropuertoActual;
    }

    public void setAeropuertoActual(Aeropuerto aeropuertoActual) {
        this.aeropuertoActual = aeropuertoActual;
    }

    public boolean isEnAeropuerto() {
        return enAeropuerto;
    }

    public void setEnAeropuerto(boolean enAeropuerto) {
        this.enAeropuerto = enAeropuerto;
    }

    public boolean isEntregado() {
        return entregado;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }

    public Date getFechaDeEntrega() {
        return fechaDeEntrega;
    }

    public void setFechaDeEntrega(Date fechaDeEntrega) {
        this.fechaDeEntrega = fechaDeEntrega;
    }

    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
    }

    public String toString() {
        return "Paquete: id" + id + " - " + envio.toString();
    }

    public Simulacion getSimulacionActual() {
        return simulacionActual;
    }

    public void setSimulacionActual(Simulacion simulacionActual) {
        this.simulacionActual = simulacionActual;
    }

    public PlanRuta getPlanRutaActual() {
        return planRutaActual;
    }

    public void setPlanRutaActual(PlanRuta planRutaActual) {
        this.planRutaActual = planRutaActual;
    }

}
