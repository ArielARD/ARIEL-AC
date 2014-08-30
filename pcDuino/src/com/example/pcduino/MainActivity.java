package com.example.pcduino;

import java.io.IOException;
import java.net.UnknownHostException;

import com.codeminders.ardrone.ARDrone;
import com.codeminders.ardrone.NavData;
import com.codeminders.ardrone.NavDataListener;

import android.app.Activity;
import android.net.sip.SipAudioCall.Listener;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.method.TextKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity{
	
	ARDrone 	drone = null;
	Handler 	handler;
	
	TextView 	roll;
	TextView 	pitch;
	TextView 	yaw;
	TextView 	Battery;
	

	Sensor sensor1;
	Sensor sensor2;
	Sensor sensor3;

	
	boolean		flagData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sensor1 = new Sensor();
		sensor2 = new Sensor();
		sensor3 = new Sensor();
		
		try {
			drone = new ARDrone();
			handler = new Handler();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		roll = (TextView) findViewById(R.id.roll);
		pitch = (TextView) findViewById(R.id.pitch);
		yaw = (TextView) findViewById(R.id.Yaw);
		Battery = (TextView) findViewById(R.id.Battery);
		
		sensor1.DisText = (TextView) findViewById(R.id.Distance1);
		sensor1.adc = new ADC();
		sensor1.pin = Integer.parseInt("2");
		sensor1.defDist = 4.0;
		sensor1.defCount = 5;
		sensor1.defTime = 3000;
		
		sensor1.setDistance = (EditText) findViewById(R.id.SetDis1);
		sensor1.maxErrors = (EditText) findViewById(R.id.SetMaxErorr1);
		sensor1.maxtime = (EditText) findViewById(R.id.SetMaxTime1);
		
		sensor2.DisText = (TextView) findViewById(R.id.Distance1);
		sensor2.adc = new ADC();
		sensor2.pin = Integer.parseInt("3");
		sensor2.defDist = 4.0;
		sensor2.defCount = 5;
		sensor2.defTime = 3000;
		
		sensor2.setDistance = (EditText) findViewById(R.id.SetDis2);
		sensor2.maxErrors = (EditText) findViewById(R.id.SetMaxErorr2);
		sensor2.maxtime = (EditText) findViewById(R.id.SetMaxTime2);
		
		sensor3.DisText = (TextView) findViewById(R.id.Distance1);
		sensor3.adc = new ADC();
		sensor3.pin = Integer.parseInt("4");
		sensor3.defDist = 4.0;
		sensor3.defCount = 5;
		sensor3.defTime = 3000;
		
		sensor3.setDistance = (EditText) findViewById(R.id.SetDis3);
		sensor3.maxErrors = (EditText) findViewById(R.id.SetMaxErorr3);
		sensor3.maxtime = (EditText) findViewById(R.id.SetMaxTime3);
	
		flagData = false;	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void Connect(View v) {
		try {
			drone.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disConnect(View v) {
		try {
			drone.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playLed(View v) {
		try {
			drone.playLED(2, 10, 3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void clear(View v) {
		try {
			drone.clearEmergencySignal();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setEmer(View v) {
		try {
			drone.sendEmergencySignal();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getSensorDis(final Sensor sensor) {
		Thread threadSen = new Thread(new Runnable() {	
			public void run() {
				double start = System.currentTimeMillis();
				while (flagData) {
					try {	
						double stop = System.currentTimeMillis();
						double time = stop - start;
						sensor.pinValue = sensor.adc.analogRead(sensor.pin);
						sensor.volts = sensor.pinValue * 0.0012207;
						if (sensor.volts >= sensor.defDist) sensor.count100m++;
						if (sensor.count100m >= sensor.defCount && (time < sensor.defTime)) {
							drone.playLED(2, 10, 1);
							start = System.currentTimeMillis();
							stop = start;
							time = 0;
							sensor.count100m = 0;
						}
						if (time>=sensor.defTime) {
							start = System.currentTimeMillis();
							stop = start;
							time = 0;
							sensor.count100m = 0;
						}
 						Thread.sleep(200);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		threadSen.start();
	}

	public void takeOff(View v) {
		try {
			drone.takeOff();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Land(View v) {
		try {
			drone.land();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void OnOffData(View v) {
		if (!flagData) {
			flagData = true;
		}
		else {
			flagData = false;
		}
		startData();
	}

	public void startData() {
		NavDataListener nd = new NavDataListener() {
			@Override
			public void navDataReceived(final NavData nd) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						roll.setText(nd.getRoll()+"");
						pitch.setText(nd.getPitch()+"");
						Battery.setText(nd.getBattery()+"");
						yaw.setText(nd.getYaw()+"");
						sensor1.DisText.setText(Double.toString(sensor1.volts).substring(0,5));
					}
				});
			}
		};
		getSensorDis(sensor1);
		//getSensorDis(sensor2);
		//getSensorDis(sensor3);
		if (flagData) drone.addNavDataListener(nd);
		else drone.removeNavDataListener(nd);
	}
	
	public void setDis1(View v) {
		String temp = sensor1.setDistance.getText().toString();
		sensor1.defDist = Double.parseDouble(temp);
	}
	public void setMaxErorr1(View v) {
		String temp = sensor1.maxErrors.getText().toString();
		sensor1.defCount = Integer.parseInt(temp);
	}
	public void setMaxTime1(View v) {
		String temp = sensor1.maxtime.getText().toString();
		sensor1.defTime = (int) Double.parseDouble(temp);
	}
	
	public void setDis2(View v) {
		String temp = sensor2.setDistance.getText().toString();
		sensor2.defDist = Double.parseDouble(temp);
	}
	public void setMaxErorr2(View v) {
		String temp = sensor2.maxErrors.getText().toString();
		sensor2.defCount = Integer.parseInt(temp);
	}
	public void setMaxTime2(View v) {
		String temp = sensor2.maxtime.getText().toString();
		sensor2.defTime = (int) Double.parseDouble(temp);
	}
	
	public void setDis3(View v) {
		String temp = sensor3.setDistance.getText().toString();
		sensor3.defDist = Double.parseDouble(temp);
	}
	public void setMaxErorr3(View v) {
		String temp = sensor3.maxErrors.getText().toString();
		sensor3.defCount = Integer.parseInt(temp);
	}
	public void setMaxTime3(View v) {
		String temp = sensor3.maxtime.getText().toString();
		sensor3.defTime = (int) Double.parseDouble(temp);
	}
	
	public void up(View v) {}
	public void down(View v) {}
	public void left(View v) {}
	public void right(View v) {}
	
	public void upPit(View v) {}
	public void downPit(View v) {}
	public void leftYaw(View v) {}
	public void rightYaw(View v) {}
}
