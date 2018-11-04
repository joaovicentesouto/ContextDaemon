package context;

import context.CacheController;

public class ControlCacheController implements CacheController {

	@Override
	public void update(SmartData data) {
		// TODO Auto-generated method stub
		System.out.println("Control: " + data.toString());
	}

}
