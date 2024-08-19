package paquetes;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import paquetes.tipos.IPacket;
import paquetes.tipos.Packet;
import paquetes.tipos.PacketPause;
import paquetes.tipos.PacketPing;
import paquetes.tipos.PacketSetMedia;
import paquetes.tipos.PacketSetTime;
import paquetes.tipos.PacketTerminado;
import reproductor.Reproductor;

public class PacketDispacher {

	private Map<String, Class<? extends Packet>> paquetes = new HashMap<>();

	public PacketDispacher() {
		registerPackets();
	}

	private void registerPacket(String name, Class<? extends Packet> packetClass) {
		paquetes.put(name, packetClass);
	}

	private void registerPackets() {
		//AQUI SE REGISTRAN LOS PAQUETES LOS CUALES PUEDE ENVIAR EL CLIENTE, LOS QUE ENVIA EL SERVIDOR NO.
		registerPacket("pause", PacketPause.class);
		registerPacket("settime", PacketSetTime.class);
		registerPacket("ping", PacketPing.class);
		registerPacket("finished", PacketTerminado.class);
		registerPacket("setmedia", PacketSetMedia.class);
		
	}

	public void dispachPacket(String data, Reproductor jugador) {
		try {
			JSONObject jsonData = new JSONObject(data);
			Class<? extends Packet> packetClass = paquetes.get(jsonData.getString("tipo"));
			if (packetClass != null) {
				Packet packet = packetClass.getDeclaredConstructor().newInstance();
				if (packet instanceof IPacket)
					((IPacket) packet).handleData(jsonData,jugador);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
