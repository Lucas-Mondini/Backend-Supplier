package model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Supplier {
	private int Id;
	private String Name;
	private String Email;
	private String Comment;
	private String CNPJ;
	
	public Supplier(String Name, String Email, String Comment, String CNPJ) {
		this.Id = Id = 0;
		this.Name = Name;
		this.Email = Email;
		this.Comment = Comment;
		this.CNPJ = CNPJ;
	}
	
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
		   json = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
		   e.printStackTrace();
		}
		return json;
	}
	
	//Getters and Setters
	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public String getCNPJ() {
		return CNPJ;
	}

	public void setCNPJ(String cNPJ) {
		CNPJ = cNPJ;
	}


}
