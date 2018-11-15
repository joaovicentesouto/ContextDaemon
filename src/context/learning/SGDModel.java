package context.learning;

import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.functions.SGD;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class SGDModel implements LearningModel
{
//! ================== Attributes ==================

	private SGD _classifier;
	private boolean trained = false;

//! ================== Constructor ==================
	
	public SGDModel() {
		_classifier = new SGD();
	}
	
	public SGDModel(Instances data) throws Exception {
		_classifier = new SGD();
		relearning(data);
	}

//! ================== Learning Functions ==================

	@Override
	public void update(Instances data) throws Exception {
		if (!trained) {
			relearning(data);
			return;
		}
		
		for (Instance i : data) {
			synchronized (this) {
				((UpdateableClassifier)_classifier).updateClassifier(i);
			}
		}
	}
	
	@Override
	public void update(Instance data) throws Exception {
		if (!trained)
			return;

		synchronized (this) {
			((UpdateableClassifier)_classifier).updateClassifier(data);
		}
	}

	@Override
	public void relearning(Instances data) throws Exception {	
		SGD temp;
		
		synchronized (this) {
			temp = (SGD) SGD.makeCopy(_classifier);
		}
		
		try {
			temp.setLossFunction(new SelectedTag(SGD.SQUAREDLOSS, SGD.TAGS_SELECTION));
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

//! ================== Predict Functions ==================

	@Override
	public void predict(Instance data) throws Exception {
		if (trained) {	
			synchronized (this) {
				_classifier.classifyInstance(data);
			}
		}
	}

}
