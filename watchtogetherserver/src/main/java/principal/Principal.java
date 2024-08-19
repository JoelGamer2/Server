package principal;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import paquetes.tipos.Lista;
import paquetes.tipos.PacketPause;
import paquetes.tipos.PacketPing;
import paquetes.tipos.PacketSetMedia;
import paquetes.tipos.PacketSetTime;
import paquetes.tipos.PacketStartMedia;
import reproductor.Reproductor;

public class Principal {

	public static ConcurrentHashMap<Reproductor, Reproductor> reproductores = new ConcurrentHashMap<>();
	private static final int PUERTO = 1234;
	private static ExecutorService executor = Executors.newFixedThreadPool(100);
	public static Lista listaDeReproduccion;
	public static void main(String[] args) {
		executor.submit(() -> {

			try {
				ServerSocket serverSocket = new ServerSocket(PUERTO);
				System.out.println("Servidor Utena comparte iniciado en el puerto:" + PUERTO);
				while (serverSocket.isBound()) {

					Socket clientSocket = serverSocket.accept();
					Reproductor nuevoJugador = new Reproductor(clientSocket);
					reproductores.put(nuevoJugador, nuevoJugador);
					System.out.println("Reproductor conectado" + reproductores);

				}
				serverSocket.close();
				System.exit(-1);
			} catch (IOException e) {
				e.printStackTrace();
			}

		});

		executor.submit(() -> {

			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			while (true) {
				try {
					String linea = scanner.nextLine();
					if (linea.equals("pause")) {
						sendBroadcast(new PacketPause(true).toString(), null);
						if(listaDeReproduccion != null)
							listaDeReproduccion.pause(null, true);
					} else if (linea.equals("play")) {
						sendBroadcast(new PacketPause(false).toString(), null);
						if(listaDeReproduccion != null)
							listaDeReproduccion.pause(null, false);
					} else if (linea.contains("settime")) {
						long time = Long.parseLong(linea.split(" ")[1]) * 1000;
						sendBroadcast(new PacketSetTime(time).toString(), null);
						if(listaDeReproduccion != null)
							listaDeReproduccion.setTime(time);
					} else if (linea.contains("setlista")) {
						String[] comando = linea.split(" ");
						File lista = new File(comando[1]);
						if (lista.exists()) {
							sendBroadcast(new PacketPing().toString(), null);
							listaDeReproduccion = new Lista(new ArrayList<Reproductor>(reproductores.values()));
							listaDeReproduccion.setLista(lista);
							System.out.println("Lista puesta");

						}else {
							System.out.println(String.format("La lista con el nombre %s no existe", comando[1]));
						}

					}else if(linea.contains("lista")){
						if(listaDeReproduccion != null)
							listaDeReproduccion.parseCommand(linea);
					}

					else {
						String[] comando = linea.split(" ");
						listaDeReproduccion = new Lista(new ArrayList<Reproductor>(reproductores.values()));
						listaDeReproduccion.setDelay(comando.length > 1 ? Integer.parseInt(comando[1].trim()) : 15);
						listaDeReproduccion.agregar(comando[0]);
						listaDeReproduccion.pasarAlSiguiente();

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public static void setMediaSolo(String path, int delay, Reproductor reproductor, long time, boolean pausa) {
		

		reproductor.enviarPaquete(new PacketSetMedia(path).toString());

		Util.setTime(delay, new TimerTask() {
			
			@Override
			public void run() {
				reproductor.enviarPaquete(new PacketStartMedia().toString());
				if(pausa) 
					reproductor.enviarPaquete(new PacketPause(true).toString());
				reproductor.enviarPaquete(new PacketSetTime(pausa ? time : time+(delay*1000)).toString());

				
			}
		});
		
	}
	
	public static void setMedia(String path, int delay) {

		sendBroadcast(new PacketSetMedia(path).toString(), null);

		Util.setTime(delay, new TimerTask() {
			
			@Override
			public void run() {
				sendBroadcast(new PacketStartMedia().toString(), null);
				
			}
		});
	}

	public static void sendBroadcast(String data, Reproductor sendBy) {
		for (Reproductor re : reproductores.values())
			if (!re.equals(sendBy))
				re.enviarPaquete(data);
	}



}
