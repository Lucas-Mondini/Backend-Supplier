package controller;

import model.Supplier;
import model.ReturnObj;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;


public class SupplierController {
	private static ArrayList<Supplier> suppliers = new ArrayList<>();
	
	private static String JsonNotFound 	= "{\"error\": \"Not found\"}";
	private static String JsonCNPJ 		= "{\"error\": \"CNPJ already in use\"}";
	private static String JsonEmail 	= "{\"error\": \"Invalid Email\"}";
	private static String JsonSuccess	= "{\"Success\": \"Success\"}";
	private static String JsonSWW		= "{\"Error\": \"Something went wrong\"}";
	private static String JsonMA		= "{\"Error\": \"Missing argument\"}";
	
	
	private static final String EMAIL_PATTERN 	= "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
	        									+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
	
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
	private static Boolean isUsedCNPJ(String CNPJ) {
		for (Supplier s : suppliers) {
			if (s.getCNPJ().equals(CNPJ))
				return true;
		}
		return false;
	}
	public static boolean isValidMail(String email){
	    Matcher matcher = pattern.matcher(email);
	    return matcher.matches();
	 }
	
	
	
	
	public static ReturnObj Create(Supplier supplier) {
		if(isUsedCNPJ(supplier.getCNPJ()))
			return new ReturnObj(false, JsonCNPJ);
		if(!isValidMail(supplier.getEmail()))
			return new ReturnObj(false, JsonEmail);
		try {
		supplier.setId(getIncrement());
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		if(suppliers.add(supplier))
			return new ReturnObj(true, supplier.toJson());
		return new ReturnObj(false, JsonSWW);
		
	}
	
	public static ReturnObj List() {
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
		if(json.equals("[]")) {
			return new ReturnObj(false, JsonNotFound);
		}
		return new ReturnObj(true, json);
	}
	
	public static ReturnObj Get(int id) {
		try {
			return new ReturnObj(true, find(id).toJson());
		} catch(Exception e) {
			return new ReturnObj(false, JsonNotFound);
		}
	}
	
	public static ReturnObj Update (int id, Supplier supplier) {
		supplier.setId(id);
		if(isUsedCNPJ(supplier.getCNPJ()))
			return new ReturnObj(false, JsonCNPJ);
		if(!isValidMail(supplier.getEmail()))
			return new ReturnObj(false, JsonEmail);
		try {
			Supplier s = find(id);
			s = supplier;
			suppliers.set(id, s).toJson();
			return new ReturnObj(true, s.toJson());
		}catch (Exception e) {
			return new ReturnObj (false, JsonNotFound);
		}
	}
	
	public static ReturnObj Delete(int id) {
		try {
			suppliers.remove(id);
		} catch (Exception e) {
			System.out.println(e.toString());
			return new ReturnObj(false, JsonNotFound);
		}
		return new ReturnObj(true, JsonSuccess);
	}

}
