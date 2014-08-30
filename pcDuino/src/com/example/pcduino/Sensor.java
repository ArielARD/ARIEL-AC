package com.example.pcduino;

import android.widget.EditText;
import android.widget.TextView;

public class Sensor {

	ADC 		adc;
	int 		pin;
	int 		pinValue;
	double 		volts;
	int 		count100m;
	TextView 	DisText;
	
	double 		defDist;
	int 		defCount;
	int 		defTime;
	
	EditText	setDistance;
	EditText 	maxErrors;
	EditText 	maxtime;
	
	
	public Sensor() {
		
		adc = new ADC();
		pin = 0;
		pinValue = 0;
		volts = 0;
		count100m = 0;
		
		defDist = 4;
		defCount = 5;
		defTime = 3000;
		
	}

}
