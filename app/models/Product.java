package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class Product extends Model{

	
	@Id
	public long id ;
	
	public String prod_name ;
	
}
