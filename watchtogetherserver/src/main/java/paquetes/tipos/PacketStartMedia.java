package paquetes.tipos;

import org.json.JSONObject;

import reproductor.Reproductor;

public class PacketStartMedia extends Packet {

	public PacketStartMedia() {
	}

	@Override
	public void handleData(JSONObject data, Reproductor reproductor) {

	}

	@Override
	public String toString() {
		JSONObject prueba = new JSONObject();
		prueba.put("tipo", "startmedia");
		return prueba.toString();
	}

}
