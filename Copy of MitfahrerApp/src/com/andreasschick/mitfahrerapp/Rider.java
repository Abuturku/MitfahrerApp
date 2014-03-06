package com.andreasschick.mitfahrerapp;

public class Rider {
	
	
	private int id;
	private String ridersName;
	private int rides;
	
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getRidersName() {
		return ridersName;
	}
	
	public void setRidersName(String ridersName) {
		this.ridersName = ridersName;
	}
	
	public int getRides() {
		return rides;
	}
	
	public void setRides(int rides) {
		this.rides = rides;
	}
	

	
	public Rider(int id, String ridersName, int rides) {
		this.id = id;
		this.ridersName = ridersName;
		this.rides = rides;
	}

	public Rider(String ridersName, int rides) {
		this.ridersName = ridersName;
		this.rides = rides;
	}
	
}
