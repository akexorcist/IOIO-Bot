package com.inex.ioio.robotic.ioiobot;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import android.os.Bundle;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Main extends IOIOActivity {
	LinearLayout layoutLeft, layoutRight;
	DrawCanvas drawLeft, drawRight;

	float dutyLeft, dutyRight;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    getWindow().setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN 
        		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);
		
		layoutLeft = (LinearLayout)findViewById(R.id.layoutLeft);
		layoutRight = (LinearLayout)findViewById(R.id.layoutRight);
	}
	
	public void onPause() {
		super.onPause();
		finish();
	}
	
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		drawRight = new DrawCanvas(Main.this
				, layoutRight.getWidth(), layoutRight.getHeight());
		
		drawRight.setPosition(drawRight.height / 2);
		draw(layoutRight, drawRight);
		
		drawLeft = new DrawCanvas(Main.this
				, layoutRight.getWidth(), layoutRight.getHeight());
		
		drawLeft.setPosition(drawLeft.height / 2);
		draw(layoutLeft, drawLeft);
		
		layoutRight.setOnTouchListener(new OnTouchListener() {
			float speed;
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction() == MotionEvent.ACTION_DOWN 
						|| arg1.getAction() == MotionEvent.ACTION_MOVE) {
					
					drawRight.setPosition(arg1.getY());
					draw(layoutRight, drawRight);
					
					speed = arg1.getY() - (drawRight.height / 2);
					
					if(speed > (drawRight.height / 2) - (drawRight.width / 2)) 
						speed = (drawRight.height / 2) - (drawRight.width / 2);
					else if(speed < -((drawRight.height / 2) - (drawRight.width / 2)))
						speed = -((drawRight.height / 2) - (drawRight.width / 2));
					
					speed = speed * 200 / (drawRight.height - drawRight.width);
					dutyRight = speed / 100;
					
				} else if(arg1.getAction() == MotionEvent.ACTION_UP) {

					speed = 0;
					dutyRight = 0;
					drawRight.setPosition(drawRight.height / 2);
					draw(layoutRight, drawRight);
				}
				
				return true;
			}
		});
    	
    	layoutLeft.setOnTouchListener(new OnTouchListener() {
			float speed;
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction() == MotionEvent.ACTION_DOWN 
						|| arg1.getAction() == MotionEvent.ACTION_MOVE) {
					
					drawLeft.setPosition(arg1.getY());
					draw(layoutLeft, drawLeft);

					speed = arg1.getY() - (drawLeft.height / 2);
					
					if(speed > (drawLeft.height / 2) - (drawLeft.width / 2)) 
						speed = (drawLeft.height / 2) - (drawLeft.width / 2);
					else if(speed < -((drawLeft.height / 2) - (drawLeft.width / 2)))
						speed = -((drawLeft.height / 2) - (drawLeft.width / 2));

					speed = speed * 200 / (drawLeft.height - drawLeft.width);
					dutyLeft = speed / 100;
					
				} else if(arg1.getAction() == MotionEvent.ACTION_UP) {

					speed = 0;
					dutyLeft = 0;
					drawLeft.setPosition(drawLeft.height / 2);
					draw(layoutLeft, drawLeft);
				}
				
				return true;
			}
		});
	}
	
	public void draw(LinearLayout layout, DrawCanvas draw) {
		try {
			layout.removeView(draw);
		} catch (Exception e) { }
		layout.addView(draw);
	}
	
	class Looper extends BaseIOIOLooper {
		DigitalOutput D1A, D1B, D2A, D2B, D3A, D3B, D4A, D4B;
		PwmOutput PWM1, PWM2, PWM3, PWM4;
		
		public void setup() throws ConnectionLostException {
        	D1A = ioio_.openDigitalOutput(1, false);
        	D1B = ioio_.openDigitalOutput(2, false);
        	D2A = ioio_.openDigitalOutput(4,false);
        	D2B = ioio_.openDigitalOutput(5,false);
        	D3A = ioio_.openDigitalOutput(16,false);
        	D3B = ioio_.openDigitalOutput(17,false);
        	D4A = ioio_.openDigitalOutput(18,false);
        	D4B = ioio_.openDigitalOutput(19,false);
        	PWM1 = ioio_.openPwmOutput(3, 100);
        	PWM1.setDutyCycle(0);
        	PWM2 = ioio_.openPwmOutput(6, 100);
        	PWM2.setDutyCycle(0);
        	PWM3 = ioio_.openPwmOutput(13, 100);
        	PWM3.setDutyCycle(0);
        	PWM4 = ioio_.openPwmOutput(14, 100);
        	PWM4.setDutyCycle(0);
        	
        	runOnUiThread(new Runnable() {
				public void run() {
					
					Toast.makeText(getApplicationContext(), 
							"Connected!", Toast.LENGTH_SHORT).show();
				}		
			});
		}
		
		public void loop() throws ConnectionLostException, InterruptedException { 
			if(dutyLeft > 0) {
				PWM1.setDutyCycle(dutyLeft);
				PWM2.setDutyCycle(dutyLeft);
	    		D1A.write(false);
	    		D2A.write(false);
	    		D1B.write(true);
	    		D2B.write(true);
			} else if(dutyLeft < 0) {
				PWM1.setDutyCycle(Math.abs(dutyLeft));
				PWM2.setDutyCycle(Math.abs(dutyLeft));
	    		D1A.write(true);
	    		D2A.write(true);
	    		D1B.write(false);
	    		D2B.write(false);
			} else {
				PWM1.setDutyCycle(0);
				PWM2.setDutyCycle(0);
	    		D1A.write(false);
	    		D2A.write(false);
	    		D1B.write(false);
	    		D2B.write(false);
			}
			
			if(dutyRight > 0) {
				PWM3.setDutyCycle(dutyRight);
				PWM4.setDutyCycle(dutyRight);
	    		D3A.write(false);
	    		D4A.write(false);
	    		D3B.write(true);
	    		D4B.write(true);
			} else if(dutyRight < 0) {
				PWM3.setDutyCycle(Math.abs(dutyRight));
				PWM4.setDutyCycle(Math.abs(dutyRight));
	    		D3A.write(true);
	    		D4A.write(true);
	    		D3B.write(false);
	    		D4B.write(false);
			} else {
				PWM3.setDutyCycle(0);
				PWM4.setDutyCycle(0);
	    		D3A.write(false);
	    		D4A.write(false);
	    		D3B.write(false);
	    		D4B.write(false);
			}

			Thread.sleep(50);
		}
	}

    protected IOIOLooper createIOIOLooper() {
        return new Looper();
    }
}
