package context.cache;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.DenseInstance;

import context.component.SmartData;

public class CacheController
{
//! ================= Attributes =================

	//! Constantes
	static long TEMPERATURE = 2224179556L;
	static long HUMIDITY    = 2224179500L; //! VERIFICAR

	//! Guarda o lote de novas instancias
	//! Uso: Apenas para reforçar o aprendizado (update)
	private Instances _current_instances = null;

	//! Guarda a cache persistente em memória (espelho do que existe no disco)
	
	//! Uso: Usado apenas quando o modelo errar
	//! (treinamento do zero e será que precisa mudar alguns valores??)
	private Instances _persistent_instances = null;

	//! Contexto atual
	private Instance _current_context = null;

	//! Armazena a média dos valores medidos num intervalo de 30s
	private Double _avg_internal_temps = 0.0;
	private Double _avg_external_temps = 0.0;
	private Double _avg_internal_hums  = 0.0;
	private Double _avg_external_hums  = 0.0;
	private int    _n_in_temp 		   = 0;
	private int    _n_out_temp 		   = 0;
	private int    _n_in_hum 		   = 0;
	private int    _n_out_hum 		   = 0;

	//! Current user command
	private Double _current_ideal_temperature = 0.0;
	private long   _last_command_time 		  = 0L;

	//! Responsável pela armazenamento do timestamp
	private Calendar _calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

//! ================= Constructor =================

	public CacheController() throws Exception {
		super();

		try {
			reload_backup();
			_current_instances.clear();
		} catch (Exception e) {
			throw new Exception("CacheController: error on contructor!");
		}

		reset_parameters();
	}

	private void reload_backup() throws Exception
	{	
		//! Carrega instancias persistentes
		DataSource source;
		Instances cache;

		try {
			source = new DataSource("cache.arff");
			cache = source.getDataSet();
		} catch (Exception e) {
			throw new Exception("CacheController: cache.arff failed!");
		} 

		//! Os dados da cache persistente são insuficientes?
		if (cache.size() < 1000)
		{
			Instances default_instances;

			try {
				source = new DataSource("default.arff");
				default_instances = source.getDataSet();
			}
			catch (Exception e) {
				throw new Exception("CacheController: default.arff failed!");
			}

			default_instances.setClassIndex(cache.numAttributes() - 1);

			Iterator<Instance> it = default_instances.iterator();

			//! Completa instancias utiliando as default
			while(it.hasNext() && cache.size() < 1000) {
				Instance i = it.next();
				cache.add(i);
			}
		}

		_persistent_instances = cache;

		//! Configura o espelho da cache persistente
		_persistent_instances.setClassIndex(_persistent_instances.numAttributes() - 1);
		_current_instances = new Instances(_persistent_instances);

		//! Assume sendo o contexto atual
		_current_context = _persistent_instances.get(_persistent_instances.size()-1);
		_last_command_time = _calendar.getTimeInMillis();
	}

//! ================= Data =================

	public void update_data(SmartData data)
	{
		//! Cria uma instancia que representa um intervalo de 30s
		if ((_calendar.getTimeInMillis() - data.getT())/1000L > 30)
			create_context(data);

		switch (data.getX())
		{

		//! Interno
		case 298:
			if (data.getUnit() == TEMPERATURE) {
				_n_in_temp++;
				_avg_internal_temps = _avg_internal_temps + (data.getValue() - _avg_internal_temps) / _n_in_temp;
			}
			else if (data.getUnit() == HUMIDITY) {
				_n_in_hum++;
				_avg_internal_hums = _avg_internal_hums + (data.getValue() - _avg_internal_hums) / _n_in_hum;
			}

			break;

			//! Externo
		case 302:
			if (data.getUnit() == TEMPERATURE) {
				_n_out_temp++;
				_avg_external_temps = _avg_external_temps + (data.getValue() - _avg_external_temps) / _n_out_temp;
			}
			else if (data.getUnit() == HUMIDITY) {
				_n_out_hum++;
				_avg_external_hums = _avg_external_hums + (data.getValue() - _avg_external_hums) / _n_out_hum;
			}

			break;

		default:
			System.err.println("Este dispositivo não deveria estar sendo monitorado!");
			break;
		}

		print_parameters();
	}

	private void create_context(SmartData data)
	{
		if (_n_in_temp == 0 || _n_in_hum == 0 || _n_out_temp == 0 || _n_out_hum == 0) {
			System.err.println("CacheController: Cannot create the context instance!");
			return;
		}

		Instance instance = new DenseInstance(_persistent_instances.numAttributes());

		instance.setValue(0, _avg_internal_temps);
		instance.setValue(1, _avg_external_temps);
		instance.setValue(2, _avg_internal_hums);
		instance.setValue(3, _avg_external_hums);
		instance.setValue(4, _calendar.get(Calendar.MINUTE));
		instance.setValue(5, _calendar.get(Calendar.HOUR_OF_DAY));
		instance.setValue(6, _calendar.get(Calendar.DAY_OF_WEEK));

		synchronized (_current_ideal_temperature) {
			instance.setValue(7, _current_ideal_temperature); //! Comando do usuário
		}

		//! Adiciona nas instâncias atuais
		_current_instances.add(instance);

		//! Atualiza o contexto atual
		_current_context = instance;

		//! Reseta parâmetros para nova instância
		_calendar.setTimeInMillis(data.getT() + 1);

		reset_parameters();
	}

//! ================= Control =================

	public synchronized void update_control(SmartData data, boolean from_user) {
		if (from_user) {
			//! Precisa modificar alguns valores?
			//! Tipo, todas as horas iguais a esse data tem a mesma temperatura!
			
			for (Instance i : _persistent_instances) {
				if (i.value(0) == 0.0) {
					
				}
			}
		}
		
		synchronized (_current_ideal_temperature) {
			_current_ideal_temperature = data.getValue();
			_last_command_time = _calendar.getTimeInMillis();
		}
	}

//! ================= Getters =================

	public Instance current_context() {
		return _current_context;
	}

	public Instances current_instances() {
		return new Instances(_current_instances);
	}

	public int current_size() {
		return _current_instances.size();
	}

	public Instances persistente_instances() {
		return new Instances(_persistent_instances);
	}

	public long last_command_time() {
		return _last_command_time;
	}

//! ================= Persistent Cache (Disk) =================

	public void persist_instances()
	{
		//! Elimina as instâncias mais antigas.
		//! 24 horas * 120 medidas_por_hora = 2880 entradas por dia
		int limit = _persistent_instances.size() - 2280;
		for (int index = 0; index < limit; index++)
			_persistent_instances.remove(index);

		//! Atualiza com novas instancias 
		_persistent_instances.addAll(_current_instances);

		//! Atualiza em disco
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("cache2.arff", false));
			writer.write(_persistent_instances.toString());
			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			System.out.println("CacheController: Error on periodic update");
			e.printStackTrace();
		}

		_current_instances.clear();
	}

//! ================= Auxiliary =================

	private void reset_parameters() {
		_avg_internal_temps = 0.0;
		_avg_external_temps = 0.0;
		_avg_internal_hums = 0.0;
		_avg_external_hums = 0.0;
		_n_in_temp = 0;
		_n_out_temp = 0;
		_n_in_hum = 0;
		_n_out_hum = 0;
	}

	static int i = 0;
	private void print_parameters() {
		System.out.println("Atualização das médias " + i++);
		System.out.println("_avg_internal_temps: " + _avg_internal_temps);
		System.out.println("_avg_external_temps: " + _avg_external_temps);
		System.out.println("_avg_internal_hums: " + _avg_internal_hums);
		System.out.println("_avg_external_hums: " + _avg_external_hums);
		System.out.println("_n_in_temp: " + _n_in_temp);
		System.out.println("_n_out_temp: " + _n_out_temp);
		System.out.println("_n_in_hum: " + _n_in_hum);
		System.out.println("_n_out_hum: " + _n_out_hum);
		System.out.println("");
	}
}