package com.pucp.pe.Clases;

import java.util.concurrent.atomic.AtomicInteger;

public class ContadorID {
    private static final AtomicInteger contador = new AtomicInteger(0);

    public static int obtenerSiguienteID() {
        return contador.incrementAndGet();
    }

    public static void reiniciar() {
        contador.set(0);
    }
}
