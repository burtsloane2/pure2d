/**
 * 
 */
package com.funzio.pure2D.gl.gl10;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author long
 */
public final class BlendFunc {
    private final static int DEFAULT_SRC = GL10.GL_SRC_ALPHA;
    private final static int DEFAULT_DST = GL10.GL_ONE_MINUS_SRC_ALPHA;

    public int src = DEFAULT_SRC;
    public int dst = DEFAULT_DST;

    // for GLES11Ext.glBlendFuncSeparate()
    public int src_alpha = -1;
    public int dst_alpha = -1;

    // public int equation = GL11ExtensionPack.GL_FUNC_ADD; // by default
    // For subtractive: GLES11Ext.glBlendEquationOES(equation = GL11ExtensionPack.GL_FUNC_SUBTRACT);

    public BlendFunc() {
        // nothing
    }

    public BlendFunc(final int src, final int dst) {
        this.src = src;
        this.dst = dst;
    }

    public BlendFunc(final int src, final int dst, final int srcAlpha, final int dstAlpha) {
        this.src = src;
        this.dst = dst;

        this.src_alpha = srcAlpha;
        this.dst_alpha = dstAlpha;
    }

    public void setValues(final int src, final int dst) {
        this.src = src;
        this.dst = dst;
    }

    public void set(final BlendFunc value) {
        this.src = value.src;
        this.dst = value.dst;

        this.src_alpha = value.src_alpha;
        this.dst_alpha = value.dst_alpha;
    }

    public boolean equals(final BlendFunc func) {
        return (this == func) || (src == func.src && dst == func.dst && src_alpha == func.src_alpha && dst_alpha == func.dst_alpha);
    }

    /**
     * Addictive blending
     * 
     * @return
     */
    public static BlendFunc getAdd() {
        // (src * 1) + (dst * 1) = src + dst
        return new BlendFunc(GL10.GL_ONE, GL10.GL_ONE);
    }

    /**
     * Multiplicative blending
     * 
     * @return
     */
    public static BlendFunc getMultiply() {
        // (src * dst) + (dst * 0) = src * dst
        return new BlendFunc(GL10.GL_DST_COLOR, GL10.GL_ZERO);
    }

    /**
     * Interpolative blending
     * 
     * @return
     */
    public static BlendFunc getInterpolate() {
        // (src * src_alpha) + (dst * (1 - src_apha)) = src * src_alpha + dst - (dst * src_alpha)
        return new BlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Interpolative blending, with separate alpha
     * 
     * @return
     */
    public static BlendFunc getInterpolate2() {
        // (src * src_alpha) + (dst * (1 - src_apha)) = src * src_alpha + dst - (dst * src_alpha)
        return new BlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA, GL10.GL_ONE, GL10.GL_ONE);
    }

    /**
     * Pre-multiplied Alpha
     * 
     * @return
     */
    public static BlendFunc getPremultipliedAlpha() {
        // (src * 1) + (dst * (1 - src_apha)) = src * 1 + dst - (dst * src_apha)
        return new BlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Screen blending
     * 
     * @return
     */
    public static BlendFunc getScreen() {
        // (src * 1) + (dst * (1 - src)) = src + dst - dst * src = dst + src * (1 - dst)
        return new BlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR);
    }

    /**
     * Screen-Alpha blending
     * 
     * @return
     */
    public static BlendFunc getScreenAlpha() {
        // (src * 0) + (dst * (1 - src_alpha)) = dst - dst * src_alpha
        return new BlendFunc(GL10.GL_ZERO, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static BlendFunc getInterpolateColor() {
        // (src * src_color) + (dst * (1 - src_alpha)) = src * src_color + dst - dst * src_alpha
        return new BlendFunc(GL10.GL_SRC_COLOR, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

}
