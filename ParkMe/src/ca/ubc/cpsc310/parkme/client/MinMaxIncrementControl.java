package ca.ubc.cpsc310.parkme.client;

import com.google.gwt.user.client.ui.TextBox;

public class MinMaxIncrementControl {
	final double min;
	final double max;
	final int divisions; //Number of increments from min to max value
	int currentLocation; //The number of increments from the minimum value.
	final double spacing; //Size of each increment
	TextBox textBox; //This text box will be updated whenever the currentLocation is set

	public MinMaxIncrementControl(double min, double max, int divisions, TextBox textBox, double defaultValue){
		this.min = min;
		this.max = max;
		assert(divisions>0);
		this.divisions = divisions;
		spacing=(max-min)/divisions;
		this.currentLocation =(int)((defaultValue-this.min)/spacing +.5);
		setTextBoxToRecordChangesIn(textBox);
	}
	
	public MinMaxIncrementControl(double min, double max, int divisions){
		this(min, max, divisions, null, 0);
	}

	public MinMaxIncrementControl(double min, double max, int divisions, TextBox textBox){
		this(min, max, divisions, textBox,0);		
	}
	
	public void setTextBoxToRecordChangesIn(TextBox textBox){
		this.textBox = textBox;
		setTextBoxText();	
	}

	public void setIncrements(int increments){ 
		currentLocation=increments;
		currentLocation=Math.min(currentLocation,divisions);
		currentLocation=Math.max(currentLocation,0);
		setTextBoxText();
	}
	
	public void setTextBoxText(){
		if(textBox==null) return;
		textBox.setText(""+getCurrentValue());		
	}
	
	//Increase the current value by a certain # of increments
	public void changeIncrementsBy(int inc){
		setIncrements(currentLocation+inc);
	}	
	
	public void increment(){changeIncrementsBy(1);}	
	public void decrement(){changeIncrementsBy(-1);}	
	public void setMin(){setIncrements(0);}	
	public void setMax(){setIncrements(divisions);}
	
	public void setValue(double location){
		setIncrements((int)((location-min)/spacing+.5));
	}
	
	public double getCurrentValue(){
		return min+spacing*currentLocation;
	}

}
