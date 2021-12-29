package model;


import java.io.Serializable;

public class Player implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String firstName;
	private String lastName;
	private String age;
	private String username;
	private String password;
	private String ipAddress;
	private String status="OFFLINE"; 
	
	public Player(String firsName,String lastName,String age,String username,String password,String ipAddress){
		this.firstName=firsName;
		this.lastName=lastName;
		this.age=age;
		this.username=username;
		this.password=password;
		this.ipAddress=ipAddress;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getValues() {
		String player = this.getFirstName() + ":" + this.getLastName() + ":" + this.getAge() + ":" + this.getUsername() + ":" +this.getPassword()+ ":" +this.getIpAddress()+ ":" +this.getStatus();
    	return player;
    }

}