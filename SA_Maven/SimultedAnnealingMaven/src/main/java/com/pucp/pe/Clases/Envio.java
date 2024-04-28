package com.pucp.pe.Clases;

import java.util.Date;

public class Envio {
    int id;
    String codigo_ciudad_origen;
    String codigo_ciudad_destino;
    Date fecha_ciudad_origen; // GMT y hora incluida
    String hora_ciudad_origen;
    int cantidad_paquetes;
    Paquete[] paquetes;
}
