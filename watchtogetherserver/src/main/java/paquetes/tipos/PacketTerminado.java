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
	}
	
}
