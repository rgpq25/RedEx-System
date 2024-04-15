public class Vuelo {
    Aeropuerto origen;
    Aeropuerto destino;
    String fechaPartida;
    String fechaLlegada;
    String identificador;

    Vuelo(Aeropuerto origen, Aeropuerto destino, String fechaPartida, String fechaLlegada, String identificador) {
        this.origen = origen;
        this.destino = destino;
        this.fechaPartida = fechaPartida;
        this.fechaLlegada = fechaLlegada;
        this.identificador = identificador;
    }
}
