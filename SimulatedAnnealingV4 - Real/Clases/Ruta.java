package Clases;

import java.sql.Date;

/*
 * Las rutas se generan con el planificador replanificador
 * 
 */

public class Ruta {
    int id;
    String id_ciudad_origen;
    String id_ciudad_destino;
    int orden_secuencia;
    Date fecha_inicio;
    Date fecha_fin;
    int id_vuelo;
}
