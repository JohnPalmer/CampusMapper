package net.movelab.cmlibrary;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class LifelineGLSurfaceView extends GLSurfaceView {

	private LifelineRenderer mRenderer;
	private float mPreviousX = 0;
	private float mPreviousY = 0;
	private float mDensity;

	public float TOUCH_SCALE_FACTOR = 180.0f / 320;

	public LifelineGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LifelineGLSurfaceView(Context context) {
		super(context);

	}

	public void setRenderer(LifelineRenderer renderer, Float density) {
		mDensity = density;
		mRenderer = renderer;
		super.setRenderer(renderer);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		float x = e.getX();
		float y = e.getY();

		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:

			float dx = (x - mPreviousX) * (TOUCH_SCALE_FACTOR / mDensity);
			float dy = (y - mPreviousY) / (200f / mDensity);

			mRenderer.mAngle += dx;

			if ((dy > 0 && mRenderer.mZpos <= LifelineRenderer.MAXZPOS)
					|| (dy < 0 && mRenderer.mZpos >= LifelineRenderer.MINZPOS)) {
				mRenderer.mZpos += dy;
			}

			requestRender();
		}

		mPreviousX = x;
		mPreviousY = y;
		return true;
	}

}
