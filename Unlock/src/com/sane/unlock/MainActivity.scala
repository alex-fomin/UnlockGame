package com.sane.unlock;

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.view.MenuItem
import android.support.v4.app.NavUtils
import org.andengine.ui.activity.SimpleBaseGameActivity
import org.andengine.engine.options.EngineOptions
import org.andengine.entity.scene.Scene
import org.andengine.engine.options.ScreenOrientation
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy
import org.andengine.engine.camera.Camera
import org.andengine.entity.scene.background.Background
import org.andengine.util.color.Color

class MainActivity extends SimpleBaseGameActivity
{
	val WIDTH = 800
	val HEIGHT = 600
  
	def onCreateResources() = {
	  
	}
	
	def onCreateEngineOptions() : EngineOptions={
	  val engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(WIDTH, HEIGHT), new Camera(0, 0, WIDTH, HEIGHT));
	  engineOptions
	} 
	
	def onCreateScene() : Scene = {
	  val scene = new Scene()
	  scene.setBackground(new Background(Color.RED))
	  scene
	}
    
}
