package com.andreasschick.mitfahrerapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MitfahrerApp_Activity extends Activity {

	final Context context = this;
	List<String> ridersNames = new ArrayList<String>();
	List<Integer> rides = new ArrayList<Integer>();
	int defaultRides = 1;
	double defaultPrice = 2.5;
	int driversCount;
	RiderDataSource datasource;
	SQLiteDatabase database;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mitfahrer_app);
		
		datasource = new RiderDataSource(context);

		setupLayout();
		
		driversCount = ridersNames.size() + 1;
		
	}

	private void setupLayout() {
		try {
			LinearLayout linearLayoutEntries = (LinearLayout)findViewById(R.id.LinearLayoutEntries);
			linearLayoutEntries.removeAllViews();
			
			datasource.open();
			ridersNames.clear();			
			ridersNames.addAll(datasource.getAllRidersNames());
			rides.addAll(datasource.getAllRides());
			datasource.close();
			
			if (ridersNames.size() != 0){						
				for (int i = 0; i < ridersNames.size(); i++){
					generateNewRider(ridersNames.get(i), driversCount, rides.get(i), true);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.w("SQLException", e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mitfahrer_app_, menu);
		return true;
	} //Maybe I'll add an Settings Menu later, let's see.
	//I will surely add one.
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.action_refresh:
				setupLayout();
				return true;
			default: 
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	
	public void addRider(View view){
		keyboardShow();
		
		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View promptView = layoutInflater.inflate(R.layout.promptaddrider, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// set prompts.xml to be the layout of the alertdialog builder
		alertDialogBuilder.setView(promptView);
		
		//save the users input in an TextView named input
		final TextView input = (TextView) promptView.findViewById(R.id.etPromptUserInput);
		
		// setup a dialog window
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if(input.getText().length() != 0){
									String ridersName = input.getText().toString();
									generateNewRider(ridersName, driversCount, defaultRides, false);
									keyboardHide();
								}
								else{
									Toast.makeText(context, "Bitte einen Namen eingeben!", Toast.LENGTH_LONG).show();
									keyboardHide();
								}
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,	int id) {
								dialog.cancel();
								keyboardHide();
							}
						});

		// create an alert dialog
		AlertDialog alertD = alertDialogBuilder.create();

		alertD.show();
	}
	
	public void generateNewRider(final String ridersName, final int driversCount, int rides, boolean onCreate){
		this.driversCount++;
		
		LinearLayout linearLayoutEntries = (LinearLayout)findViewById(R.id.LinearLayoutEntries);
		
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		final LinearLayout view = (LinearLayout) layoutInflater.inflate(R.layout.entrylayout, null);
		view.setId(this.driversCount);
		
		
		//Add the CustomView to the LinearLayout in the ScrollView
		linearLayoutEntries.addView(view);
		
		//Getting the references of the Buttons
		Button addRides = (Button)view.findViewById(R.id.btnAddRide);
		Button removeRides = (Button)view.findViewById(R.id.btnRemoveRide);
		Button deleteRider = (Button)view.findViewById(R.id.btnDeleteRider);
		//Setting OnClickListener for that Buttons
		addRides.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addRide(view, ridersName);
			}
		});
		
		removeRides.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeRide(view, ridersName);
			}
		});
		deleteRider.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deleteRider(ridersName);
				setupLayout();
			}
		});
		
		//Make the entry clickable
		view.setClickable(true);
		OnClickListener clickListener = new OnClickListener() {
			public void onClick(View v) {
				//Toast.makeText(context, driversCount + ". geklickt.", Toast.LENGTH_SHORT).show();
			}
		};
		OnLongClickListener longClickListener = new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				//Stuff to do on LongClick
				//Toast.makeText(context, "LongClick registriert", Toast.LENGTH_SHORT).show();
				return false;
			}
		};
		
		//Set OnClickListeners
		view.setOnClickListener(clickListener);
		view.setOnLongClickListener(longClickListener);
		
		//Do all the necessary stuff, like setting the Name and default values of price and rides
		refreshEntry(view, ridersName, rides);		
		
		
		if (onCreate == false) {
			try {
				datasource.open();
				datasource.createRider(ridersName, defaultRides);
				datasource.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void refreshEntry(final View view, final String ridersName, int rides){
		try{		
			TextView tvName = (TextView)view.findViewById(R.id.tvRider);
			tvName.setId(R.id.tvRider + driversCount+1000);
			tvName.setText(ridersName);	
			
			final TextView tvPrice = (TextView)view.findViewById(R.id.tvPriceRider);
			tvPrice.setId(R.id.tvPriceRider + driversCount-1000);
			
			final EditText etRides = (EditText)view.findViewById(R.id.etRides);
			etRides.setId(R.id.etRides + driversCount);
			etRides.setText(String.valueOf(rides));
			try{
				double actualPrice = (Integer.parseInt(etRides.getText().toString())) * defaultPrice;
				tvPrice.setText(String.valueOf(actualPrice) + "0 €");
			}catch (NumberFormatException e){
				tvPrice.setText("0.00");
			}
			//Setting up a TextWatcher
			TextWatcher watcher = new TextWatcher() {
	
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					//Nothing to do here, for now
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					try{
						double actualPrice = (Integer.parseInt(etRides.getText().toString())) * defaultPrice;
						tvPrice.setText(String.valueOf(actualPrice) + "0 €");
						
						datasource.open();
						datasource.updateRides(ridersName, Integer.parseInt(etRides.getText().toString()));
						datasource.close();
						
					}catch (NumberFormatException e){
						tvPrice.setText("0.00");
					}catch (SQLException e){
						Log.e("DATABASE", "Failed to update entry: " + e.toString());
						e.printStackTrace();
					}
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					//Nothing to do here, for now
				}
			};
			//and applying it to our EditText
			etRides.addTextChangedListener(watcher);
		}
		
		catch(Exception e){
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
			Log.e("Exception", e.toString());
			e.printStackTrace();
		}
	}
	
	public void addRide(View v, String ridersName){
		try{		
			EditText etRides = (EditText)v.findViewById(R.id.etRides + v.getId());
			String currentRides = etRides.getText().toString();
			int currentRidesNumber = Integer.parseInt(currentRides);
			currentRidesNumber++;
			etRides.setText(String.valueOf(currentRidesNumber));
			
			datasource.open();
			datasource.updateRides(ridersName, currentRidesNumber);
			datasource.close();
			
		}catch(SQLException e){
			Log.e("DATABASE", "Failed to update entry: " + e.toString());
			e.printStackTrace();
		}catch(Exception e){
			Log.e("Exception", e.toString());
			e.printStackTrace();
		}

	}
	
	public void removeRide(View v, String ridersName){
		try{
			EditText etRides = (EditText)v.findViewById(R.id.etRides + v.getId());
			String currentRides = etRides.getText().toString();
			int currentRidesNumber = Integer.parseInt(currentRides);
			currentRidesNumber--;
			etRides.setText(String.valueOf(currentRidesNumber));
			
			datasource.open();
			datasource.updateRides(ridersName, currentRidesNumber);
			datasource.close();
			
		}catch(SQLException e){
			Log.e("DATABASE", "Failed to update entry: " + e.toString());
			e.printStackTrace();
		}catch(Exception e){
			Log.e("Exception", e.toString());
			e.printStackTrace();
		}
	}
	
	private void deleteRider(String ridersName) {
		try{
			datasource.open();
			datasource.deleteRider(ridersName);
			datasource.close();
		}catch (SQLException e){
			Log.w("Exception", e.toString());
			e.printStackTrace();
		}
	}
	
	public void keyboardShow(){
		InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	private void keyboardHide(){
		InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}
}
