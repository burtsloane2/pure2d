package com.funzio.pure2D.demo.textures;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Shape;
import com.funzio.pure2D.shapes.Sprite;

public class HelloTextureActivity extends StageActivity {
    private Texture mTexture;
    protected boolean mUseTexture = true;
    private PointF mTempPoint = new PointF();

    @Override
    protected int getLayout() {
        return R.layout.stage_textures;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setColor(COLOR_GREEN);
        mScene.setRenderContinueously(true);
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    // load the textures
                    loadTexture();

                    // create first obj
                    addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });
    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.cc_175, null);
        // mTexture = new DrawableTexture(mScene.getGLState(), getResources(), R.drawable.cc_175, null, true);
    }

    private void addObject(final float x, final float y) {
        // convert from screen to scene's coordinates
        mScene.screenToGlobal(x, y, mTempPoint);

        // create object
        Sprite obj = new Sprite();
        // center origin
        obj.setOriginAtCenter();
        if (mUseTexture) {
            obj.setTexture(mTexture);
        } else {
            obj.setSize(mTexture.getSize());
        }

        // set positions
        obj.setPosition(mTempPoint.x, mTempPoint.y);

        // add to scene
        mScene.addChild(obj);

        // // motion trail
        // MotionTrailShape mMotionTrail = new MotionTrailShape();
        // mMotionTrail.setNumPoints(15);
        // mMotionTrail.setStrokeRange(10, 1);
        // mMotionTrail.setStrokeColors(new GLColor(1f, 0, 0, 1f), new GLColor(1f, 0, 0, 0.5f));
        // mMotionTrail.setTarget(obj);
        // mScene.addChild(mMotionTrail);
        //
        // // animation
        // final RotateAnimator animator = new RotateAnimator(null);
        // animator.setDuration(3000);
        // animator.setPivot(x, y, 100);
        // animator.clearPivot();
        // animator.setLoop(Playable.LOOP_REPEAT);
        // obj.addManipulator(animator);
        // animator.start(360);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    addObject(event.getX(), event.getY());
                }
            });
        }

        return true;
    }

    public void onClickTextures(final View view) {
        if (view.getId() == R.id.cb_textures) {
            mUseTexture = ((CheckBox) findViewById(R.id.cb_textures)).isChecked();
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    final int num = mScene.getNumChildren();
                    for (int n = 0; n < num; n++) {
                        Shape obj = (Shape) mScene.getChildAt(n);
                        obj.setTexture(mUseTexture ? mTexture : null);
                    }
                }
            });
        }
    }
}
