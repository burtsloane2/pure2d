/**
 * ****************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ****************************************************************************
 */

/**
 * This Vertical List is a UI Component that can handle LARGE amount of data by recycling its ItemRenderers
 */
package com.funzio.pure2D.containers;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.animators.Animator;

import java.util.ArrayList;

/**
 * List is an extended Wheel that can render an array of data
 *
 * @author long
 */
public class VList<T extends Object> extends VWheel implements List {
    protected static final String TAG = VList.class.getSimpleName();

    protected Class<? extends ItemRenderer> mItemRenderer;
    protected java.util.List<T> mData;

    protected PointF mVirtualContentSize = new PointF();
    protected PointF mVirtualScrollMax = new PointF();
    protected PointF mItemSize = new PointF();
    private int mDataStartIndex = -1;

    private boolean mRepeating = false;
    private boolean mChildrenNumInvalidated = false;

    public VList() {
        super();

        // default values
        setAlignment(Alignment.HORIZONTAL_CENTER);
        setSwipeEnabled(true);

        // NOTE: important to recycle the display children
        super.setRepeating(true);
    }

    @Override
    /**
     * Completely override the super method
     */
    public void setRepeating(boolean repeating) {
        mRepeating = repeating;
    }

    @Override
    public void scrollTo(final float x, float y) {

        // add friction when scroll out of bounds
        if (!mRepeating) {
            if (y < 0) {
                y *= SCROLL_OOB_FRICTION;
            } else if (y > mVirtualScrollMax.y) {
                y = mVirtualScrollMax.y + (y - mVirtualScrollMax.y) * SCROLL_OOB_FRICTION;
            }
        }

        super.scrollTo(x, y);
    }

    @Override
    public void onAnimationEnd(final Animator animator) {
        super.onAnimationEnd(animator);

        // out of range?
        if (!mRepeating && animator == mVelocAnimator) {
            final int round = Math.round(mScrollPosition.y);
            if (round < 0) {
                snapTo(0);
                return;
            } else if (round >= mVirtualScrollMax.y) {
                snapTo(mVirtualScrollMax.y);
                return;
            }
        }
    }

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        super.onAnimationUpdate(animator, value);

        // out of range?
        if (!mRepeating && animator == mVelocAnimator) {
            final int round = Math.round(mScrollPosition.y);
            if (round < 0 || round >= mVirtualScrollMax.y) {
                mVelocAnimator.end();
            }
        }
    }

    public void setItemRenderer(Class<? extends ItemRenderer> clazz) throws Exception {
        mItemRenderer = clazz;

        final ItemRenderer item = mItemRenderer.newInstance();
        mItemSize.set(item.getSize());

        if (mData != null) {
            invalidateChildrenNum();
        }
    }

    public Class<? extends ItemRenderer> getItemClass() {
        return mItemRenderer;
    }

    public java.util.List<T> getData() {
        return mData;
    }

    public void setData(java.util.List<T> data) {
        mData = data;

        if (data == null) {
            removeAllChildren();
        } else if (mItemRenderer != null) {
            invalidateChildrenNum();
        }
    }

    /**
     * Add or remove children to fill this list efficiently
     */
    protected void updateRenderers() {
        final int num = getNeededRenderers();
        final int diff = num - mNumChildren;
        if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                try {
                    final ItemRenderer child = mItemRenderer.newInstance();
                    // auto set size
                    child.setSize(mSize.x, child.getHeight());
                    addChild((DisplayObject) child);
                } catch (InstantiationException e) {
                    Log.e(TAG, "", e);
                    break;
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "", e);
                    break;
                }
            }
        } else if (diff < 0) {
            for (int i = 0; i < -diff; i++) {
                final ItemRenderer child = (ItemRenderer) mChildren.get(num + i);
                removeChild(num + i);
            }
        }


        // update size and invalidate
        updateVirtualContentSize();
        invalidateChildrenPosition();
    }

    /**
     * Determine how many children needed to fill this list
     *
     * @return
     */
    protected int getNeededRenderers() {

        final int dataLen = mData.size();
        int num = (int) Math.ceil(mSize.y / (getCellHeight() + mGap));
        if (num < dataLen) {
            // add another extra item
            num++;
        } else if (num > dataLen) {
            num = dataLen;
        }

        return num;
    }

    @Override
    public void setSize(float w, float h) {
        super.setSize(w, h);

        // re-init items
        if (mItemRenderer != null && mData != null) {
            invalidateChildrenNum();
        }
    }

    @Override
    protected void positionChildren() {
        final int dataSize = mData.size();
        if (mNumChildren == 0 || dataSize == 0) {
            // nothing to position
            return;
        }

        final int oldStartIndex = getStartIndex();
        super.positionChildren();
        final int newStartIndex = getStartIndex();

        // find which data item index to start
        int itemIndex = 0;
        int numLoopedItems = 0;
        if (mScrollPosition.y > 0) {
            int numClippedItems = (int) Math.ceil(mScrollPosition.y / (getCellHeight() + mGap));
            itemIndex = numClippedItems;
        } else if (mScrollPosition.y < 0) {
            numLoopedItems = (int) (-mScrollPosition.y / (getCellHeight() + mGap));
            itemIndex = dataSize - numLoopedItems % dataSize;
        }

        // diff check
        if (mChildrenNumInvalidated || oldStartIndex != newStartIndex || itemIndex != mDataStartIndex) {
            mDataStartIndex = itemIndex;
            //Log.v(TAG, newStartIndex + " --- " + itemIndex);

            ItemRenderer child;
            for (int i = 0; i < mNumChildren; i++) {
                child = (ItemRenderer) mChildren.get((newStartIndex + i) % mNumChildren);
                // re-set data for child
                itemIndex = (mDataStartIndex + i) % dataSize;
                child.setData(itemIndex, mData.get(itemIndex));

                if (!mRepeating) {
                    if (mScrollPosition.y > 0) {
                        child.setVisible(mDataStartIndex + i < dataSize);
                    } else {
                        child.setVisible(i >= numLoopedItems);
                    }
                }
            }

            // base on VGroup logic
            if (getStartY() > mGap) {
                // fill the first item in to fill the space
                int index = newStartIndex - 1;
                if (index < 0) {
                    index += mNumChildren;
                }
                child = (ItemRenderer) mChildren.get(index);

                // draw the first item to fill the space
                itemIndex = mDataStartIndex % dataSize - 1;
                if (!mRepeating) {
                    if (mScrollPosition.y < 0) {
                        child.setVisible(itemIndex >= 0);
                    }
                }
                if (itemIndex < 0) {
                    itemIndex += dataSize;
                }
                // re-set data for child
                child.setData(itemIndex, mData.get(itemIndex));
            }
        }
    }

    protected void updateVirtualContentSize() {
        mDataStartIndex = -1;
        int len = mData != null ? mData.size() : 0;

        mVirtualContentSize.x = mItemSize.x > mContentSize.x ? mItemSize.x : mContentSize.x;
        mVirtualContentSize.y = getCellHeight() * len + mGap * (len - 1);

        // update scroll max
        mVirtualScrollMax.x = Math.max(0, mVirtualContentSize.x - mSize.x);
        mVirtualScrollMax.y = Math.max(0, mVirtualContentSize.y - mSize.y);
    }

    protected void invalidateChildrenNum() {
        mChildrenNumInvalidated = true;
    }

    protected float getCellHeight() {
        return Math.max(mItemSize.y, mMinCellSize);
    }

    @Override
    public void updateChildren(final int deltaTime) {
        if (mChildrenNumInvalidated) {
            updateRenderers();

            // too soon to do this
            //mChildrenNumInvalidated = false;
        }

        super.updateChildren(deltaTime);

        // validate here instead
        mChildrenNumInvalidated = false;
    }

    public boolean addItem(final T item) {
        if (mData == null) {
            mData = new ArrayList<T>();
        }
        mData.add(item);

        invalidateChildrenNum();

        return false;
    }

    public boolean addItem(final T item, final int index) {
        if (index < 0) {
            return false;
        }

        if (mData == null) {
            mData = new ArrayList<T>();
        }

        mData.add(Math.min(index, mData.size()), item);

        invalidateChildrenNum();

        return false;
    }

    public boolean removeItem(final T item) {
        if (mData == null) {
            return false;
        }

        if (mData.remove(item)) {
            invalidateChildrenNum();

            return true;
        }

        return false;
    }

    public boolean removeItem(final int index) {
        if (mData == null || index < 0 || index >= mData.size()) {
            return false;
        }

        if (mData.remove(index) != null) {
            invalidateChildrenNum();

            return true;
        }

        return false;
    }

    public boolean removeAllItems() {
        if (mData == null) {
            return false;
        }

        removeAllChildren();
        mData.clear();

        return true;
    }

    @Override
    public void onItemTouch(MotionEvent event, ItemRenderer item) {
        Log.v(TAG, "onItemTouch(), Index:" + item.getDataIndex() + ": " + item.getData());
    }
}