package com.pucp.pe.Clases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Funciones {

    public Funciones() {
    }

    public static int stringGmt2Int(String gmt) {
        return Integer.parseInt(gmt.replace("GMT", ""));
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static void printFormattedDate(Date date) {
        System.out.println(getFormattedDate(date));
    }

    public static Date parseDateString(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date convertTimeZone(Date date, String fromTimeZone, String toTimeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(fromTimeZone));
        long timeInMillis = date.getTime();

        int fromOffset = TimeZone.getTimeZone(fromTimeZone).getOffset(timeInMillis);
        int toOffset = TimeZone.getTimeZone(toTimeZone).getOffset(timeInMillis);

        int offsetDifference = toOffset - fromOffset;

        return new Date(timeInMillis + offsetDifference);
    }

    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    public static int getDifferenceInDays(Date startDate, Date endDate) {
        long diffInMillies = Math.abs(startDate.getTime() - endDate.getTime());
        int diffInDays = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diffInDays;
    }

    public static ArrayList<Aeropuerto> leerAeropuertos(String inputPath, HashMap<String, Ubicacion> ubicacionMap) {
        ArrayList<Aeropuerto> aeropuertos_list = new ArrayList<Aeropuerto>();

        try {
            File file = new File(inputPath + "/aeropuertos.csv");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                Aeropuerto aeropuerto = new Aeropuerto();

                aeropuerto.setUbicacion(ubicacionMap.get(parts[0].trim()));
                aeropuerto.setCapacidad_maxima(Integer.parseInt(parts[1].trim()));
                aeropuerto.setCapacidad_utilizada(0);
                aeropuertos_list.add(aeropuerto);
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return aeropuertos_list;
    }

    public static ArrayList<Paquete> leerPaquetes(String inputPath, HashMap<String, Ubicacion> ubicacionMap) {
        ArrayList<Paquete> paquetes_list = new ArrayList<Paquete>();

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

                Date fecha_recepcion_GMTOrigin = parseDateString(firstDateString);

                // lo llevamos a UTC
                Date fecha_recepcion_GMT0 = convertTimeZone(
                        fecha_recepcion_GMTOrigin,
                        paquete.getCiudadOrigen().getZonaHoraria(),
                        "UTC");

                Date fecha_maxima_entrega_GMTDestino = addDays(fecha_recepcion_GMTOrigin, 2); // aqui estaria en
                                                                                              // timezone de destino
                Date fecha_maxima_entrega_GMT0 = convertTimeZone(
                        fecha_maxima_entrega_GMTDestino,
                        paquete.getCiudadDestino().getZonaHoraria(),
                        "UTC");

                paquete.setFecha_recepcion(fecha_recepcion_GMT0);
                paquete.setFecha_maxima_entrega(fecha_maxima_entrega_GMT0);
                paquetes_list.add(paquete);

                id++;
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return paquetes_list;
    }

    public static ArrayList<Vuelo> leerVuelos(String inputPath, HashMap<String, Ubicacion> ubicacionMap) {
        ArrayList<Vuelo> vuelos_list = new ArrayList<Vuelo>();

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
                vuelo.getPlan_vuelo().setHora_ciudad_origen(parts[2].trim().split(" ")[1]);
                vuelo.getPlan_vuelo().setHora_ciudad_destino(parts[3].trim().split(" ")[1]);

                String dateSalida = parts[2].trim();
                String dateLlegada = parts[3].trim();
                Date fecha_salida = parseDateString(dateSalida);
                Date fecha_llegada = parseDateString(dateLlegada);
                vuelo.setFecha_salida(fecha_salida);
                vuelo.setFecha_llegada(fecha_llegada);

                vuelos_list.add(vuelo);
                id++;
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return vuelos_list;
    }

    public static ArrayList<PlanVuelo> leerPlanVuelos(String inputPath, HashMap<String, Ubicacion> ubicacionMap) {
        ArrayList<PlanVuelo> vuelos_list = new ArrayList<PlanVuelo>();

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

                PlanVuelo planVuelo = new PlanVuelo();
                planVuelo.setId(id);
                planVuelo.setCiudadOrigen(ubicacionMap.get(parts[0].trim()));
                planVuelo.setCiudadDestino(ubicacionMap.get(parts[1].trim()));
                planVuelo.setCapacidad_maxima(Integer.parseInt(parts[4].trim()));
                planVuelo.setHora_ciudad_origen(parts[2].trim());
                planVuelo.setHora_ciudad_destino(parts[3].trim());

                vuelos_list.add(planVuelo);
                id++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return vuelos_list;
    }

    public static void ordenarPaquetes(Paquete[] paquetes) {
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

    public static void ordenarVuelos(Vuelo[] vuelos) {
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

    public static Date generateRandomDateTime(Date startDate, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        // Obtener la diferencia de tiempo en milisegundos
        long startMillis = calendar.getTimeInMillis();
        long endMillis = (endDate != null) ? endDate.getTime() : System.currentTimeMillis();

        // Asegurarse de que la fecha de inicio no es posterior a la fecha de fin
        if (endDate != null && startMillis > endMillis) {
            throw new IllegalArgumentException("La fecha de inicio debe ser antes de la fecha de fin.");
        }

        // Generar un tiempo aleatorio en este rango
        Random random = new Random();
        long randomMillis = startMillis + (long) (random.nextDouble() * (endMillis - startMillis));

        // Configurar el tiempo aleatorio generado
        calendar.setTimeInMillis(randomMillis);
        return calendar.getTime();
    }

    public static ArrayList<Ubicacion> generarUbicaciones(int cantidad) {
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

    public static ArrayList<Aeropuerto> generarAeropuertos(ArrayList<Ubicacion> ubicaciones) {
        ArrayList<Aeropuerto> aeropuertos = new ArrayList<>();
        Random random = new Random();
        int capacidad = 100 + random.nextInt(401);
        for (Ubicacion ubicacion : ubicaciones) {
            aeropuertos.add(new Aeropuerto(ubicacion, 0, capacidad));
        }
        return aeropuertos;
    }

    public static ArrayList<Paquete> generarPaquetes(int n, List<Aeropuerto> aeropuertos, Date fechaInicio,
            Date fechaFin, String outputPath) {
        File csvFile = new File(outputPath + "/paquetes.csv");
        PrintWriter out;
        try {
            out = new PrintWriter(csvFile);

            ArrayList<Paquete> paquetes = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                Collections.shuffle(aeropuertos);
                Aeropuerto origen = aeropuertos.get(0);
                Aeropuerto destino = aeropuertos.get(1);
                Date fechaRecepcion = generateRandomDateTime(fechaInicio, fechaFin);
                paquetes.add(
                        new Paquete(origen.getUbicacion(), origen.getUbicacion(), destino.getUbicacion(),
                                fechaRecepcion));

                out.println(Funciones.getFormattedDate(fechaRecepcion) + "," + origen.getUbicacion().getId() + ","
                        + destino.getUbicacion().getId());
            }

            out.close();

            return paquetes;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<PlanVuelo> generarPlanesDeVuelo(ArrayList<Aeropuerto> aeropuertos,
            int repeticionesPorConexion, String outputPath) {
        File csvFile = new File(outputPath + "/vuelos.csv");
        PrintWriter out;

        try {
            out = new PrintWriter(csvFile);

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
                            int duracionHoras;
                            int duracionMinutos = 0 + random.nextInt(59);

                            int diferencia = destino.getUbicacion().diferenciaHoraria()
                                    - origen.getUbicacion().diferenciaHoraria();

                            if (origen.getUbicacion().getContinente()
                                    .contentEquals(destino.getUbicacion().getContinente())) {
                                duracionHoras = 2 + random.nextInt(10);
                            } else {
                                duracionHoras = 12 + random.nextInt(12);
                            }
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.HOUR_OF_DAY, horaSalidaHora);
                            cal.set(Calendar.MINUTE, horaSalidaMinuto);
                            cal.add(Calendar.HOUR_OF_DAY, duracionHoras - diferencia);
                            cal.add(Calendar.MINUTE, duracionMinutos);

                            String horaLlegada = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY),
                                    cal.get(Calendar.MINUTE));

                            int capacidad = 100 + random.nextInt(401);

                            horaSalida = horaSalida.concat(":00");
                            horaLlegada = horaLlegada.concat(":00");

                            PlanVuelo plan = new PlanVuelo(idPlan++, origen.getUbicacion(), destino.getUbicacion(),
                                    horaSalida, horaLlegada, capacidad);
                            planes.add(plan);

                            out.println(origen.getUbicacion().getId() + "," + destino.getUbicacion().getId() + ","
                                    + horaSalida + "," + horaLlegada + "," + capacidad);
                        }
                    }
                }
            }

            out.close();
            return planes;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
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

    /*
     * public static EstadoAlmacen obtenerEstadoAlmacen(ArrayList<Paquete> paquetes,
     * ArrayList<PlanRuta> planRutas, ArrayList<Aeropuerto> aeropuertos) {
     * Funciones funciones = new Funciones();
     * ArrayList<RegistroAlmacenamiento> registros =
     * funciones.crearRegistrosAlmacenamiento(paquetes, planRutas,
     * aeropuertos);
     * if (funciones.verificar_capacidad(registros, aeropuertos)) {
     * 
     * HashMap<Aeropuerto, HashMap<Date, Integer>> eventos_acumulados = new
     * HashMap<Aeropuerto, HashMap<Date, Integer>>();
     * HashMap<Aeropuerto, HashMap<Date, Integer>> acumulado_aeropuerto = new
     * HashMap<Aeropuerto, HashMap<Date, Integer>>();
     * // Crea eventos de inicio y fin
     * for (RegistroAlmacenamiento registro : registros) {
     * eventos_acumulados.putIfAbsent(registro.getAeropuerto(), new HashMap<Date,
     * Integer>());
     * eventos_acumulados.get(registro.getAeropuerto()).putIfAbsent(registro.
     * getFechaInicio(), 0);
     * eventos_acumulados.get(registro.getAeropuerto()).putIfAbsent(registro.
     * getFechaFin(), 0);
     * 
     * eventos_acumulados.get(registro.getAeropuerto()).put(registro.getFechaInicio(
     * ),
     * eventos_acumulados.get(registro.getAeropuerto()).get(registro.getFechaInicio(
     * )) + 1);
     * eventos_acumulados.get(registro.getAeropuerto()).put(registro.getFechaFin(),
     * eventos_acumulados.get(registro.getAeropuerto()).get(registro.getFechaFin())
     * - 1);
     * }
     * 
     * for (Aeropuerto aeropuerto : eventos_acumulados.keySet()) {
     * HashMap<Date, Integer> registrosPorFecha =
     * eventos_acumulados.get(aeropuerto);
     * ArrayList<Date> fechasOrdenadas = new
     * ArrayList<>(registrosPorFecha.keySet());
     * Collections.sort(fechasOrdenadas);
     * 
     * HashMap<Date, Integer> acumuladosPorFecha = new HashMap<>();
     * int acumulado = 0;
     * 
     * for (Date fecha : fechasOrdenadas) {
     * acumulado += registrosPorFecha.get(fecha);
     * acumuladosPorFecha.put(fecha, acumulado);
     * }
     * 
     * acumulado_aeropuerto.put(aeropuerto, acumuladosPorFecha);
     * }
     * 
     * return new EstadoAlmacen(acumulado_aeropuerto);
     * 
     * }
     * 
     * return null;
     * }
     */

}