package context;

import context.CacheController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
//import java.time;
import java.util.List;
import java.util.TimeZone;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.DenseInstance;
import weka.core.Attribute;

public class CacheController {
	
	//! Constantes
	static long TEMPERATURE = 2224179556L;
	static long HUMIDITY = 2224179500L; //! VERIFICAR
	
	//! Max size of _current_instances = 1000
	private Instances _current_instances;
	
//	private ArrayList<Attribute> attributes;
	
	private Instances _persistent_instances;
	
	//! Build one instance per 30 seconds
	private Double avg_internal_temps;
	private Double avg_external_temps;
	private Double avg_internal_hums;
	private Double avg_external_hums;
	private int n_in_temp;
	private int n_out_temp;
	private int n_in_hum;
	private int n_out_hum;
	
	private Calendar _calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	
	private Instance _current_context = null;
	
	private MachineLearning _learning = null;

	public CacheController(MachineLearning learning) throws Exception {
		super();

		_learning = learning;
		
		 //! Ver o t do smartdata está em milisegundos
		_calendar.setTimeInMillis(System.currentTimeMillis());
		
		try {
			reload_backup();
			_current_instances.clear();
		} catch (Exception e) {
			throw e;
		}
		
		reset_parameters();
//		feature_selection();
	}
	
	private void reload_backup() throws Exception {
		
		//! Carrega instancias persistentes
		DataSource source;
		Instances aux;
		
		try {
			source = new DataSource("cache.arff");
			aux = source.getDataSet();
		} catch (Exception e) {
			throw new Exception("cache.arff failed!");
		}

//		Instances aux = source.getDataSet();
		aux.setClassIndex(aux.numAttributes() - 1); 
		
		//! Dados insuficientes?
		if (aux.size() < 1000)
		{
			try {
				source = new DataSource("default.arff");
			} catch (Exception e) {
				throw new Exception("default.arff failed!");
			}
			
			_persistent_instances = source.getDataSet();
			_persistent_instances.setClassIndex(aux.numAttributes() - 1);
			
			Iterator<Instance> it = _persistent_instances.iterator();
			
			while(it.hasNext() && _persistent_instances.size() < 1000)
			{
				Instance i = it.next();
				aux.add(i);
			}
		}
		
		_persistent_instances = aux;
		
		_persistent_instances.setClassIndex(_persistent_instances.numAttributes() - 1);
		_current_context = _persistent_instances.get(_persistent_instances.size()-1);
//		
//		_learning = new MachineLearning(_persistent_instances);
//		
		_current_instances = new Instances(_persistent_instances);
//		_current_instances.clear();
	}
	
	public synchronized void updateControl(SmartData data) { }

	public synchronized void updateData(SmartData data) throws Exception
	{	
		//! Se já se passaram 30 segundos => constrói uma instância
		System.out.println("Seco333: " + (data.getT() - _calendar.getTimeInMillis())/1000L);
		if ((data.getT() - _calendar.getTimeInMillis())/1000L > 30) {
			System.out.println("Seco333: " + (data.getT() - _calendar.getTimeInMillis())/1000L);
			
			Instance instance = new DenseInstance(8); //! _persistent_instances.size()
			instance.setValue(0, avg_internal_temps);
			instance.setValue(1, avg_external_temps);
			instance.setValue(2, avg_internal_hums);
			instance.setValue(3, avg_external_hums);
			instance.setValue(4, _calendar.get(Calendar.MINUTE));
			instance.setValue(5, _calendar.get(Calendar.HOUR_OF_DAY));
			instance.setValue(6, _calendar.get(Calendar.DAY_OF_WEEK));
			instance.setValue(7, 22); //! Comando do usuário
			
			_current_instances.add(instance);
			System.out.println("In: " + instance);
			
			_current_context = instance;
			
			//! Zera nova instância
			_calendar.setTimeInMillis(data.getT() + 1);
			reset_parameters();
		}
		
		
		switch (data.getX())
		{
		//! Interno
		case 298:
			if (data.getUnit() == TEMPERATURE) {
				n_in_temp++;
				avg_internal_temps = avg_internal_temps + (data.getValue() - avg_internal_temps) / n_in_temp;
			} else { //! HUMIDITY
				n_in_hum++;
				avg_internal_hums = avg_internal_hums + (data.getValue() - avg_internal_hums) / n_in_hum;
			}
			
			break;
			
		//! Externo
		case 302:
			if (data.getUnit() == TEMPERATURE) {
				n_out_temp++;
				avg_external_temps = avg_external_temps + (data.getValue() - avg_external_temps) / n_out_temp;
			} else { //! HUMIDITY
				n_out_hum++;
				avg_external_hums = avg_external_hums + (data.getValue() - avg_external_hums) / n_out_hum;
			}
			
			break;
			
		//! Erro
		default:
			throw new Exception("Deu ruim!");
		}
		
		//! Update learning model
		if(_current_instances.size() >= 2) {
			update_model();
			_current_instances.clear();
		}
		
		print_parameters();
	}
	
	public synchronized Instance current_context() {
		return _current_context;
	}
	
	public synchronized void update_model()
	{
		System.out.println("UPDATING.......\n");
		//! Update model
		try {
			_learning.update(_current_instances);			
		} catch (Exception e) {
			System.out.println("Error on periodic update: " + e.getMessage());
		}
		
		//! Update persistent cache
		_persistent_instances.addAll(_current_instances);
		
		//! 24 horas * 120 medidas_por_hora = 2880 entradas por dia (nunca vai ser isso)
		int limit = _persistent_instances.size() - 2280;
		for (int index = 0; index < limit; index++)
			_persistent_instances.remove(index);
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("cache2.arff", false));
			writer.write(_persistent_instances.toString());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			System.out.println("Error on periodic update: " + e.getMessage());
		}
	}
	
	public MachineLearning learning() {
		return _learning;
	}
	
	private void reset_parameters() {
		avg_internal_temps = 0.0;
		avg_external_temps = 0.0;
		avg_internal_hums = 0.0;
		avg_external_hums = 0.0;
		n_in_temp = 0;
		n_out_temp = 0;
		n_in_hum = 0;
		n_out_hum = 0;
	}
	
	static int i = 0;
	
	private void print_parameters() {
		System.out.println("Data " + i++);
		System.out.println("avg_internal_temps: " + avg_internal_temps);
		System.out.println("avg_external_temps: " + avg_external_temps);
		System.out.println("avg_internal_hums: " + avg_internal_hums);
		System.out.println("avg_external_hums: " + avg_external_hums);
		System.out.println("n_in_temp: " + n_in_temp);
		System.out.println("n_out_temp: " + n_out_temp);
		System.out.println("n_in_hum: " + n_in_hum);
		System.out.println("n_out_hum: " + n_out_hum);
		System.out.println("");
	}
}