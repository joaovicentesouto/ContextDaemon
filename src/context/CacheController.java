package context;

import context.CacheController;
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
	
	//! Max size of _instances = 1000
	private List<Instance> _instances;
	private ArrayList<Attribute> attributes;
	
	private Instances _persistent_instances;
	
	//! Build one instance per 30 seconds
	private Double avg_internal_temps = 0.0, avg_external_temps = 0.0, avg_internal_hums = 0.0, avg_external_hums = 0.0;
	private int n_in_temp = 0, n_out_temp = 0, n_in_hum = 0, n_out_hum = 0;
	
	private Calendar _calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")); 

	public CacheController() throws Exception {
		super();
		
		_instances = Collections.synchronizedList(new LinkedList<Instance>());
		_calendar.setTimeInMillis(System.currentTimeMillis());
		
		reload_backup();
//		feature_selection();
	}
	
	private void reload_backup() throws Exception {
		
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
//				new Attribute("internal_temperature"),	fica
//				new Attribute("external_temperature"),	fica
//				new Attribute("internal_humidity"),		fica
//				new Attribute("external_humidity"),		fica
//				new Attribute("second"),				sai
//				new Attribute("minute"),				fica
//				new Attribute("hour"),					fica
//				new Attribute("day"),					sai
//				new Attribute("week_day"),				fica
//				new Attribute("month"),					sai
//				new Attribute("ideal_temperature")		fica
//			)
//		);
//		
//		_persistent_instances = new Instances("weather", attributes, 11);
//	}

	public synchronized void updateData(SmartData data) throws Exception
	{	
		//! Se já se passaram 30 segundos => constrói uma instância
		if (_calendar.getTimeInMillis()/1000l - data.getT() > 30) {
			
			Instance instance = new DenseInstance(8); //! _persistent_instances.size()
			instance.setValue(0, avg_internal_temps);
			instance.setValue(1, avg_external_temps);
			instance.setValue(2, avg_internal_hums);
			instance.setValue(3, avg_external_hums);
			instance.setValue(4, _calendar.get(Calendar.MINUTE));
			instance.setValue(5, _calendar.get(Calendar.HOUR_OF_DAY));
			instance.setValue(6, _calendar.get(Calendar.DAY_OF_WEEK));
			instance.setValue(7, 22); //! Comando do usuário
			
			_instances.add(instance);
			
			
			//! Zera nova instância
			_calendar.setTimeInMillis((data.getT() + 1) * 1000L);
			avg_internal_temps = 0.0;
			avg_external_temps = 0.0;
			avg_internal_hums = 0.0;
			avg_external_hums = 0.0;
			n_in_temp = 0;
			n_out_temp = 0;
			n_in_hum = 0;
			n_out_hum = 0;
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
		if(_instances.size() >= 1000) {
			update_model();
		}
	}
	
	public synchronized void update_model() {
		
	}
	
	public synchronized void updateControl(SmartData data) {
		
	}
}