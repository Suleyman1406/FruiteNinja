package com.atilsamancioglu.fruitninjastarter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

public class FruitNinja extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture background;
	Texture apple;
	Texture cherry;
	Texture ruby;
	Texture bill;
	Texture heart;
	BitmapFont font;
	FreeTypeFontGenerator fontGenerator;

	int lifes =4;
	int score=0;
	int maxScore=score;
	float genCounter=0;
	private final float startGenSpeed=1.1f;
	float genSpeed=startGenSpeed;

	Random random=new Random();
	Array<Fruit> fruits=new Array<>();
	double currentTime;
	double gameOverTime=-1.0f;
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("ninjabackground.png");
		apple=new Texture("apple.png");
		cherry =new Texture("cherry.png");
		ruby=new Texture("ruby.png");
		bill=new Texture("bill.png");
		heart=new Texture("heart.png");
		Fruit.radius=Math.max(Gdx.graphics.getHeight(),Gdx.graphics.getWidth())/20f;
		Gdx.input.setInputProcessor(this);
		fontGenerator=new FreeTypeFontGenerator(Gdx.files.internal("robotobold.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter fontParameter=new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.color= Color.BLUE;
		fontParameter.size=40;
		fontParameter.characters="0123456789 MxCutoplayScre:+-=";
		font=fontGenerator.generateFont(fontParameter);

	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		double newTime= TimeUtils.millis()/1000.0;
		System.out.println(newTime+" new");
		double frameTime=Math.min(newTime-currentTime,0.3);
		System.out.println(frameTime+" frame");
		float deltaTime=(float) frameTime;
		currentTime=newTime;
		if (score>maxScore){
			maxScore=score;
		}
		if (lifes ==0 && gameOverTime==0f){
		gameOverTime=currentTime;
		}
		if (lifes >0){
			genSpeed-=deltaTime*0.015f;
			if (genCounter<=0f){
				genCounter=genSpeed;
				addItem();
			}else {
				genCounter-=deltaTime;
			}
			for (int i = 0; i < lifes; i++) {
				batch.draw(heart,i*48f+48f,Gdx.graphics.getHeight()-65f,65,65);
			}
			for (Fruit fruit : fruits) {
				fruit.uptade(deltaTime);
				switch (fruit.type){
					case REGULAR:
						batch.draw(apple,fruit.getPosition().x,fruit.getPosition().y,Fruit.radius,Fruit.radius);
						break;
					case EXTRA:
						batch.draw(cherry,fruit.getPosition().x,fruit.getPosition().y,Fruit.radius,Fruit.radius);
						break;
					case ENEMY:
						batch.draw(ruby,fruit.getPosition().x,fruit.getPosition().y,Fruit.radius,Fruit.radius);
						break;
					case LIFE:
						batch.draw(bill,fruit.getPosition().x,fruit.getPosition().y,Fruit.radius,Fruit.radius);
						break;
				}
			}
			boolean holdlives=false;
			Array<Fruit> toRemove=new Array<>();
			for (Fruit fruit : fruits) {
				if (fruit.outOfScreen()){
					toRemove.add(fruit);
					if (fruit.living &&fruit.type==Fruit.Type.REGULAR){
						lifes--;
						holdlives=true;
						break;
					}
				}
			}
			if (holdlives==true){
				for (Fruit fruit : fruits) {
					fruit.living=false;
				}
			}
			for (Fruit fruit : toRemove) {
				fruits.removeValue(fruit,true);
			}
		}
		font.draw(batch,"Score: "+score,60,60);
		font.draw(batch,"Max Score: "+maxScore,Gdx.graphics.getWidth()-300,60);
		if (lifes<=0) {
			font.draw(batch, "Cut to play", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		}
		batch.end();
	}
	private void addItem(){
		float position=random.nextFloat()*Math.max(Gdx.graphics.getHeight(),Gdx.graphics.getWidth());
		Fruit fruit=new Fruit(new Vector2(position,-Fruit.radius),new Vector2((Gdx.graphics.getWidth()*0.5f-position)*(random.nextFloat()-0.2f),Gdx.graphics.getHeight()*0.5f));
		float type=random.nextFloat();
		if (type>0.98){
			fruit.type=Fruit.Type.LIFE;
		}else if(type>0.88){
			fruit.type=Fruit.Type.EXTRA;
		}
		else if(type>0.78){
			fruit.type=Fruit.Type.ENEMY;
		}
		fruits.add(fruit);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		fontGenerator.dispose();
		font.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (lifes<=0&& currentTime-gameOverTime>2f){
			gameOverTime=0f;
			score=0;
			lifes=4;
			genSpeed=startGenSpeed;
			fruits.clear();
		}else {
			Array<Fruit> toRemove=new Array<>();
			int plusScore=0;
			Vector2 p=new Vector2(screenX,Gdx.graphics.getHeight()-screenY);
			for (Fruit fruit : fruits) {

				if (fruit.clicked(p)){
					toRemove.add(fruit);
					switch (fruit.type){
						case REGULAR:
							plusScore++;
							break;
						case EXTRA:
							plusScore+=2;
							score++;
							break;
						case ENEMY:
							lifes--;
							break;
						case LIFE:
							lifes++;
							break;
					}
				}
			}
			score+=plusScore*plusScore;
			for (Fruit f : toRemove) {
				fruits.removeValue(f,true);
			}
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
