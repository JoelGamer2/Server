package paquetes.tipos;

import org.json.JSONObject;

import reproductor.Reproductor;

public interface IPacket {
	 public void handleData(JSONObject data, Reproductor reproductor);
}
