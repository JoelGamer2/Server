package principal;

import java.util.Timer;
import java.util.TimerTask;

public class Util {

	public static void setTime(int segundos, TimerTask task) {
		

		Timer timer = new Timer();


		timer.schedule(task, segundos * 1000);
	}
	
	
}
