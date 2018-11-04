package context;

import context.CacheController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
//import java.time;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.DenseInstance;
import weka.core.Attribute;

public class CacheController {
	
	//! Constantes
	static long TEMPERATURE = 2224179556L;
	static long HUMIDITY = 2224179500L; //! VERIFICAR
	
	//! Max size of _instances = 1000
	private List<Instance> _instantes;
	private ArrayList<Attribute> attributes;
	
	private Instances _persistent_instances;
	
	//! Build one instance per 30 seconds
	private Double avg_internal_temps = 0.0, avg_external_temps = 0.0, avg_internal_hums = 0.0, avg_external_hums = 0.0;
	private int n_in_temp = 0, n_out_temp = 0, n_in_hum = 0, n_out_hum = 0;
	
	private Date current;

	public CacheController() {
		super();
		
		_instantes = Collections.synchronizedList(new LinkedList<Instance>());
		
		reload_backup();
		feature_selection();
	}
	
	private void reload_backup() {
		
		//! Carrega instancias persistentes
		DataSource source = new DataSource("cache.arff");
		_persistent_instances = source.getDataSet();
		
		//! Dados insuficientes?
		if (_persistent_instances.size() < 1000)
		{
			source = new DataSource("default.arff");
			Instances aux = source.getDataSet();
			
			Iterator<Instance> it = aux.iterator();
			
			while(it.hasNext())
				_persistent_instances.add((Instance) it);
		}
	}
	
	//! Vou deixar o feature selection de lado por enquanto.
	//! Problema em fazer o feature e salvar as instancias com
	//! menos dados que o que podem vir a precisar depois.
	//! Ver se fizemos apenas uma vez na mão o feature selection.
//	private void feature_selection() {
//		attributes = new ArrayList<Attribute>(
//			Arrays.asList(
//				new Attribute("internal_temperature"),
//				new Attribute("external_temperature"),
//				new Attribute("internal_humidity"),
//				new Attribute("external_humidity"),
//				new Attribute("second"),
//				new Attribute("minute"),
//				new Attribute("hour"),
//				new Attribute("day"),
//				new Attribute("week_day"),
//				new Attribute("month"),
//				new Attribute("ideal_temperature")
//			)
//		);
//		
//		_persistent_instances = new Instances("weather", attributes, 11);
//	}

	public synchronized void updateData(SmartData data) {
		System.out.println("Data: " + data.toString());
		
		//attribute numeric
	
		Double aux;
	
		
		if(data.getX() == 298 && data.getUnit() == TEMPERATURE) {
			aux = internal_temps.get(internal_temps.size() - 1);
			if(aux - data.getValue() < 0.1 || data.getValue() - aux < 0.1)
				internal_temps.add(data.getValue());
		} else if(data.getX() == 302 && data.getUnit() == TEMPERATURE) {
			aux = external_temps.get(external_temps.size() - 1);
			if(aux - data.getValue() < 0.1 || data.getValue() - aux < 0.1)
				external_temps.add(data.getValue());
		} else if(data.getX() == 298 && data.getUnit() == HUMIDITY) {
			aux = internal_hums.get(internal_hums.size() - 1);
			if(aux - data.getValue() < 0.1 || data.getValue() - aux < 0.1)
				internal_hums.add(data.getValue());
		} else if(data.getX() == 302 && data.getUnit() == HUMIDITY) {
			aux = external_hums.get(external_hums.size() - 1);
			if(aux - data.getValue() < 0.1 || data.getValue() - aux < 0.1)
				external_hums.add(data.getValue());
		}
		
		Double temp_in = internal_temps.get(index);
		Double temp_out = external_temps.get(index);
		Double hum_in = internal_hums.get(index);
		Double hum_out = external_hums.get(index);
		        
		Date tstamp = timestamps.get(index);
		
		// transforming tstamp 
		
		Instance instance = new DenseInstance(12);
		
		instance.setValue(0, temp_in);
		instance.setValue(1, temp_out);
		instance.setValue(2, hum_in);
		instance.setValue(3, hum_out);
		
		// add attributes
		
		instances.add(instance);
		
		index++;
		
		if(internal_temps.size() > 1000 || external_temps.size() > 1000 || internal_hums.size() > 1000 || external_hums.size() > 1000) {
			
		}
	}
	
	public synchronized void updateControl(SmartData data) {
		
	}
	}
}