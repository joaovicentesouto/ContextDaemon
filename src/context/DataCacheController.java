package context;

import context.CacheController;

public class DataCacheController<Data> implements CacheController<Data> {

	@Override
	public void update(Data data) {
		// TODO Auto-generated method stub
		System.out.println("Data: " + data);
	}

}
