package paquetes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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

	private Map<String, Supplier<Packet>> paquetes = new HashMap<>();

	public PacketDispacher() {
		registerPackets();
	}

	private void registerPacket(String name, Supplier<Packet> packetClass) {
		paquetes.put(name, packetClass);
	}

	private void registerPackets() {
		//AQUI SE REGISTRAN LOS PAQUETES LOS CUALES PUEDE ENVIAR EL CLIENTE, LOS QUE ENVIA EL SERVIDOR NO.
		registerPacket("pause", PacketPause::new);
		registerPacket("settime", PacketSetTime::new);
		registerPacket("ping", PacketPing::new);
		registerPacket("finished", PacketTerminado::new);
		registerPacket("setmedia", PacketSetMedia::new);
		
	}

	public void dispachPacket(String data, Reproductor jugador) {
		try {
			JSONObject jsonData = new JSONObject(data);
			String tipo = jsonData.getString("tipo");
			if (paquetes.containsKey(tipo)) {
				Packet paquete = paquetes.get(tipo).get();
				if (paquete instanceof IPacket)
					((IPacket) paquete).handleData(jsonData,jugador);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
