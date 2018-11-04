package context;

import java.util.Iterator;
import java.util.List;

import weka.classifiers.UpdateableClassifier;
import weka.classifiers.functions.SGD;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class MachineLearning {
	
	private SGD _classifier;
	
	public MachineLearning()
	{
		_classifier = null;
	}
	
	public MachineLearning(Instances data) throws Exception
	{
		_classifier = new SGD();
		_classifier.setLossFunction(new SelectedTag(SGD.HUBER, SGD.TAGS_SELECTION));
		_classifier.buildClassifier(data);
		_classifier.getOptions();
	}
	
	public synchronized void update(List<Instance> data) throws Exception
	{	
		for (Instance i : data)
			((UpdateableClassifier)_classifier).updateClassifier(i);
	}
	
	public synchronized void predict(Instance data) throws Exception {
		_classifier.classifyInstance(data);
	}

}
