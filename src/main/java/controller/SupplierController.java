package controller;

import model.Supplier;

import java.util.List;
import java.util.ArrayList;


public class SupplierController {
	private static ArrayList<Supplier> suppliers = new ArrayList<>();
	
	private static int getIncrement() {
		Supplier s = null;
		try {		
			s = suppliers.get(suppliers.size() - 1);
		} catch(Exception e) {
			System.out.println(e.toString());
			return 0;
		}
		return s.getId()+1;
	}
	
	private static Supplier find(int id) {
		for (Supplier s : suppliers) {
			if (s.getId() == id)
				return s;
		}
		return null;
	}
	
	
	public static String Create(Supplier supplier) {
		try {
		supplier.setId(getIncrement());
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		if(suppliers.add(supplier))
			return supplier.toJson();
		return null;
		
	}
	
	public static String List() {
		String json = "[";
		try {
			for (Supplier s : suppliers) {
				json+=s.toJson() + ",\n";
			}
			if(json != "[") {
				json = json.substring(0, json.length() -2);
			}
			
		} catch(Exception e) {
			System.out.println(e.toString());
			return null;
		}
		json += "]";
		return json.equals("[]") ? null : json;
	}
	
	public static String Get(int id) {
		try {
			return find(id).toJson();
		} catch(Exception e) {
			return null;
		}
	}
	
	public static String Update (int id, Supplier supplier) {
		supplier.setId(id);
		try {
			Supplier s = find(id);
			s = supplier;
			suppliers.set(id, s).toJson();
			return s.toJson();
		}catch (Exception e) {
			return null;
		}
	}
	
	public static boolean Delete(int id) {
		try {
			suppliers.remove(id);
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
		return true;
	}

}
