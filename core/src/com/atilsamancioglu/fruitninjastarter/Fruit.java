package com.atilsamancioglu.fruitninjastarter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Fruit {
    static int maxScore=0;
    public static float radius=60f;
    public enum Type{
        REGULAR,ENEMY,EXTRA,LIFE
    }
    Type type;
    public boolean living=true;
    Vector2 position,velocity;
    public Fruit(Vector2 position,Vector2 velocity){
        this.position=position;
        this.velocity=velocity;
        type=Type.REGULAR;
    }
    public boolean clicked(Vector2 clickedV){
        if (position.dst2(clickedV)<=radius*radius+1) return true;
        return false;
    }
    public Vector2 getPosition(){
        return this.position;
    }

    public boolean outOfScreen(){
        return (position.y<-2f*radius);
    }
    public void uptade(float time){
        velocity.y-=time*(Gdx.graphics.getHeight()*0.2f);
        velocity.x-=time*Math.signum(velocity.x)*0.9f;
        position.mulAdd(velocity,time);
    }
}
