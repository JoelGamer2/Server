package paquetes.tipos;

import org.json.JSONObject;

import principal.Principal;
import reproductor.Reproductor;

public class PacketTerminado extends Packet {
	
	
	public PacketTerminado() {
		
	}
	
	
	
	@Override
	public void handleData(JSONObject data, Reproductor reproductor) {
		reproductor.viendo = false;
		if(Principal.listaDeReproduccion != null)
			Principal.listaDeReproduccion.termino(reproductor);
		System.out.println("[Reproductor] Episodio Terminado!");
	}
	
	
	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		json.put("tipo", "terminated");
		return json.toString();
	} 
	
}
