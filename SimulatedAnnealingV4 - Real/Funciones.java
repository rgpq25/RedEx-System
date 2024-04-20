import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import Clases.Aeropuerto;
import Clases.EstadoAlmacen;
import Clases.Evento;
import Clases.Paquete;
import Clases.PlanRuta;
import Clases.PlanVuelo;
import Clases.RegistroAlmacenamiento;
import Clases.Ubicacion;
import Clases.Vuelo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import java.util.stream.Collectors;

public class Funciones {

    public Funciones() {
    }

    public Date parseDateString(String dateString, String format, String timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date convertTimeZone(Date date, String fromTimeZone, String toTimeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(fromTimeZone));
        String formattedDate = sdf.format(date);

        sdf.setTimeZone(TimeZone.getTimeZone(toTimeZone));
        try {
            return sdf.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    public Aeropuerto[] leerAeropuertos(String inputPath) {
        List<Aeropuerto> aeropuertos_list = new ArrayList<Aeropuerto>();
        Aeropuerto[] aeropuertos = null;
        try {
            File file = new File(inputPath + "/aeropuertos.csv");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                Aeropuerto aeropuerto = new Aeropuerto();

                Ubicacion ubicacion = new Ubicacion(parts[0].trim(), "GMT" + parts[2].trim());

                aeropuerto.setUbicacion(ubicacion);
                aeropuerto.setCapacidad_maxima(Integer.parseInt(parts[1].trim()));
                aeropuerto.setCapacidad_utilizada(0);
                aeropuertos_list.add(aeropuerto);
            }

            scanner.close();
            aeropuertos = aeropuertos_list.toArray(new Aeropuerto[aeropuertos_list.size()]);

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return aeropuertos;
    }

    public Paquete[] leerPaquetes(String inputPath, HashMap<String, Ubicacion> ubicacionMap) {
        List<Paquete> paquetes_list = new ArrayList<Paquete>();
        Paquete[] paquetes = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            File file = new File(inputPath + "/paquetes.csv");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");

            int id = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                Paquete paquete = new Paquete();
                paquete.setId(id);
                paquete.setCiudadAlmacen(ubicacionMap.get(parts[1].trim()));
                paquete.setCiudadOrigen(ubicacionMap.get(parts[1].trim()));
                paquete.setCiudadDestino(ubicacionMap.get(parts[2].trim()));

                String firstDateString = parts[0].trim();
                Date fecha_recepcion = parseDateString(firstDateString, "yyyy-MM-dd HH:mm:ss",
                        paquete.getCiudadOrigen().getZonaHoraria());
                Date fecha_maxima_entrega = addDays(fecha_recepcion, 2);

                paquete.setFecha_recepcion(fecha_recepcion);
                paquete.setFecha_maxima_entrega(fecha_maxima_entrega);
                paquetes_list.add(paquete);

                id++;
            }

            scanner.close();
            paquetes = paquetes_list.toArray(new Paquete[paquetes_list.size()]);

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return paquetes;
    }

    public Vuelo[] leerVuelos(String inputPath, HashMap<String, Ubicacion> ubicacionMap) {
        List<Vuelo> vuelos_list = new ArrayList<Vuelo>();
        Vuelo[] vuelos = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            File file = new File(inputPath + "/vuelos.csv");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");

            int id = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                // TODO: Horas de vuelo segun registro de datos ya estan en sus horas
                // correspondientes
                Vuelo vuelo = new Vuelo();
                vuelo.setPlan_vuelo(new PlanVuelo());
                vuelo.setId(id);
                vuelo.getPlan_vuelo().setCiudadOrigen(ubicacionMap.get(parts[0].trim()));
                vuelo.getPlan_vuelo().setCiudadDestino(ubicacionMap.get(parts[1].trim()));
                vuelo.getPlan_vuelo().setCapacidad_maxima(Integer.parseInt(parts[4].trim()));

                String dateSalida = parts[2].trim();
                String dateLlegada = parts[3].trim();
                Date fecha_salida = parseDateString(dateSalida, "yyyy-MM-dd HH:mm:ss",
                        vuelo.getPlan_vuelo().getCiudadOrigen().getZonaHoraria());
                Date fecha_llegada = parseDateString(dateLlegada, "yyyy-MM-dd HH:mm:ss",
                        vuelo.getPlan_vuelo().getCiudadDestino().getZonaHoraria());
                vuelo.setFecha_salida(fecha_salida);
                vuelo.setFecha_llegada(fecha_llegada);

                vuelos_list.add(vuelo);
                id++;
            }
            scanner.close();
            vuelos = vuelos_list.toArray(new Vuelo[vuelos_list.size()]);

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return vuelos;
    }

    public void ordenarPaquetes(Paquete[] paquetes) {
        for (int i = 0; i < paquetes.length; i++) {
            for (int j = 0; j < paquetes.length - 1; j++) {
                if (paquetes[j].getFecha_recepcion().compareTo(paquetes[j + 1].getFecha_recepcion()) > 0) {
                    Paquete temp = paquetes[j];
                    paquetes[j] = paquetes[j + 1];
                    paquetes[j + 1] = temp;
                }
            }
        }

    }

    public void ordenarVuelos(Vuelo[] vuelos) {
        for (int i = 0; i < vuelos.length; i++) {
            for (int j = 0; j < vuelos.length - 1; j++) {
                if (vuelos[j].getFecha_salida().compareTo(vuelos[j + 1].getFecha_salida()) > 0) {
                    Vuelo temp = vuelos[j];
                    vuelos[j] = vuelos[j + 1];
                    vuelos[j + 1] = temp;
                }
            }
        }
    }

    public ArrayList<Ubicacion> generarUbicaciones(int cantidad) {
        // Lista expandida de ubicaciones posibles con zonas horarias ajustadas
        Ubicacion[] posiblesUbicaciones = {
                new Ubicacion("SKBO", "América del Sur", "Colombia", "Bogotá", "bogo", "-5"),
                new Ubicacion("SEQM", "América del Sur", "Ecuador", "Quito", "quit", "-5"),
                new Ubicacion("SVMI", "América del Sur", "Venezuela", "Caracas", "cara", "-4"),
                new Ubicacion("SBBR", "América del Sur", "Brasil", "Brasilia", "bras", "-3"),
                new Ubicacion("SPIM", "América del Sur", "Perú", "Lima", "lima", "-5"),
                new Ubicacion("SLLP", "América del Sur", "Bolivia", "La Paz", "lapa", "-4"),
                new Ubicacion("SCEL", "América del Sur", "Chile", "Santiago de Chile", "sant", "-3"),
                new Ubicacion("SABE", "América del Sur", "Argentina", "Buenos Aires", "buen", "-3"),
                new Ubicacion("SGAS", "América del Sur", "Paraguay", "Asunción", "asun", "-4"),
                new Ubicacion("SUAA", "América del Sur", "Uruguay", "Montevideo", "mont", "-3"),
                new Ubicacion("LATI", "Europa", "Albania", "Tirana", "tira", "+2"),
                new Ubicacion("EDDI", "Europa", "Alemania", "Berlín", "berl", "+2"),
                new Ubicacion("LOWW", "Europa", "Austria", "Viena", "vien", "+2"),
                new Ubicacion("EBCI", "Europa", "Bélgica", "Bruselas", "brus", "+2"),
                new Ubicacion("UMMS", "Europa", "Bielorrusia", "Minsk", "mins", "+3"),
                new Ubicacion("LBSF", "Europa", "Bulgaria", "Sofia", "sofi", "+3"),
                new Ubicacion("LKPR", "Europa", "Checa", "Praga", "prag", "+2"),
                new Ubicacion("LDZA", "Europa", "Croacia", "Zagreb", "zagr", "+2"),
                new Ubicacion("EKCH", "Europa", "Dinamarca", "Copenhague", "cope", "+2")
        };

        ArrayList<Ubicacion> ubicaciones = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < cantidad; i++) {
            int index = random.nextInt(posiblesUbicaciones.length);
            ubicaciones.add(posiblesUbicaciones[index]);
        }
        return ubicaciones;
    }

    public ArrayList<Aeropuerto> generarAeropuertos(ArrayList<Ubicacion> ubicaciones, int capacidadMaxima) {
        ArrayList<Aeropuerto> aeropuertos = new ArrayList<>();
        for (Ubicacion ubicacion : ubicaciones) {
            aeropuertos.add(new Aeropuerto(ubicacion, 0, capacidadMaxima));
        }
        return aeropuertos;
    }

    public ArrayList<Paquete> generarPaquetes(int n, List<Aeropuerto> aeropuertos, int maximo_dias) {
        ArrayList<Paquete> paquetes = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            Collections.shuffle(aeropuertos);
            Aeropuerto origen = aeropuertos.get(0);
            Aeropuerto destino = aeropuertos.get(1);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, rand.nextInt(maximo_dias + 1));
            Date fechaRecepcion = (Date) cal.getTime();
            paquetes.add(
                    new Paquete(origen.getUbicacion(), origen.getUbicacion(), destino.getUbicacion(), fechaRecepcion));
        }
        return paquetes;
    }

    public ArrayList<PlanVuelo> generarPlanesDeVuelo(ArrayList<Aeropuerto> aeropuertos, int repeticionesPorConexion) {
        Random random = new Random();
        ArrayList<PlanVuelo> planes = new ArrayList<>();

        int idPlan = 0;
        // Generar planes para cada par de aeropuertos
        for (Aeropuerto origen : aeropuertos) {
            for (Aeropuerto destino : aeropuertos) {
                if (!origen.equals(destino)) {
                    for (int i = 0; i < repeticionesPorConexion; i++) {
                        int horaSalidaHora = random.nextInt(24);
                        int horaSalidaMinuto = random.nextInt(60);
                        String horaSalida = String.format("%02d:%02d", horaSalidaHora, horaSalidaMinuto);

                        // Calcular duración del vuelo basada en una lógica ficticia o real (puede ser
                        // basada en distancia u otros factores)
                        int duracionHoras = 1 + random.nextInt(8); // Duración de vuelo de 1 a 8 horas
                        int duracionMinutos = random.nextInt(60);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, horaSalidaHora);
                        cal.set(Calendar.MINUTE, horaSalidaMinuto);
                        cal.add(Calendar.HOUR_OF_DAY, duracionHoras);
                        cal.add(Calendar.MINUTE, duracionMinutos);

                        String horaLlegada = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE));

                        int capacidad = 100 + random.nextInt(401); // Capacidades entre 100 y 500

                        PlanVuelo plan = new PlanVuelo(idPlan++, origen.getUbicacion(), destino.getUbicacion(),
                                horaSalida, horaLlegada, capacidad);
                        planes.add(plan);
                    }
                }
            }
        }

        return planes;
    }

    public ArrayList<Vuelo> generarVuelos(ArrayList<Aeropuerto> aeropuertos, ArrayList<PlanVuelo> planesVuelo,
            int maxDias) {
        ArrayList<Vuelo> vuelos = new ArrayList<>();

        // Mueve la instancia de Calendar aquí para inicializarla una vez por día.
        Calendar cal = Calendar.getInstance();

        for (int day = 0; day < maxDias; day++) {
            // Utiliza la fecha actual como base y añade la cantidad de días correspondiente
            // a la iteración.
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, day);

            for (PlanVuelo plan : planesVuelo) {
                // Configura la hora de partida según el plan.
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(plan.getHora_ciudad_origen().split(":")[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(plan.getHora_ciudad_origen().split(":")[1]));
                Date fechaPartida = cal.getTime();

                // Clona 'cal' para configurar la hora de llegada.
                Calendar calLlegada = (Calendar) cal.clone();
                calLlegada.set(Calendar.HOUR_OF_DAY, Integer.parseInt(plan.getHora_ciudad_destino().split(":")[0]));
                calLlegada.set(Calendar.MINUTE, Integer.parseInt(plan.getHora_ciudad_destino().split(":")[1]));
                Date fechaLlegada = calLlegada.getTime();

                // Comprobar si la fecha de llegada es anterior a la de partida y ajustarla si
                // es necesario.
                if (!fechaLlegada.after(fechaPartida)) {
                    calLlegada.add(Calendar.DAY_OF_MONTH, 1); // Añadir un día a la fecha de llegada.
                    fechaLlegada = calLlegada.getTime();
                }

                // Agrega el vuelo a la lista.
                vuelos.add(new Vuelo(plan, fechaPartida, fechaLlegada));
            }
        }

        return vuelos;
    }

    public ArrayList<PlanRuta> asignarVuelosAPaquetes(ArrayList<Paquete> paquetes, ArrayList<Vuelo> vuelos) {
        // Inicializa la lista de PlanRuta con la misma cantidad de elementos que
        // paquetes
        ArrayList<PlanRuta> planRutas = new ArrayList<>();
        for (int i = 0; i < paquetes.size(); i++) {
            planRutas.add(new PlanRuta(new ArrayList<Vuelo>()));
        }

        // Índice para asociar cada paquete con su PlanRuta correspondiente
        int index = 0;
        for (Paquete paquete : paquetes) {
            String currentLocation = paquete.getCiudadOrigen().getId();
            Set<String> visitados = new HashSet<>();
            visitados.add(currentLocation);

            while (!currentLocation.equals(paquete.getCiudadDestino().getId())) {
                final String finalCurrentLocation = currentLocation;
                List<Vuelo> vuelosPosibles = vuelos.stream()
                        .filter(v -> v.getPlan_vuelo().getCiudadOrigen().getId().equals(finalCurrentLocation)
                                && !visitados.contains(v.getPlan_vuelo().getCiudadDestino().getId()))
                        .collect(Collectors.toList());

                if (vuelosPosibles.isEmpty()) {
                    System.out.println("No hay más vuelos disponibles desde " + currentLocation
                            + " que cumplan con los requisitos.");
                    break;
                }

                Vuelo posibleVuelo = vuelosPosibles.get(new Random().nextInt(vuelosPosibles.size()));
                planRutas.get(index).agregarVuelo(posibleVuelo);
                currentLocation = posibleVuelo.getPlan_vuelo().getCiudadDestino().getId();
                visitados.add(currentLocation);

                if (currentLocation.equals(paquete.getCiudadDestino().getId())) {
                    break;
                }
            }
            index++; // Incrementa el índice para el siguiente paquete y su PlanRuta correspondiente
        }

        return planRutas;
    }

    public Aeropuerto extraAeropuerto(ArrayList<Aeropuerto> aeropuertos, String id) {
        for (Aeropuerto aeropuerto : aeropuertos) {
            if (aeropuerto.getId() == id) {
                return aeropuerto;
            }
        }
        return null;
    }

    public ArrayList<RegistroAlmacenamiento> crearRegistrosAlmacenamiento(ArrayList<Paquete> paquetes,
            ArrayList<PlanRuta> planRutas, ArrayList<Aeropuerto> aeropuertos) {
        ArrayList<RegistroAlmacenamiento> almacenamiento = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < paquetes.size(); i++) {
            Date currentTime = paquetes.get(i).getFecha_recepcion();
            for (Vuelo vuelo : planRutas.get(i).getVuelos()) {
                if (currentTime.before(vuelo.getFecha_salida())) {
                    almacenamiento.add(new RegistroAlmacenamiento(paquetes.get(i).getId(),
                            currentTime,
                            vuelo.getFecha_salida(),
                            extraAeropuerto(aeropuertos, vuelo.getPlan_vuelo().getCiudadOrigen().getId())));
                }
                currentTime = vuelo.getFecha_llegada();
            }
            // Final storage for 1 minute after the last flight
            calendar.setTime(currentTime);
            calendar.add(Calendar.MINUTE, 1); // Añadir un minuto a la última hora de llegada
            Date finalTime = calendar.getTime(); // Obtener la fecha actualizada

            almacenamiento.add(new RegistroAlmacenamiento(paquetes.get(i).getId(),
                    currentTime,
                    finalTime,
                    extraAeropuerto(aeropuertos, paquetes.get(i).getCiudadDestino().getId())));
        }
        return almacenamiento;
    }

    public boolean verificar_capacidad(ArrayList<RegistroAlmacenamiento> registros, ArrayList<Aeropuerto> aeropuertos) {
        ArrayList<Evento> eventos = new ArrayList<>();

        // Crea eventos de inicio y fin
        for (RegistroAlmacenamiento registro : registros) {
            eventos.add(new Evento(registro.getFechaInicio(), registro.getAeropuerto(), 1)); // Evento de inicio
            eventos.add(new Evento(registro.getFechaFin(), registro.getAeropuerto(), -1)); // Evento de fin
        }

        // Ordena los eventos por fecha y hora
        Collections.sort(eventos, Comparator.comparing(Evento::getFechaHora));

        HashMap<Aeropuerto, Integer> acumuladosPorAeropuerto = new HashMap<>();
        HashMap<Aeropuerto, Integer> capacidadMaximaPorAeropuerto = new HashMap<>();

        // Inicializar capacidades máximas
        for (Aeropuerto aeropuerto : aeropuertos) {
            capacidadMaximaPorAeropuerto.put(aeropuerto, aeropuerto.getCapacidad_maxima());
        }

        // Verificar la capacidad en cada cambio de evento
        for (Evento evento : eventos) {
            int acumuladoActual = acumuladosPorAeropuerto.getOrDefault(evento.getAeropuerto(), 0) + evento.getCambio();
            acumuladosPorAeropuerto.put(evento.getAeropuerto(), acumuladoActual);

            // Comprueba si la capacidad se ha excedido
            if (acumuladoActual > capacidadMaximaPorAeropuerto.get(evento.getAeropuerto())) {
                return false; // Retorna falso si se excede la capacidad en algún momento
            }
        }

        return true; // Retorna verdadero si nunca se excede la capacidad
    }

    public boolean verificar_capacidad_aeropuertos(ArrayList<Paquete> paquetes,
            ArrayList<PlanRuta> planRutas, ArrayList<Aeropuerto> aeropuertos) {
        Funciones funciones = new Funciones();
        ArrayList<RegistroAlmacenamiento> registros = funciones.crearRegistrosAlmacenamiento(paquetes, planRutas,
                aeropuertos);
        return funciones.verificar_capacidad(registros, aeropuertos);
    }

    public EstadoAlmacen obtenerEstadoAlmacen(ArrayList<Paquete> paquetes,
            ArrayList<PlanRuta> planRutas, ArrayList<Aeropuerto> aeropuertos) {
        Funciones funciones = new Funciones();
        ArrayList<RegistroAlmacenamiento> registros = funciones.crearRegistrosAlmacenamiento(paquetes, planRutas,
                aeropuertos);
        if (funciones.verificar_capacidad(registros, aeropuertos)) {

            HashMap<Aeropuerto, HashMap<Date, Integer>> eventos_acumulados = new HashMap<Aeropuerto, HashMap<Date, Integer>>();
            HashMap<Aeropuerto, HashMap<Date, Integer>> acumulado_aeropuerto = new HashMap<Aeropuerto, HashMap<Date, Integer>>();
            // Crea eventos de inicio y fin
            for (RegistroAlmacenamiento registro : registros) {
                eventos_acumulados.putIfAbsent(registro.getAeropuerto(), new HashMap<Date, Integer>());
                eventos_acumulados.get(registro.getAeropuerto()).putIfAbsent(registro.getFechaInicio(), 0);
                eventos_acumulados.get(registro.getAeropuerto()).putIfAbsent(registro.getFechaFin(), 0);

                eventos_acumulados.get(registro.getAeropuerto()).put(registro.getFechaInicio(),
                        eventos_acumulados.get(registro.getAeropuerto()).get(registro.getFechaInicio()) + 1);
                eventos_acumulados.get(registro.getAeropuerto()).put(registro.getFechaFin(),
                        eventos_acumulados.get(registro.getAeropuerto()).get(registro.getFechaFin()) - 1);
            }

            for (Aeropuerto aeropuerto : eventos_acumulados.keySet()) {
                HashMap<Date, Integer> registrosPorFecha = eventos_acumulados.get(aeropuerto);
                ArrayList<Date> fechasOrdenadas = new ArrayList<>(registrosPorFecha.keySet());
                Collections.sort(fechasOrdenadas);

                HashMap<Date, Integer> acumuladosPorFecha = new HashMap<>();
                int acumulado = 0;

                for (Date fecha : fechasOrdenadas) {
                    acumulado += registrosPorFecha.get(fecha);
                    acumuladosPorFecha.put(fecha, acumulado);
                }

                acumulado_aeropuerto.put(aeropuerto, acumuladosPorFecha);
            }

            return new EstadoAlmacen(acumulado_aeropuerto);

        }

        return null;
    }
}