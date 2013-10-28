package com.inex.ioio.robotic.ioiobot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class DrawCanvas extends View{
 	float y;
 	int width, height;
 	Bitmap bitmap;
 	int offset = 10;
 	
 	public DrawCanvas(Context context, int width, int height) {
         super(context);
         this.width = width;
         this.height = height;

    	 bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.stick);
    	 bitmap = Bitmap.createScaledBitmap(bitmap, this.width - (offset * 2)
    			 , this.width - (offset * 2), false);
     }
     
     public void onDraw(Canvas canvas) {
    	 canvas.drawBitmap(bitmap, offset, y - (width / 2) + offset, null);
     }
     
     public void setPosition(float y) {
     	this.y = y;
     	
     	if(y < (width / 2)) 
     		this.y = (width / 2);
     	else if(y > height - (width / 2))
     		this.y = height - (width / 2);
     }
 }
