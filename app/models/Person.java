package models;

import javax.persistence.*;

import com.avaje.ebean.Model;

@Entity
public class Person extends Model{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;

    public String name;
    
    public static final Find<Long,Person> find = new Find<Long,Person>(){};
    
    public String getName() {
		return name;
	}
    
    public void setName(String name) {
		this.name = name;
	}
}
