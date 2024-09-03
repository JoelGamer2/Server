package paquetes.tipos;

import org.json.JSONObject;

import principal.Principal;
import reproductor.Reproductor;

public class PacketPause extends Packet {

	private boolean pausa;
	
	
	public PacketPause() {
		
	}
	
	public PacketPause(boolean pausa) {
		this.pausa = pausa;
	}
	
	@Override
	public void handleData(JSONObject data, Reproductor reproductor) {
	
		Principal.sendBroadcast(data.toString(), reproductor);
		if(Principal.listaDeReproduccion != null)
			Principal.listaDeReproduccion.pause(reproductor, data.getBoolean("pause"));
	}

	
	@Override
	public String toString() {
		JSONObject prueba = new JSONObject();
		prueba.put("tipo", "pause");
		prueba.put("pause", pausa);
		return prueba.toString();
	} 
	
}
