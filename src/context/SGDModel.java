package context;

import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.functions.SGD;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class SGDModel implements LearningModel
{
	private SGD _classifier;
	private boolean trained = false;
	
	public SGDModel() {
		_classifier = new SGD();
	}
	
	public SGDModel(Instances data) throws Exception {
		_classifier = new SGD();
		relearning(data);
	}

	@Override
	public void update(Instances data) throws Exception
	{
		if (!trained) {
			relearning(data);
			return;
		}

		SGD temp;
		
		synchronized (this) {
			temp = (SGD) SGD.makeCopy(_classifier);
		}
		
		for (Instance i : data) {
			synchronized (this) {
				((UpdateableClassifier)temp).updateClassifier(i);
			}
		}
		
		synchronized (this) {
			_classifier = temp;
		}
	}

	@Override
	public void relearning(Instances data) throws Exception
	{	
		SGD temp;
		
		synchronized (this) {
			temp = (SGD) SGD.makeCopy(_classifier);
		}
		
		try {
			temp.setLossFunction(new SelectedTag(SGD.HUBER, SGD.TAGS_SELECTION));
			temp.buildClassifier(data);
			//temp.getOptions();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("MachineLearning: Error on update!");
		}
		
		// Cria um objeto manipular a rede neural
		Evaluation eval = new Evaluation(data);

		// Atributo aux
		Random rand = new Random(1);

		// Número de camadas da rede neural
		int folds = 10;

		// Validação do modelo
		eval.crossValidateModel(temp, data, folds, rand);
		
		System.out.println("Erro: " + eval.errorRate());
		System.out.println("Erro: " + eval.toSummaryString());
		
		synchronized (this) {
			_classifier = temp;
			trained = true;
		}
	}

	@Override
	public void predict(Instance data) throws Exception
	{
		if (trained) {	
			synchronized (this) {
				_classifier.classifyInstance(data);
			}
		}
	}

}
