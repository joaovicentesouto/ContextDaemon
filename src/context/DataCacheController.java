package context;

import context.CacheController;
import java.util.ArrayList;
//import java.time;

//import weka.core.Instance;
//import weka.core.Instances;
//import weka.core.DenseInstance;
//import weka.core.Attribute;

public class DataCacheController implements CacheController {
	
	private ArrayList<SmartData> inside_temp, outside_temp;
	private ArrayList<SmartData> inside_hum, outside_hum;
//	private ArrayList<Attribute> attributes;

	@Override
	public void update(SmartData data) {
		System.out.println("Data: " + data.toString());
		
//		//attribute numeric
//		Attribute a0 = new Attribute("temperature_inside");
//		Attribute a1 = new Attribute("temperature_outside"); 
//		Attribute a2 = new Attribute("humidity_inside");
//		Attribute a3 = new Attribute("humidity_outside"); 
//		Attribute a4 = new Attribute("second");
//		Attribute a5 = new Attribute("minute"); 
//		Attribute a6 = new Attribute("hour");
//		Attribute a7 = new Attribute("day"); 
//		Attribute a8 = new Attribute("w_day");
//		Attribute a9 = new Attribute("month"); 
//		Attribute a10 = new Attribute("year");
//		Attribute a11 = new Attribute("temperature_ideal"); 
//		
//		attributes.add(a0);
//		attributes.add(a1);
//		attributes.add(a2);
//		attributes.add(a3);
//		attributes.add(a4);
//		attributes.add(a5);
//		attributes.add(a6);
//		attributes.add(a7);
//		attributes.add(a8);
//		attributes.add(a9);
//		attributes.add(a10);
//		attributes.add(a11);
//		
//		Instances instances = new Instances("weather", attributes, 12);
//		
//		SmartData smart = data;
//		
//		SmartData aux;
//		
//		if(smart.getX() == 298 && smart.getUnit().equals("2224179556")) {
//			aux = inside_temp.get(inside_temp.size() - 1);
//			if(aux.getValue() - smart.getValue() < 0.1 || smart.getValue() - aux.getValue() < 0.1)
//				inside_temp.add(smart);
//		} else if(smart.getX() == 302 && smart.getUnit().equals("2224179500")) {
//			aux = outside_temp.get(outside_temp.size() - 1);
//			if(aux.getValue() - smart.getValue() < 0.1 || smart.getValue() - aux.getValue() < 0.1)
//				outside_temp.add(smart);
//		} else if(smart.getX() == 298 && smart.getUnit().equals("2224179556")) {
//			aux = inside_hum.get(inside_hum.size() - 1);
//			if(aux.getValue() - smart.getValue() < 0.1 || smart.getValue() - aux.getValue() < 0.1)
//				inside_hum.add(smart);
//		} else if(smart.getX() == 302 && smart.getUnit().equals("2224179500")) {
//			aux = outside_hum.get(outside_hum.size() - 1);
//			if(aux.getValue() - smart.getValue() < 0.1 || smart.getValue() - aux.getValue() < 0.1)
//				outside_hum.add(smart);
//		}
//		
//		if(inside_temp.size() > 1000 || outside_temp.size() > 1000 || inside_hum.size() > 1000 || outside_hum.size() > 1000) {
//			int smaller_size = outside_hum.size();
//			if (smaller_size > inside_temp.size())
//		        smaller_size = inside_temp.size();
//			else if (smaller_size > outside_temp.size())
//		        smaller_size = outside_temp.size();
//			else if (smaller_size > inside_hum.size())
//		        smaller_size = inside_hum.size();
//			
//			int index = 0;
//			int mode = 3;
//			while(index < smaller_size) {
//				
//				if(mode % 3 == 0) {
//					mode++;
//					continue;
//				}
//				
//				SmartData temp_in = inside_temp.get(index);
//				SmartData temp_out = outside_temp.get(index);
//				SmartData hum_in = inside_hum.get(index);
//				SmartData hum_out = outside_hum.get(index);
//				        
//				String tstamp = (String) temp_in.getTimestamp();
//				
//				// transforming tstamp 
//				
//				Instance instance = new DenseInstance(12);
//				
//				instance.setValue(0, temp_in.getValue());
//				instance.setValue(1, temp_out.getValue());
//				instance.setValue(2, hum_in.getValue());
//				instance.setValue(3, hum_out.getValue());
//				
//				// add attributes
//				
//				instances.add(instance);
//				
//				index++;
//			}
//			// call feature selection
//			// call deep learning
//		}
	}
}