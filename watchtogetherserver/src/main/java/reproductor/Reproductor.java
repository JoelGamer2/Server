package reproductor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.TimerTask;

import paquetes.PacketDispacher;
import principal.Principal;
import principal.Util;

public class Reproductor {

	private Socket cliente;
	private DataOutputStream out;
	private DataInputStream in;
	private static PacketDispacher packetDispacher;
	private ThreadLeer leer;
	public boolean viendo;
	private Rsa criptografia = new Rsa();
	public Reproductor(Socket socket) {
		if(Reproductor.packetDispacher == null)
			Reproductor.packetDispacher = new PacketDispacher();
		this.cliente = socket;
		try {
			out = new DataOutputStream(cliente.getOutputStream());
			in = new DataInputStream(cliente.getInputStream());
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		leer =new ThreadLeer();
		leer.start();
		if(Principal.listaDeReproduccion != null) {
			Util.setTime(3, new TimerTask() {
				
				@Override
				public void run() {
					if(Principal.listaDeReproduccion != null)
						Principal.listaDeReproduccion.agregarReproductor(Reproductor.this);
					
				}
			});
		}
	}

	
	public void enviarPaquete(String data) {
		try {

			out.writeUTF(criptografia.estaLaClavePublicaDelCliente() ? criptografia.encriptarConClavePublicaCliente(data+"^") : data);
			out.flush();
		} catch (IOException e) {
			desconectar();
		}
	}

	@SuppressWarnings("removal")
	public void desconectar() {
		
		Principal.reproductores.remove(this);
		if(Principal.listaDeReproduccion != null)
			Principal.listaDeReproduccion.borrarReproductor(this);
		try {
			System.out.println("Cliente desconectado.");
			out.close();
			in.close();
			cliente.close();
			leer.stop();

		} catch (Exception e) {
			
		}

	}

	public class ThreadLeer extends Thread {

		@Override
		public void run() {
			super.run();
			try {
				cliente.setSoTimeout(70*1000);
				enviarPaquete(String.format("publicKey:%s",criptografia.getPublicKeyEncoded()));
				while (!cliente.isClosed()) {
					
					String paqueteRecibido = in.readUTF();
					if(paqueteRecibido.contains("publicKey:")) {
						criptografia.cargarClavePublica(paqueteRecibido.split(":")[1]);
						continue;
					}
					
					if(criptografia.estaLaClavePublicaDelCliente()) {
						paqueteRecibido = criptografia.desencriptar(paqueteRecibido);
						if(paqueteRecibido.charAt(0) == '{' && paqueteRecibido.charAt(paqueteRecibido.length()-1) == '^')
							Reproductor.packetDispacher.dispachPacket(paqueteRecibido.replace("^", ""), Reproductor.this);
						else
							desconectar();
					}
			//	System.out.println("[cliente] " + paqueteRecibido);
					
				}
				desconectar();
			} catch (Exception e) {
				desconectar();
			}
		}
	}

}
