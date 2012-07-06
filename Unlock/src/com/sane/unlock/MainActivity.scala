package com.sane.unlock;

import org.andengine.engine.camera.Camera
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy
import org.andengine.engine.options.EngineOptions
import org.andengine.engine.options.ScreenOrientation
import org.andengine.entity.scene.background.Background
import org.andengine.entity.scene.Scene
import org.andengine.ui.activity.SimpleBaseGameActivity
import org.andengine.util.color.Color

class MainActivity extends SimpleBaseGameActivity
{
	val WIDTH = 800
	val HEIGHT = 600
  
	lazy val textures = new Textures(this)
	
	
	def onCreateResources() = {
	  textures
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

