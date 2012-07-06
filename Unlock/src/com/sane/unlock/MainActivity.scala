package com.sane.unlock;

import org.andengine.engine.camera.Camera
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy
import org.andengine.engine.options.EngineOptions
import org.andengine.engine.options.ScreenOrientation
import org.andengine.entity.scene.background.Background
import org.andengine.entity.scene.Scene
import org.andengine.ui.activity.SimpleBaseGameActivity
import org.andengine.util.color.Color
import org.andengine.extension.physics.box2d.PhysicsWorld
import com.badlogic.gdx.math.Vector2
import org.andengine.entity.primitive.Rectangle
import org.andengine.extension.physics.box2d.PhysicsFactory
import com.badlogic.gdx.physics.box2d.BodyDef
import org.andengine.entity.sprite.Sprite
import com.badlogic.gdx.physics.box2d.Body
import org.andengine.extension.physics.box2d.PhysicsConnector
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef
import org.andengine.entity.scene.IOnAreaTouchListener
import org.andengine.entity.scene.IOnSceneTouchListener
import org.andengine.input.touch.TouchEvent
import org.andengine.entity.scene.ITouchArea
import android.view.MotionEvent
import com.badlogic.gdx.physics.box2d.joints.MouseJoint
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef
import org.andengine.extension.physics.box2d.util.Vector2Pool

class MainActivity extends SimpleBaseGameActivity with IOnAreaTouchListener with IOnSceneTouchListener {
  val WIDTH = 800
  val HEIGHT = 600

  lazy val textures = new Textures(this)

  def onCreateResources() = {
    textures
  }

  def onCreateEngineOptions(): EngineOptions = {
    val engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(WIDTH, HEIGHT), new Camera(0, 0, WIDTH, HEIGHT));
    engineOptions.getTouchOptions().setNeedsMultiTouch(true)
    engineOptions
  }

  lazy val world = new PhysicsWorld(new Vector2(0, 0), true);
  object Orientation extends Enumeration {
    type Orientation = Value
    val Horizontal = Value
    val Vertical = Value
  }

  import Orientation._
  def onCreateScene(): Scene = {
    val scene = new Scene()

    createMargins(world, scene)

    scene.setBackground(new Background(Color.YELLOW))

    createBlock(100, 180, Horizontal, scene);
    createBlock(200, 180, Horizontal, scene);
    createBlock(400, 80, Vertical, scene);
    scene.registerUpdateHandler(world);

    scene.setOnSceneTouchListener(this);
    scene.setOnAreaTouchListener(this);

    scene
  }

  val BLOCK_WIDTH = 50
  val BLOCK_LENGTH = 300
  val FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f)

  def createBlock(x: Int, y: Int, orientation: Orientation, scene: Scene): Sprite = {

    val (width, height, prismatic, bodyToJoint, texture) = orientation match {
      case Horizontal => (BLOCK_WIDTH, BLOCK_LENGTH, new Vector2(0, 1), this.leftBody, textures.verBlockTexture)
      case Vertical => (BLOCK_LENGTH, BLOCK_WIDTH, new Vector2(1, 0), this.roofBody, textures.horBlockTexture)
    }

    val horBlock = new Sprite(x, y, width, height, texture, this.getVertexBufferObjectManager());
    val horBlockBody = PhysicsFactory.createBoxBody(this.world, horBlock, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);

    world.registerPhysicsConnector(new PhysicsConnector(horBlock, horBlockBody, true, false));
    horBlock.setUserData(horBlockBody)
    
    val def2 = new PrismaticJointDef();

    def2.initialize(horBlockBody, bodyToJoint, horBlockBody.getWorldCenter(), prismatic);
    def2.lowerTranslation = -10;
    def2.upperTranslation = 10;
    def2.enableLimit = true;

    this.world.createJoint(def2);

    val def1 = new DistanceJointDef();
    def1.initialize(horBlockBody, bodyToJoint, horBlockBody.getWorldCenter(), horBlockBody.getWorldCenter());
    def1.frequencyHz = 1.5f;
    def1.length = 0;
    def1.dampingRatio = 0.5f;
    this.world.createJoint(def1);

    scene.registerTouchArea(horBlock);
    scene.attachChild(horBlock);

    horBlock
  }

  var roofBody: Body = null
  var leftBody: Body = null
  def createMargins(world: PhysicsWorld, scene: Scene) = {
    val wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

    def create(x: Int, y: Int, width: Int, height: Int): Body = {
      val element = new Rectangle(x, y, width, height, this.getVertexBufferObjectManager());
      val body = PhysicsFactory.createBoxBody(this.world, element, BodyDef.BodyType.StaticBody, wallFixtureDef);
      scene.attachChild(element)
      body
    }

    create(0, HEIGHT - 2, WIDTH, 2);
    roofBody = create(0, 0, 2, HEIGHT);
    leftBody = create(0, 0, WIDTH, 2);
    create(WIDTH - 2, 0, 2, HEIGHT);
  }

  val _joints = new Array[MouseJoint](10)
  val _jointsBodyes = new Array[Body](10)
  val pixelToMeteRatio = PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

  def onAreaTouched(
    pSceneTouchEvent: TouchEvent,
    pTouchArea: ITouchArea,
    pTouchAreaLocalX: Float,
    pTouchAreaLocalY: Float): Boolean = {

    if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN || pSceneTouchEvent.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
      this.runOnUpdateThread(new Runnable() {
        def run() {
          val face = pTouchArea.asInstanceOf[Sprite];
          val i = pSceneTouchEvent.getPointerID();
          val mjActive = _joints(i);

          if (mjActive == null) {
            val vector = new Vector2(pTouchAreaLocalX / pixelToMeteRatio, pTouchAreaLocalY / pixelToMeteRatio);
            val groundBodyDef = new BodyDef();
            groundBodyDef.position.set(vector);
            _jointsBodyes(i) = world.createBody(groundBodyDef);
            _joints(i) = MainActivity.this.createMouseJoint(face, pTouchAreaLocalX, pTouchAreaLocalY, _jointsBodyes(i));
          }
        }
      });

      return true;
    }

    return false;
  }
  
  
  def onSceneTouchEvent(pScene:Scene, pSceneTouchEvent:TouchEvent) : Boolean = {
		if (this.world != null) {


			if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_MOVE) {

				this.runOnUpdateThread(new Runnable() {
					def run() {
						val i = pSceneTouchEvent.getPointerID();
						val mjActive = _joints(i);

						if (mjActive != null) { //If the MJ is active move it ..
							val vec = new Vector2(pSceneTouchEvent.getX() / pixelToMeteRatio, pSceneTouchEvent.getY() / pixelToMeteRatio);
							mjActive.setTarget(vec);

						}
					}
				});
				return true;
			}

			if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP || pSceneTouchEvent.getAction() == MotionEvent.ACTION_CANCEL
					) {

				this.runOnUpdateThread(new Runnable() {
					def run() {
						val i = pSceneTouchEvent.getPointerID();
						val mjActive = _joints(i);


						if (mjActive != null) {
							MainActivity.this.world.destroyJoint(mjActive);
							MainActivity.this.world.destroyBody(_jointsBodyes(i));
							_joints(i) = null;
							_jointsBodyes(i)=null;
						}

					}
				});

				return true;
			}

		}
		return false;
	}
  
  
  	def createMouseJoint( pFace:Sprite, pTouchAreaLocalX:Float, pTouchAreaLocalY:Float,jointBody:Body):MouseJoint= {
		val body =  pFace.getUserData().asInstanceOf[Body]
		val mouseJointDef = new MouseJointDef();

		val localPoint = Vector2Pool.obtain((pTouchAreaLocalX - pFace.getWidth() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (pTouchAreaLocalY - pFace.getHeight() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		jointBody.setTransform(localPoint, 0);

		mouseJointDef.bodyA = jointBody;
		mouseJointDef.bodyB = body;
		mouseJointDef.dampingRatio = 1f;
		mouseJointDef.frequencyHz = 30;
		mouseJointDef.maxForce = (200000.0f);
		mouseJointDef.collideConnected = true;

		val target = mouseJointDef.target;
		target.set(body.getWorldPoint(localPoint));
		Vector2Pool.recycle(localPoint);

		this.world.createJoint(mouseJointDef).asInstanceOf[MouseJoint];
	}
  
}

