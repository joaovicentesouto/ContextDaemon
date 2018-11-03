package context;

import context.CacheController;

public class ControlCacheController<Data> implements CacheController<Data> {

	@Override
	public void update(Data data) {
		// TODO Auto-generated method stub
		System.out.println("Control: " + data);
	}

}
