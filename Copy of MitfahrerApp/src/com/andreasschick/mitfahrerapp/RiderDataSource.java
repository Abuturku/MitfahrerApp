package com.andreasschick.mitfahrerapp;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class RiderDataSource {
	
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = {"ID", "NAME", "RIDES"};
	
	
	public RiderDataSource(Context context){
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public Rider createRider(String name, int rides){
		ContentValues values = new ContentValues();
		values.put("NAME", name);
		values.put("RIDES", rides);
		
		long insertId = database.insert("RIDERS", null, values);
		
		Cursor cursor = database.query("RIDERS", allColumns, "ID = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		
		return cursorToRider(cursor);
	}
	
	public void deleteRider(String name){
		database.delete("RIDERS", "NAME = '" + name + "'", null);
	}

	private Rider cursorToRider(Cursor cursor) {
		int setId = cursor.getInt(0);
		String setRidersName = cursor.getString(1);
		int setRides = cursor.getInt(2);
		
		Rider rider = new Rider(setId, setRidersName, setRides);
		
		return rider;
	}
	
	protected List<Rider> getAllRiders(){
		List<Rider> RidersList = new ArrayList<Rider>();
		
		Cursor cursor = database.query("RIDERS", allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		if(cursor.getCount() == 0) return RidersList;
		
		while(cursor.isAfterLast() != true){
			Rider rider = cursorToRider(cursor);
			RidersList.add(rider);
			cursor.moveToNext();
		}
		
		return RidersList;
	}
	
	protected List<String> getAllRidersNames(){
		List<String> RidersList = new ArrayList<String>();
		
		Cursor cursor = database.rawQuery("SELECT NAME FROM RIDERS", null);
		cursor.moveToFirst();
		
		if (cursor.getCount() != 0){
			while(cursor.isAfterLast() != true){
				RidersList.add(cursor.getString(0));
				cursor.moveToNext();
			}
		}
		
		return RidersList;
	}
	
	protected List<Integer> getAllRides(){
		List<Integer> RidesList = new ArrayList<Integer>();
		
		Cursor cursor = database.rawQuery("SELECT RIDES FROM RIDERS", null);
		cursor.moveToFirst();
		
		if (cursor.getCount() != 0){
			while(cursor.isAfterLast() != true){
				RidesList.add(cursor.getInt(0));
				cursor.moveToNext();
			}
		}
		
		return RidesList;
	}
	
	public void updateRides(String name, int rides){
		ContentValues values = new ContentValues();
		values.put("RIDES", rides);
		database.update("RIDERS", values, "NAME = '" + name + "'", null);
	}
}
