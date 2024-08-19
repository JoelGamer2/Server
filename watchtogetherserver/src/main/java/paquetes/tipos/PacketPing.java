package paquetes.tipos;

import org.json.JSONObject;

import reproductor.Reproductor;

public class PacketPing extends Packet {

	
	
	public PacketPing() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public void handleData(JSONObject data, Reproductor reproductor) {
	
		reproductor.enviarPaquete(data.toString());
		
	}

	
	@Override
	public String toString() {
		JSONObject prueba = new JSONObject();
		prueba.put("tipo", "ping");
		return prueba.toString();
	} 
	
}
