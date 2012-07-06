package com.sane.unlock
import org.andengine.ui.activity.SimpleBaseGameActivity
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory

class Textures(activity: SimpleBaseGameActivity) {

  val engine = activity.getEngine

  val bitmapTextureAtlas = new BitmapTextureAtlas(engine.getTextureManager, 512, 512);

  BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

  val horBlockTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, activity, "horblock.png", 0, 0);
  val verBlockTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, activity, "verblock.png", 0, 60);

  engine.getTextureManager().loadTexture(bitmapTextureAtlas);

}