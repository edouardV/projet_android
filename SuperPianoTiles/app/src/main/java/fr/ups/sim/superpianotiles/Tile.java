package fr.ups.sim.superpianotiles;

import android.graphics.Point;
import android.graphics.RectF;

/**
 * Created by keke on 13/03/2016.
 */
public class Tile{
    private int number;
    private RectF rect;

    public Tile(int num, float left, float top, float right, float bottom){
        number =num;
        rect = new RectF(left, top, right, bottom);
    }

    public int getNumber() {
        return number;
    }

    public RectF getRect() {
        return rect;
    }

    public void translate(float i){
        rect.set(rect.left, rect.top+i, rect.right, rect.bottom+i);
    }

    public boolean contains(float xFloat, float yFloat){
        return rect.contains(xFloat, yFloat);
    }

}
