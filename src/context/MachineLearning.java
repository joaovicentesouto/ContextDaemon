package context;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SGD;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class MachineLearning {
	
//	private SGD _classifier;
	
	// cria um objeto para fazer a rede neural
	MultilayerPerceptron _classifier;
	
	public MachineLearning() {
		_classifier = new MultilayerPerceptron();
	}
	
	public MachineLearning(Instances data) throws Exception {
		_classifier = new MultilayerPerceptron();
		init(data);
	}
	
	public void init(Instances data) throws Exception {
//		_classifier = new SGD();
//		this._classifier.setLossFunction(new SelectedTag(SGD.SQUAREDLOSS, SGD.TAGS_SELECTION));
//		this._classifier.buildClassifier(data);
//		this._classifier.getOptions();
		
		//constroi e treina a rede neural para os dados de treinamento fornecidos
		this._classifier.setHiddenLayers("0");
		try {
			this._classifier.buildClassifier(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// cria um objeto manipular a rede neural
		Evaluation eval = new Evaluation(data);

		// atributo aux
		Random rand = new Random(1);

		// número de camadas da rede neural
		int folds = 10;

		// validação do modelo
		eval.crossValidateModel(_classifier, data, folds, rand);
		
		System.out.println("Erro: " + eval.errorRate());
		System.out.println("Erro: " + eval.toSummaryString());
	}
	
	public synchronized void update(Instances data) throws Exception
	{
		//! SGD
//		if (_classifier == null)
//			init(data);
//		else
//			for (Instance i : data)
//				((UpdateableClassifier)_classifier).updateClassifier(i);
		
		//! Multilayer
		init(data);
	}
	
	public synchronized void predict(Instance data) throws Exception {
		this._classifier.classifyInstance(data);
	}

}
