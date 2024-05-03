package pucp.e3c.redex_back.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "capacidad_almacen")
public class CapacidadAlmacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Date id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_aeropuerto", referencedColumnName = "id")
    Aeropuerto aeropuerto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_simulacion", referencedColumnName = "id")
    Simulacion simulacionActual;

    int capacidadUtilizada;

}
