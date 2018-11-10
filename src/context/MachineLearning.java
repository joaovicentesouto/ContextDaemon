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
		update(data);
	}
	
	public void update(Instances data) throws Exception
	{
		MultilayerPerceptron temp;
		
		synchronized (this) {
			temp = (MultilayerPerceptron) MultilayerPerceptron.makeCopy(_classifier);
		}
		
		//! Sem camadas intermediárias
		temp.setHiddenLayers("1");
		
		try {
			temp.buildClassifier(data);
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
		eval.crossValidateModel(_classifier, data, folds, rand);
		
		System.out.println("Erro: " + eval.errorRate());
		System.out.println("Erro: " + eval.toSummaryString());
		
		synchronized (this) {
			_classifier = temp;
		}
	}
	
	public void relearning(Instances data) throws Exception {
		//! Precisa aprender do zero se for SGD
		update(data);
	}
	
	public synchronized void predict(Instance data) throws Exception
	{
		synchronized (this) {
			//! Atualiza a própria instância (Verificar)
			_classifier.classifyInstance(data);
		}
	}

}
