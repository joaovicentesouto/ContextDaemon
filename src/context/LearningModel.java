package context;

import weka.core.Instance;
import weka.core.Instances;

public interface LearningModel {
	
	public void update(Instances data) throws Exception;
	
	public void relearning(Instances data) throws Exception;
	
	public void predict(Instance data) throws Exception;

}
