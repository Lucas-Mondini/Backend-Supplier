package com;

import controller.SupplierController;
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
            		String suppliers = SupplierController.List();
                	if(suppliers != null) {
                		Response(exchange, 200, suppliers);
                	}
                	else {
                		Response(exchange, 404, JsonNotFound);
                	}
            	}
                	
            	String supplier = SupplierController.Get(Integer.parseInt(id));
            	if(supplier != null) {
            		Response(exchange, 200, supplier);
            	}
            	else { 
            		Response(exchange, 404, JsonNotFound);
            	}
            
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
            	
            	String s = SupplierController.Create(new Supplier(name, Email, Comment, CNPJ));
            	
            	if(s != null)
            		Response(exchange, 200, s);
            	else 
            		Response(exchange, 400, JsonSWW);
            	
            	
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
            	
            	String s = SupplierController.Update(Integer.parseInt(id), new Supplier(name, Email, Comment, CNPJ));
            	
            	if(s != null)
            		Response(exchange, 200, s);
            	else 
            		Response(exchange, 400, JsonSWW);
            	
            } else if (exchange.getRequestMethod().equals("DELETE")) {
            	String id = null;
            	try {
            		id = params.get("id").get(0);
            		//esse catch é apenas para se o valor do parametro for null
            	} catch(Exception e) {
            		Response(exchange, 400, JsonMA);
            	}
            	
            	if(SupplierController.Delete(Integer.parseInt(id)))
            		Response(exchange, 200, JsonSuccess);
            	Response(exchange, 400, JsonSWW);
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