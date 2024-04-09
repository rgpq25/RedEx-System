import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Clases.Aeropuerto;
import Clases.Paquete;
import Clases.PlanVuelo;
import Clases.Vuelo;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Funciones {
    /*
	public leerAeropuertos(Aeropuerto[] aeropuertos) {
		this.position = position;
		this.velocity = velocity; 
	}
     */
	public Funciones() {
	}

	public Aeropuerto[] leerAeropuertos() {
		List<Aeropuerto> aeropuertos_list = new ArrayList<Aeropuerto>();
		Aeropuerto[] aeropuertos = null;
		try {
            // Define the file path
            File file = new File("entradaOld/aeropuertos.csv");

            // Create a Scanner object to read the file
            Scanner scanner = new Scanner(file);

            // Set the delimiter to ","
            scanner.useDelimiter(",");

            // Read and process each line
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // Split the line by comma
                String[] parts = line.split(",");

                // Assuming each line contains two parts
				Aeropuerto aeropuerto = new Aeropuerto();
				aeropuerto.setId(parts[0].trim());
				aeropuerto.setCapacidad_maxima(Integer.parseInt(parts[1].trim()));
				aeropuerto.setCapacidad_utilizada(Integer.parseInt(parts[2].trim()));
				aeropuertos_list.add(aeropuerto);
				/*System.out.println("Airport Code: " + aeropuerto.getId() + ", Value: " + aeropuerto.getCapacidad_maxima() + ", Additional Value: " + aeropuerto.getCapacidad_utilizada());*/
            }

            // Close the scanner
            scanner.close();
			//list to array
			aeropuertos = aeropuertos_list.toArray(new Aeropuerto[aeropuertos_list.size()]);
			
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
		return aeropuertos;
	}

	public Paquete[] leerPaquetes() {
		List<Paquete> paquetes_list = new ArrayList<Paquete>();
		Paquete[] paquetes = null;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		try {
            // Define the file path
            File file = new File("entradaOld/paquetes.csv");

            // Create a Scanner object to read the file
            Scanner scanner = new Scanner(file);

            // Set the delimiter to ","
            scanner.useDelimiter(",");

            // Read and process each line
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // Split the line by comma
                String[] parts = line.split(",");

                // Assuming each line contains two parts
				Paquete paquete = new Paquete();
				paquete.setFecha_recepcion(dateFormat.parse(parts[0].trim()));
				paquete.setId_ciudad_almacen(parts[1].trim());
				paquete.setId_ciudad_destino(parts[2].trim());
				paquete.setFecha_maxima_entrega(dateFormat.parse(parts[3].trim()));
				paquetes_list.add(paquete);
				/*System.out.println(paquete.getFecha_recepcion() + ", " + paquete.getId_ciudad_almacen() + ", " + paquete.getId_ciudad_destino() + ", " + paquete.getFecha_maxima_entrega());*/
            }

            // Close the scanner
            scanner.close();
			//list to array
			paquetes = paquetes_list.toArray(new Paquete[paquetes_list.size()]);
			
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return paquetes;
	}

	public Vuelo[] leerVuelos() {
		List<Vuelo> vuelos_list = new ArrayList<Vuelo>();
		Vuelo[] vuelos = null;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		try {
            // Define the file path
            File file = new File("entradaOld/vuelos.csv");

            // Create a Scanner object to read the file
            Scanner scanner = new Scanner(file);

            // Set the delimiter to ","
            scanner.useDelimiter(",");

            // Read and process each line
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // Split the line by comma
                String[] parts = line.split(",");

				Vuelo vuelo = new Vuelo();
				vuelo.setPlan_vuelo(new PlanVuelo());
				vuelo.getPlan_vuelo().setId_ubicacion_origen(parts[0].trim());
				vuelo.getPlan_vuelo().setId_ubicacion_destino(parts[1].trim());
				vuelo.setFecha_salida(dateFormat.parse(parts[2].trim()));
				vuelo.setFecha_llegada(dateFormat.parse(parts[3].trim()));
				vuelo.getPlan_vuelo().setCapacidad_maxima(Integer.parseInt(parts[4].trim()));

                vuelos_list.add(vuelo);

				/*System.out.println(vuelo.getPlan_vuelo().getId_ubicacion_origen() + ", " + vuelo.getPlan_vuelo().getId_ubicacion_destino() + ", " + vuelo.getFecha_salida() + ", " + vuelo.getFecha_llegada() + ", " + vuelo.getPlan_vuelo().getCapacidad_maxima());*/
            }

            // Close the scanner
            scanner.close();
			//list to array
			vuelos = vuelos_list.toArray(new Vuelo[vuelos_list.size()]);
			
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        } catch (ParseException e) {
			e.printStackTrace();
		}
		return vuelos;
	}

    public void ordernarPaquetes(Paquete []paquetes){
        //ordenar array de paquetes por fecha de recepcion
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

    public void ordernarVuelos(Vuelo []vuelos){
        //ordenar array de vuelos por fecha de salida
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
}