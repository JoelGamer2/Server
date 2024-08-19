package paquetes.tipos;

import org.json.JSONObject;

import principal.Principal;
import reproductor.Reproductor;

public class PacketSetTime extends Packet {

private Long time;
	
	
	public PacketSetTime() {
		
	}
	
	public PacketSetTime(Long time) {
		this.time = time;
	}
	
	
	@Override
	public void handleData(JSONObject data, Reproductor reproductor) {
	
		Principal.sendBroadcast(data.toString(), reproductor);
		if(Principal.listaDeReproduccion != null)
			Principal.listaDeReproduccion.setTime(data.getLong("time"));
		
	}

	
	@Override
	public String toString() {
		JSONObject prueba = new JSONObject();
		prueba.put("tipo", "settime");
		prueba.put("time", time);
		return prueba.toString();
	} 
	
}
