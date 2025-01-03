package paquetes.tipos;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import principal.Principal;
import reproductor.Reproductor;

public class Lista {

	private List<Reproductor> reproductores;
	private LinkedList<String> medias;
	private ConcurrentHashMap<Reproductor, Reproductor> viendo = new ConcurrentHashMap<Reproductor, Reproductor>();
	private int delayEntreMedias = 35;
	private String reproduciendo = "";
	private long timeStartedMedia = 0L;
	private long timeToTalWithSkip = 0L;
	private Timer timerEpisodioCargando;
	private boolean bucle = false;
	
	public Lista(List<Reproductor> reproductores) {
		this.reproductores = reproductores;
		this.medias = new LinkedList<String>();
		for (Reproductor re : reproductores)
			viendo.put(re, re);
	}

	public void agregar(String path) {
		if(path.contains(".txt")) {
			File subLista = new File(path);
			if(subLista.exists())
				setLista(subLista, reproduciendo == null);
			return;
		}
		
		
		if (!path.startsWith("+") && !medias.contains(path))
			medias.add(path);
		
		
	}

	public void borrar(String path) {
		if (medias.contains(path))
			medias.remove(path);
	}

	public void pasarAlSiguiente() {
		if (!medias.isEmpty() || bucle) {
			//Si no esta el bucle asignamos el nuevo elemento, este se hace para que si esta el bucle se reproduzca lo mismo.
			if(!bucle) {
				reproduciendo = medias.getFirst();
				medias.removeFirst();
			}
			timerEpisodioCargando = Principal.setMedia(reproduciendo, delayEntreMedias);
			timeStartedMedia = System.currentTimeMillis() + (delayEntreMedias * 1000);
			timeToTalWithSkip = 0L;
			for (Reproductor re : reproductores)
				viendo.put(re, re);
		}
	}

	public synchronized void termino(Reproductor reproduc) {
		if (viendo.contains(reproduc)) {

			if (medias.isEmpty() && !bucle) //si esta el bucle activado no importa que no haya mas medias ya que se tiene que volver a reproducir.
				Principal.listaDeReproduccion = null;

			viendo.remove(reproduc);
			if (viendo.size() == 0) {
				timeToTalWithSkip = 0L;
				pasarAlSiguiente();
			}

		}

	}

	public void setDelay(int delay) {
		if (delay > 0)
			this.delayEntreMedias = delay;
	}

	public void setLista(File lista, boolean siguiente) {
		try (Scanner scanner = new Scanner(lista)) {
			while (scanner.hasNextLine())
				agregar(scanner.nextLine().trim());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(siguiente)
			pasarAlSiguiente();
	}

	public String getViendo() {
		return reproduciendo;
	}

	public synchronized void agregarReproductor(Reproductor reproductor) {
		if (!viendo.contains(reproductor) && !reproductores.contains(reproductor)) {
			long time = timeToTalWithSkip != 0L ? timeToTalWithSkip + (System.currentTimeMillis() - timeStartedMedia)
					: (System.currentTimeMillis() - timeStartedMedia);
			if (pausado)
				time = timeToTalWithSkip;
			viendo.put(reproductor, reproductor);
			reproductores.add(reproductor);
			Principal.setMediaSolo(reproduciendo, 4, reproductor, time, pausado);

		}
	}

	public synchronized void borrarReproductor(Reproductor reproductor) {
		if (viendo.contains(reproductor)) {
			viendo.remove(reproductor);
			if(viendo.size() == 0)
				cancelarLista();
		}

		if (reproductores.contains(reproductor))
			reproductores.remove(reproductor);

	}

	public void parseCommand(String linea) {
		String[] args = linea.split(" ");
		if (args.length > 1) {

			if (args[1].equals("add") && args.length > 2) {
				agregar(args[2]);
				System.out.println("[Lista] fichero " + args[2] + " agregado con exito");
			}
			else if (args[1].equals("remove") && args.length > 2) {
				borrar(args[2]);
				System.out.println("[Lista] fichero " + args[2] + " borrado con exito");
			}
			else if (args[1].equals("skip")) {
				System.out.println("[Lista] fichero " + reproduciendo + " saltado con exito");
				pasarAlSiguiente();
			}
			else if (args[1].equals("delay") && args.length > 2) {
				setDelay(Integer.parseInt(args[2]));
				System.out.println("[Lista] Delay entre medias establecido en: " + args[2]);
			}
			else if (args[1].equals("stop")) {
				cancelarLista();
			}else if(args[1].equals("list"))
				System.out.println("[Lista] Quedan los siguientes ficheros por reproducir:\n"+medias);
			else if(args[1].equals("bucle") && args.length > 2) {
				bucle = Boolean.parseBoolean(args[2]);
				System.out.println("[Lista] Bucle ajustado a " + bucle + " y se reproducira constantemente " + reproduciendo);
				
			}
		}

	}

	private synchronized void cancelarLista() {
		medias.clear();
		reproductores.clear();
		for(Reproductor re : viendo.keySet()) {
			re.enviarPaquete(new PacketStartMedia().toString()); //Intento de arreglo bug -> si el medio no esta iniciado no se cancelaba
			re.enviarPaquete(new PacketTerminado().toString());
		}
		if(timerEpisodioCargando != null) {
			timerEpisodioCargando.cancel();
			timerEpisodioCargando = null;
		}
		Principal.listaDeReproduccion = null;
		System.out.println("[Lista] lista cancelada y media cancelado");
	}
	
	public synchronized void setTime(long time) {
		this.timeStartedMedia = System.currentTimeMillis();
		this.timeToTalWithSkip = time;

		if (pausado) {
			tiempoPausa = System.currentTimeMillis();
		}
	}

	boolean pausado = false;
	long tiempoPausa = 0L;

	public synchronized void pause(Reproductor reproductor, boolean pausar) {
		if (reproductor == null || viendo.contains(reproductor)) {
			if (pausar) {
				if (!pausado) {
					pausado = true;
					tiempoPausa = System.currentTimeMillis();

				}
			} else {
				if (pausado) {
					pausado = false;
					long tiempoTranscurridoDurantePausa = System.currentTimeMillis() - tiempoPausa;
					timeStartedMedia += tiempoTranscurridoDurantePausa;

				}
			}
		}
	}
}
