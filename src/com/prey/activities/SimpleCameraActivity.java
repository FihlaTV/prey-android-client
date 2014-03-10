package com.prey.activities;

 

import android.annotation.SuppressLint;
import android.app.Activity;
 

import android.content.res.Configuration;
import android.hardware.Camera;

import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
 
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.prey.PreyLogger;
import com.prey.R;

public class SimpleCameraActivity extends Activity implements SurfaceHolder.Callback {

	public static SimpleCameraActivity activity = null;
	public static Camera camera;

	public static SurfaceHolder mHolder;
	public static byte[] dataImagen = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_camera);
		
		Bundle extras=getIntent().getExtras();
		 String focus=extras.getString("focus");
		 PreyLogger.i("focus:"+focus);
		 
		
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		try {
			int numberOfCameras = Camera.getNumberOfCameras();
			if (numberOfCameras == 1) {
				camera = Camera.open();
				PreyLogger.i("open camera()");
			} else {
				if ("front".equals(focus)){
					camera = Camera.open(0);
					PreyLogger.i("open camera(0)");
				} else {
					camera = Camera.open(1);
					PreyLogger.i("open camera(1)");
				}
			}

		} catch (Exception e) {
			PreyLogger.e(" camera error:"+e.getMessage(),e);
		}
		if (camera==null){
			try {
				camera = Camera.open(0); 
			} catch (Exception e) {
			}
		}
		activity = this;
	}

	@SuppressLint("NewApi")
	public void takePicture() {
		try {
			if (camera != null) {
				Camera.Parameters parameters = camera.getParameters();
			 	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					parameters.set("orientation", "portrait");
					parameters.set("rotation", 90);
				}
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					parameters.set("orientation", "landscape");
					parameters.set("rotation", 180);
				} 
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				camera.setParameters(parameters);
				
			}
		} catch (Exception e) {
		}	
		
		try {	
			if(camera!=null){
				camera.startPreview();
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				PreyLogger.i("open takePicture()");
			}
		} catch (Exception e) {
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			dataImagen = data;
		}
	};

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		PreyLogger.i("camera setPreviewDisplay()");
		mHolder = holder;
		try {
			if (camera != null)
				camera.setPreviewDisplay(mHolder);
		} catch (Exception e) {
			PreyLogger.e("Error PreviewDisplay:" + e.getMessage(), e);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		PreyLogger.i("camera surfaceDestroyed()");
		if (camera != null) {
			try{
				camera.stopPreview();
			}catch(Exception e){
			}
			try{
				camera.release();
			}catch(Exception e){
			}
			camera = null;
		}
	}

}
