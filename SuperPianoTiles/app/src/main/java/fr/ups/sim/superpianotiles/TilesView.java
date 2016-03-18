package fr.ups.sim.superpianotiles;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Custom view that displays tiles
 */
public class TilesView extends View {

    private int tileColor = Color.BLUE;
    private int textColor = Color.WHITE;
    private Drawable mExampleDrawable;
    private float textSize = 40;
    Paint pText = new Paint();
    Paint pTile = new Paint();
    private LinkedList<Tile> tiles;
    private int tileWidth, tileHeight;
    private boolean init;

    public TilesView(final Context context) {
        super(context);
        init(null, 0);
    }

    public TilesView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TilesView(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TilesView, defStyle, 0);


        if (a.hasValue(R.styleable.TilesView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.TilesView_exampleDrawable);
            if (mExampleDrawable != null) {
                mExampleDrawable.setCallback(this);
            }
        }

        a.recycle();
        init = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        pText.setTextSize(textSize);
        pText.setColor(textColor);
        pTile.setColor(tileColor);

        //Tiles
        if(init){
            tileWidth = contentWidth/4;
            tileHeight = contentHeight/4;

            int pos = (int)(Math.random()*4);
            tiles = new LinkedList<>();
            tiles.add(new Tile(0, pos*tileWidth, getHeight()-tileHeight, pos*tileWidth+tileWidth, getHeight()));
            addTile();
            addTile();
            addTile();
            addTile();
            init = false;
        }
        for(Tile tile : tiles){
            addTile(Integer.toString(tile.getNumber()), tile.getRect(), canvas);
        }
        drawColumns(canvas);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }

    public void addTile(String order, RectF rect, Canvas canvas){
        canvas.drawRoundRect(rect, 2, 2, pTile);
        canvas.drawText(order, rect.centerX(), rect.centerY(),pText);
    }

    private void drawColumns(Canvas canvas){
        for(int i=1 ; i<4 ; i++){
            canvas.drawLine(tileWidth*i, 0, tileWidth*i, tileHeight*4, pTile);
        }
    }

       /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }

    public void addTile(){
        Tile lastTile = tiles.getLast();
        int position = (int)(Math.random()*4);
        float left = position*tileWidth;
        float top = lastTile.getRect().top-tileHeight;
        float right = left + tileWidth;
        float bottom = top + tileHeight;
        Tile tile = new Tile(lastTile.getNumber()+1, position*tileWidth, top, right, bottom);
        tiles.add(tile);
    }

    public boolean translate(int speed){
        if(tiles.getFirst().getRect().top < tileHeight*4) {
            for (Tile tile : tiles) {
                tile.translate(speed);
            }
            invalidate();
            return true;
        }
        return false;
    }

    public int blackTile(float x, float y){
        for(Tile tile : tiles){
            if(tile.contains(x, y)){
                return tile.getNumber();
            }
        }
        return -1;
    }

    public void retry(){
        tiles.clear();
        init = true;
        invalidate();
    }

    public boolean onTouchTile(int numTile){
        if(numTile == tiles.getFirst().getNumber()){
            tiles.removeFirst();
            addTile();
            return true;
        }
        return false;
    }

    public boolean noTouchTileAnimation(){
        if(tiles.getFirst().getRect().top > tileHeight*3){
            translate(-16);
            invalidate();
            return true;
        }
        return false;
    }
}
