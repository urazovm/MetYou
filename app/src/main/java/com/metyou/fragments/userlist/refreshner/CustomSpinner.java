package com.metyou.fragments.userlist.refreshner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

/**
 * Created by mihai on 8/27/14.
 */
public class CustomSpinner extends View {
    private int angle;
    private SpinningArc spinningArc;
    private int spinningRotation;
    private boolean autoRotate;

    public CustomSpinner(Context context) {
        super(context);
        autoRotate = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(spinningRotation, spinningArc.getCenterX(), spinningArc.getCenterY());
        spinningArc.draw(canvas);
        if (autoRotate) {
            spinningRotation = (spinningRotation + 10) % 360;
            postInvalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        spinningArc = new SpinningArc(0, 0, w, h);
    }

    public void setSpinningArcRotation(int rotation) {
        spinningRotation = rotation;
        postInvalidate();
    }

    public void animateRefresh() {
        spinningArc.activateSpring();
    }

    public void autoRotate(boolean mode) {
        this.autoRotate = mode;
        if (autoRotate) {
            postInvalidate();
        }
    }

    public boolean isAutoRotating() {
        return this.autoRotate;
    }

    class SpinningArc extends Drawable {
        private Paint mPaint;
        private RectF oval;
        private float strokeWidth;
        private SpringSystem springSystem;
        private Spring spring;
        private float margins;
        private float distance;


        public SpinningArc (float left, float top, float right, float bottom) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            strokeWidth = 3                                                                                                                                                                                                                                                                                                                                                                     ;
            distance = 40;
            margins = strokeWidth/2 + distance;

            mPaint.setColor(getResources().getColor(android.R.color.holo_red_dark));
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(strokeWidth);

            springSystem = SpringSystem.create();
            spring = springSystem.createSpring();
            spring.setSpringConfig(new SpringConfig(60,7));

            final float l = left + margins;
            final float t = top + margins;
            final float r = right - margins;
            final float b = bottom - margins;

            spring.addListener(new SpringListener() {
                @Override
                public void onSpringUpdate(Spring spring) {
                    float dif = (float)spring.getCurrentValue();
                    oval.set(l - dif, t - dif , r + dif, b + dif);
                    if (!autoRotate) {
                        postInvalidate();
                    }
                    if (dif > margins/2) {
                        spring.setEndValue(0);
                    }
                }

                @Override
                public void onSpringAtRest(Spring spring) {
                        Log.d("Spring", "at rest");
                }

                @Override
                public void onSpringActivate(Spring spring) {
                }

                @Override
                public void onSpringEndStateChange(Spring spring) {
                }
            });

            oval = new RectF(l, t, r, b);
        }

        float getCenterX() {
            return oval.centerX();
        }

        float getCenterY() {
            return oval.centerY();
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawArc(oval, 270, 150, false, mPaint);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }

        public void activateSpring() {
            spring.setEndValue(distance);
        }
    }
}
