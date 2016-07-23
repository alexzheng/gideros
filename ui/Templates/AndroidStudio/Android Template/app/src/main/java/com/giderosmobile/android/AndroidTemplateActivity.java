package com.giderosmobile.android;

import java.lang.reflect.Method;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;

import com.giderosmobile.android.player.*;

public class AndroidTemplateActivity extends Activity implements OnTouchListener
{
	static
	{
		System.loadLibrary("gvfs");
		System.loadLibrary("lua");
		System.loadLibrary("gideros");

		System.loadLibrary("luasocket");
		System.loadLibrary("lfs");
		System.loadLibrary("lsqlite3");
		System.loadLibrary("json");
		System.loadLibrary("bitop");
		//Line below is a marker for plugin insertion scripts. Do not remove or change
		//GIDEROS-STATIC-INIT//
	}

	static private String[] externalClasses = {
		//Line below is a marker for plugin insertion scripts. Do not remove or change
		//GIDEROS-EXTERNAL-CLASS//
			null
	};
	
	private GLSurfaceView mGLView;

	private boolean mHasFocus = false;
	private boolean mPlaying = false;
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
				
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mGLView = new GiderosGLSurfaceView(this);
        mGLView.setZOrderOnTop(true);
        SurfaceHolder giderosTrackHolder = mGLView.getHolder();
        giderosTrackHolder.setFormat(PixelFormat.TRANSPARENT);
		setContentView(mGLView);
		mGLView.setOnTouchListener(this);
		
		WeakActivityHolder.set(this);

		GiderosApplication.onCreate(externalClasses);
	}

	int[] id = new int[256];
	int[] x = new int[256];
	int[] y = new int[256];
    float[] pressure = new float[256];

	@Override
	public void onStart()
	{
		super.onStart();
		GiderosApplication.getInstance().onStart();
	}

	@Override
	public void onRestart()
	{
		super.onRestart();
		GiderosApplication.getInstance().onRestart();
	}

	@Override
	public void onStop()
	{
		GiderosApplication.getInstance().onStop();
		super.onStop();
	}

	@Override
	public void onDestroy()
	{
		GiderosApplication.onDestroy();
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		if (mPlaying == true)
		{
			GiderosApplication.getInstance().onPause();
			mGLView.onPause();
			mPlaying = false;
		}
		
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (mHasFocus == true && mPlaying == false)
		{
			mGLView.onResume();
			GiderosApplication.getInstance().onResume();
			mPlaying = true;
		}
	}
	
	@Override
	public void onLowMemory()
	{
		super.onLowMemory();

		GiderosApplication app = GiderosApplication.getInstance();
		if (app != null)
			app.onLowMemory();
	}
	 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		GiderosApplication.getInstance().onActivityResult(requestCode, resultCode, data);
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
	        if (hasFocus) {
	        	getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	        			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	        			| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	        			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	        			| View.SYSTEM_UI_FLAG_FULLSCREEN
	        			| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	        }
	    }
		
		mHasFocus = hasFocus;

		if (mHasFocus == true && mPlaying == false)
		{
			mGLView.onResume();
			GiderosApplication.getInstance().onResume();
			mPlaying = true;
		}
	}
	
	public boolean onTouch(View v, MotionEvent event)
	{
		GiderosApplication app = GiderosApplication.getInstance();
		if (app == null)
			return false;

		int size = event.getPointerCount();
		for (int i = 0; i < size; i++)
		{
			id[i] = event.getPointerId(i);
			x[i] = (int) event.getX(i);
			y[i] = (int) event.getY(i);
            pressure[i] = (float) event.getPressure(i);
		}

		int actionMasked = event.getActionMasked();
		boolean isPointer = (actionMasked == MotionEvent.ACTION_POINTER_DOWN || actionMasked == MotionEvent.ACTION_POINTER_UP);
		int actionIndex = isPointer ? event.getActionIndex() : 0;
				
		if (actionMasked == MotionEvent.ACTION_DOWN || actionMasked == MotionEvent.ACTION_POINTER_DOWN)
		{
			app.onTouchesBegin(size, id, x, y, pressure, actionIndex);
		} else if (actionMasked == MotionEvent.ACTION_MOVE)
		{
			app.onTouchesMove(size, id, x, y, pressure);
		} else if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_POINTER_UP)
		{
			app.onTouchesEnd(size, id, x, y, pressure, actionIndex);
		} else if (actionMasked == MotionEvent.ACTION_CANCEL)
		{
			app.onTouchesCancel(size, id, x, y, pressure);
		}

		return true;
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //GIDEROS-ACTIVTIY-ONKEYDOWN//
		GiderosApplication app = GiderosApplication.getInstance();
		if (app != null && app.onKeyDown(keyCode, event) == true)
			return true;
		
		return super.onKeyDown(keyCode, event);
    }

	
	@Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        //GIDEROS-ACTIVTIY-ONKEYUP//
		GiderosApplication app = GiderosApplication.getInstance();
		if (app != null && app.onKeyUp(keyCode, event) == true)
			return true;
		
		return super.onKeyUp(keyCode, event);
    }
    
    //GIDEROS-ACTIVTIY-METHODS//
}

class GiderosGLSurfaceView extends GLSurfaceView
{
	public GiderosGLSurfaceView(Context context)
	{
		super(context);
		setEGLContextClientVersion(2);
        setEGLConfigChooser(8,8,8,0,16,8);
		mRenderer = new GiderosRenderer();
		setRenderer(mRenderer);
		if (android.os.Build.VERSION.SDK_INT >= 11)
		{
			try
			{
				for (Method method : getClass().getMethods())
				{
					if (method.getName().equals("setPreserveEGLContextOnPause"))
					{
						method.invoke(this, true);
						break;
					}
				}
			}
			catch (Exception e)
			{
			}
		}
	}

	GiderosRenderer mRenderer;
}

class GiderosRenderer implements GLSurfaceView.Renderer
{
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		GiderosApplication.getInstance().onSurfaceCreated();
	}

	public void onSurfaceChanged(GL10 gl, int w, int h)
	{
		GiderosApplication.getInstance().onSurfaceChanged(w, h);
	}

	public void onDrawFrame(GL10 gl)
	{
		GiderosApplication app = GiderosApplication.getInstance();
		if (app != null)
			app.onDrawFrame();
	}
}
