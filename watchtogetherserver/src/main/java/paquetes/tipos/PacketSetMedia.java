package paquetes.tipos;

import org.json.JSONObject;

import reproductor.Reproductor;

public class PacketSetMedia extends Packet {

private String path;
	
	
	public PacketSetMedia() {
		
	}
	
	public PacketSetMedia(String path) {
		this.path = path;
	}
	
	
	@Override
	public void handleData(JSONObject data, Reproductor reproductor) {
	
		reproductor.viendo = true;
		System.out.println("[Reproductor] Medio Puesto!");
	}

	
	@Override
	public String toString() {
		JSONObject prueba = new JSONObject();
		prueba.put("tipo", "setmedia");
		prueba.put("path", "http://%ip%:8080/"+path);
		return prueba.toString();
	} 
	
}
