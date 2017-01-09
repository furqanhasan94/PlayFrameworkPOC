package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Model;

@Entity
@Table(name = "customer")
public class Customer extends Model{

	
	@Id
	public Long id ;
	
	public String customer_name ;
}
