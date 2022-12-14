package artemis.kdlyextras;

import com.unascribed.lib39.dessicant.api.DessicantControl;

public class KdlyContentInit implements Runnable {
	@Override
	public void run() {
		DessicantControl.optIn("kdlycontent");
	}
}
