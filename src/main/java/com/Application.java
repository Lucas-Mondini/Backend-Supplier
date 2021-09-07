package com;

import controller.SupplierController;
import model.ReturnObj;
import model.Supplier;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

class Application {
	private static String JsonNotFound 	= "{\"error\": \"Not found\"}";
	private static String JsonSuccess	= "{\"Success\": \"Success\"}";
	private static String JsonSWW		= "{\"Error\": \"Something went wrong\"}";
	private static String JsonMA		= "{\"Error\": \"Missing argument\"}";

	public static void main(String[] args) throws IOException {
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/api/Supplier", (exchange -> {
        Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
    	ObjectMapper mapper = new ObjectMapper();
    	Map<String,Object> body= null; 
    	try {
    		body = mapper.readValue(exchange.getRequestBody(),Map.class);
    	} catch (Exception e) { }
            if (exchange.getRequestMethod().equals("GET")) {
            	String id = null;
            	try {
            		id = params.get("id").get(0);
            		//esse catch é apenas para se o valor do parametro for null
            	} catch (Exception e) { 
            		ReturnObj suppliers = SupplierController.List();
            		int code = 0;
                	if(suppliers.success) {
                		code = 200;
                	}
                	else {
                		code = 404;
                	}
                	Response(exchange, code, suppliers.text);
            	}
                	
            	ReturnObj supplier = SupplierController.Get(Integer.parseInt(id));
            	int code = 0;
            	if(supplier.success) {
            		code = 200;
            	}
            	else { 
            		code = 404;
            	}
            	Response(exchange, code, supplier.text);
            
            } else if (exchange.getRequestMethod().equals("POST")) {
            	String name 	= null;
            	String Email 	= null;
            	String Comment 	= null;
            	String CNPJ 	= null;
            	try {
            	name 		= body.get("name").toString();
            	Email 		= body.get("email").toString();
            	Comment 	= body.get("comment").toString();
            	CNPJ 		= body.get("CNPJ").toString();
        		//esse catch é apenas para se não conseguir retornar os valores
            	} catch(Exception e) {
            		Response(exchange, 400, JsonMA);
            	}
            	
            	ReturnObj s = SupplierController.Create(new Supplier(name, Email, Comment, CNPJ));
            	int code = 0;
            	if(s.success)
            		code = 200;
            	else 
            		code = 400;
            	Response(exchange, code, s.text);
            	
            	
            } else if (exchange.getRequestMethod().equals("PUT")) {
            	
            	String id = null;
            	try {
            		id = params.get("id").get(0);
            		//esse catch é apenas para se o valor do parametro for null
            	} catch(Exception e) {
            		Response(exchange, 400, JsonMA);
            	}
            	String name 	= null;
            	String Email 	= null;
            	String Comment 	= null;
            	String CNPJ 	= null;            	
            	try {
            		name 		= body.get("name").toString();
            		Email 		= body.get("email").toString();
            		Comment 	= body.get("comment").toString();
            		CNPJ 		= body.get("CNPJ").toString();
            		//esse catch é apenas para se o valor do parametro for null
            	} catch (Exception e) { 
            		Response(exchange, 400, JsonMA);
            	}
            	
            	ReturnObj s = SupplierController.Update(Integer.parseInt(id), new Supplier(name, Email, Comment, CNPJ));
            	int code;
            	if(s.success)
            		code = 200;
            	else 
            		code = 400;
            	Response(exchange, code, s.text);
            	
            } else if (exchange.getRequestMethod().equals("DELETE")) {
            	String id = null;
            	try {
            		id = params.get("id").get(0);
            		//esse catch é apenas para se o valor do parametro for null
            	} catch(Exception e) {
            		Response(exchange, 400, JsonMA);
            	}
            	
            	ReturnObj s = SupplierController.Delete(Integer.parseInt(id));
            	int code;
            	if(s.success)
            		code = 200;
            	else
            		code = 400;
            	
            	Response(exchange, code, s.text);
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
            exchange.close();
        }));
        server.setExecutor(null); // creates a default executor
        server.start();
    }
    
    private static void Response(HttpExchange e,int status, final String res) throws IOException  {
    	e.sendResponseHeaders(status, res.getBytes().length);
        OutputStream output = e.getResponseBody();
        output.write(res.getBytes());
        output.flush();
    }
    
    
    
    
    
    
    public static Map<String, List<String>> splitQuery(String query) {
        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
            .map(s -> Arrays.copyOf(s.split("="), 2))
            .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));

    }

    private static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }
}