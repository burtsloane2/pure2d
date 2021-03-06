/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;
import android.view.animation.Interpolator;

import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
@Deprecated
public class MoveRadiusAnimator extends TweenAnimator {
    protected float mSrcX = 0;
    protected float mSrcY = 0;
    protected PointF mDelta = new PointF();

    public MoveRadiusAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float srcX, final float srcY, final float distance, final float radianAngle) {
        mSrcX = srcX;
        mSrcY = srcY;

        mDelta.x = distance * (float) Math.cos(radianAngle) - srcX;
        mDelta.y = distance * (float) Math.sin(radianAngle) - srcY;
    }

    public void setValues(final float srcX, final float srcY, final float distance, final int degreeAngle) {
        mSrcX = srcX;
        mSrcY = srcY;

        mDelta.x = distance * (float) Math.cos(degreeAngle * Pure2DUtils.DEGREE_TO_RADIAN) - srcX;
        mDelta.y = distance * (float) Math.sin(degreeAngle * Pure2DUtils.DEGREE_TO_RADIAN) - srcY;
    }

    public void setValues(final float distance, final float radianAngle) {
        mDelta.x = distance * (float) Math.cos(radianAngle);
        mDelta.y = distance * (float) Math.sin(radianAngle);
    }

    public void setValues(final float distance, final int degreeAngle) {
        mDelta.x = distance * (float) Math.cos(degreeAngle * Pure2DUtils.DEGREE_TO_RADIAN);
        mDelta.y = distance * (float) Math.sin(degreeAngle * Pure2DUtils.DEGREE_TO_RADIAN);
    }

    public void start(final float srcX, final float srcY, final float distance, final float radianAngle) {
        setValues(srcX, srcY, distance, radianAngle);

        start();
    }

    public void start(final float srcX, final float srcY, final float distance, final int degreeAngle) {
        setValues(srcX, srcY, distance, degreeAngle);

        start();
    }

    public void start(final float distance, final float radianAngle) {
        if (mTarget != null) {
            final PointF position = mTarget.getPosition();
            start(position.x, position.y, distance, radianAngle);
        }
    }

    public void start(final float distance, final int degreeAngle) {
        if (mTarget != null) {
            final PointF position = mTarget.getPosition();
            start(position.x, position.y, distance, degreeAngle);
        }
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            if (mAccumulating) {
                mTarget.move((value - mLastValue) * mDelta.x, (value - mLastValue) * mDelta.y);
            } else {
                mTarget.setPosition(mSrcX + value * mDelta.x, mSrcY + value * mDelta.y);
            }
        }

        super.onUpdate(value);
    }

    public PointF getDelta() {
        return mDelta;
    }
}
