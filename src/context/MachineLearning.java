package context;

import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;

public class MachineLearning {

	MultilayerPerceptron _classifier;
	
	public MachineLearning() {
		_classifier = new MultilayerPerceptron();
	}
	
	public MachineLearning(Instances data) throws Exception {
		_classifier = new MultilayerPerceptron();
		init(data);
	}
	
	public void init(Instances data) throws Exception {
		update(data);
	}
	
	public synchronized void update(Instances data) throws Exception
	{
		//! Sem camadas intermediárias
		this._classifier.setHiddenLayers("0");
		
		try {
			this._classifier.buildClassifier(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Cria um objeto manipular a rede neural
		Evaluation eval = new Evaluation(data);

		// Atributo aux
		Random rand = new Random(1);

		// Número de camadas da rede neural
		int folds = 10;

		// Validação do modelo
		eval.crossValidateModel(_classifier, data, folds, rand);
		
		System.out.println("Erro: " + eval.errorRate());
		System.out.println("Erro: " + eval.toSummaryString());
	}
	
	public synchronized void predict(Instance data) throws Exception {
		this._classifier.classifyInstance(data);
	}

}
