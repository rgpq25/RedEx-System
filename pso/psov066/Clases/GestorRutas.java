package Clases;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import Clases.GrafoVuelos;

public class GestorRutas {
    private RutasAlmacenadas rutasAlmacenadas = new RutasAlmacenadas();
    private GrafoVuelos grafo;
    private Paquete paquetePropio;
    private int posicionActual;
    private int contadorRutas;
    private Integer maximoRutas;

    public GestorRutas(GrafoVuelos grafo, Paquete paquetePropio) {
        this.grafo = grafo;
        this.paquetePropio = paquetePropio;
        this.maximoRutas = null;
        iniciar();
    }

    public PlanRuta consultarRuta(Integer posicion) {
        PlanRuta ruta = rutasAlmacenadas.obtenerRutaPorId(posicion);
        if (ruta == null) {
            return grafo.generarRutasPaquete(paquetePropio, rutasAlmacenadas, posicion);
        }
        return rutasAlmacenadas.obtenerRutaPorId(posicion);

    }

    private void iniciar() {
        this.posicionActual = 0;
        PlanRuta ruta = grafo.generarRutasPaquete(paquetePropio, rutasAlmacenadas, 0);
        rutasAlmacenadas.agregarRuta(0, ruta);
        this.contadorRutas = 1;
    }

    public int avanzar(int pasos) {
        if (maximoRutas != null) {
            AtomicInteger x = new AtomicInteger(pasos);
            if (rutasAlmacenadas.obtenerClaveXClavesDespues(this.posicionActual, x)) {
                this.posicionActual = x.get();
                return x.get();
            } else {
                return x.get() + 1; // Aqui debe hacerse el rebote
            }
        } else {
            if (generarRutaEn(posicionActual + pasos)) {
                return posicionActual + pasos;
            } else {
                return avanzar(pasos);
            }
        }
    }

    public Integer consultarMaximo() {
        if (maximoRutas == null) {
            return this.rutasAlmacenadas.obtenerUltimaClave();
        } else {
            return null;
        }
    }

    public Integer consultarMinimo() {
        if (maximoRutas == null) {
            return this.rutasAlmacenadas.obtenerPrimeraClave();
        } else {
            return null;
        }
    }

    public int retroceder(int pasos) {
        if (maximoRutas != null) {
            AtomicInteger x = new AtomicInteger(pasos);
            if (rutasAlmacenadas.obtenerClaveXClavesAtras(this.posicionActual, x)) {
                this.posicionActual = x.get();
                return x.get();
            } else {
                return x.get() - 1; // Aqui debe hacerse el rebote
            }
        } else {
            if (generarRutaEn(posicionActual - pasos)) {
                return posicionActual - pasos;
            } else {
                return retroceder(pasos);
            }
        }
    }

    public void setPosicion(int posicion) {
        this.posicionActual = posicion;
    }

    public PlanRuta consultarRutaActual() {
        PlanRuta rutaAlmacenada = this.rutasAlmacenadas.obtenerRutaPorId(this.posicionActual);
        if (rutaAlmacenada == null) {
            return grafo.generarRutasPaquete(paquetePropio, rutasAlmacenadas, this.posicionActual);
        } else {
            return rutaAlmacenada;
        }
    }

    public int consultarPosicionActual() {
        return posicionActual;
    }

    private boolean generarRutaEn(int posicion) {
        PlanRuta ruta = grafo.generarRutasPaquete(paquetePropio, rutasAlmacenadas, posicion);
        if (ruta == null) {
            maximoRutas = contadorRutas;
            return false;
        }
        rutasAlmacenadas.agregarRuta(posicion, ruta);
        this.contadorRutas++;
        return true;
    }

    public PlanRuta obtenerRutaPorId(Integer id) {
        return rutasAlmacenadas.obtenerRutaPorId(id);
    }

    public PlanRuta obtenerRutaPorCodigo(String codigo) {
        return rutasAlmacenadas.obtenerRutaPorCodigo(codigo);
    }

    public void eliminarRuta(Integer id) {
        rutasAlmacenadas.eliminarRuta(id);
    }

}
