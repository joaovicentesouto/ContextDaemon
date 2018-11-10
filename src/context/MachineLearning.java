package context;

import java.util.Random;

import weka.classifiers.Evaluation;
//import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.functions.SGD;
import weka.core.SelectedTag;
import weka.classifiers.UpdateableClassifier;

public class MachineLearning {
	
	//MultilayerPerceptron _classifier;
	SGD _classifier;
	boolean train = false;
	
	public MachineLearning() {
		//_classifier = new MultilayerPerceptron();
		_classifier = new SGD();
	}
	
	public MachineLearning(Instances data) throws Exception {
		//classifier = new MultilayerPerceptron();
		_classifier = new SGD();
		update(data);
	}
	
	public void update(Instances data) throws Exception
	{
		SGD temp;
		
		synchronized (this) {
			temp = (SGD) SGD.makeCopy(_classifier);
		}
		
		try {
			temp.setLossFunction(new SelectedTag(SGD.HUBER, SGD.TAGS_SELECTION));
			temp.buildClassifier(data);
			train = true;
			//temp.getOptions();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("MachineLearning: Error on update!");
		}
		
		for (Instance i : data) {
			synchronized (this) {
				((UpdateableClassifier)temp).updateClassifier(i);
			}
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
		}
				
		/*MultilayerPerceptron temp;
		
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
		}*/
	}
	
	public synchronized void predict(Instance data) throws Exception
	{
		if(train) {	
			synchronized (this) {
				//! Atualiza a própria instância (Verificar)
				_classifier.classifyInstance(data);
			}
		}
		
		
		
		
		
	}

}
