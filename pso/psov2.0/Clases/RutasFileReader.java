package Clases;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RutasFileReader {

    public static HashMap<String, ArrayList<PlanRuta>> readRutasFiles(String directoryPath, HashMap<Integer, Vuelo> vuelos_map) {
        HashMap<String, ArrayList<PlanRuta>> rutas = new HashMap<>();

        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Invalid directory path: " + directoryPath);
            return rutas;
        }

        File[] files = directory.listFiles((dir, name) -> name.startsWith("rutas_") && name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.err.println("No rutas files found in directory: " + directoryPath);
            return rutas;
        }

        for (File file : files) {
            String fileName = file.getName();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    String key = parts[0];
                    String[] routeStrings = parts[1].split(",");
                    ArrayList<PlanRuta> planRutas = new ArrayList<>();
                    PlanRuta planRuta = null;
                    for (int i = 0; i < routeStrings.length; i++) {
                        String route = routeStrings[i];
                        if (route.startsWith("R")) {
                            if (planRuta != null) {
                                planRutas.add(planRuta);
                            }
                            /*else{
                                System.out.println(routeStrings[i-1]);
                            }*/
                            planRuta = new PlanRuta();
                            //planRuta.setCodigo(route);
                            planRuta.setVuelos(new ArrayList<>());
                        } else {
                            //Vuelo vuelo = new Vuelo();
                            Vuelo vuelo = vuelos_map.get(Integer.parseInt(route));
                            //vuelo.setId(Integer.parseInt(route));
                            planRuta.getVuelos().add(vuelo);
                        }
                    }
                    if (planRuta.getVuelos() != null) {
                        planRutas.add(planRuta);
                    }
                    rutas.put(key, planRutas);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return rutas;
    }
}


