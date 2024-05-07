package pucp.e3c.redex_back.model;

import java.util.concurrent.atomic.AtomicInteger;

public class ContadorID {
    private static final AtomicInteger contador = new AtomicInteger(0);
    private static final AtomicInteger contadorPaquete = new AtomicInteger(0);

    public static int obtenerSiguienteID() {
        return contador.incrementAndGet();
    }

    public static void reiniciar() {
        contador.set(0);
    }

    public static int obtenerSiguienteIDPaquet() {
        return contadorPaquete.incrementAndGet();
    }
}
