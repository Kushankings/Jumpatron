package com.kushankings.jumpatron;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class Jumpatron extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture man[];
	int manState = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0f;
	int manY = 0;
	Random random ;
	Rectangle manRectangle;
	int score=0;
	BitmapFont font;
	Texture dizzy;
	Texture jumpstart;
	Texture gameover;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<Rectangle>();

	Texture coin;
	int coinCount;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<Rectangle>();



	Texture bomb;
	int bombCount;
	int gamestate = 0;
	int speedB = 10;
	int speedC = 6;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		manRectangle = new Rectangle();

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		int manY = Gdx.graphics.getHeight() /2;

		random = new Random();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		dizzy = new Texture("dizzy-1.png");
		jumpstart = new Texture("jumpstart.png");
		gameover = new Texture("gameover.png");


	}
	public void makeCoin() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());

	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gamestate == 1){
			//Game is Live

			//COINS MOVEMENT

			if(coinCount < 100){
				coinCount++;
			}else {
				coinCount =0;
				makeCoin();
			}

			coinRectangle.clear();
			for(int i=0 ; i< coinXs.size() ; i++)
			{
				if(speedC % 6 == 0){
					speedC= speedC + 3;
				}

				batch.draw(coin,coinXs.get(i),coinYs.get(i));

				coinXs.set(i,coinXs.get(i)-speedC);
				coinRectangle.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}

			//BOMBS MOVEMENT

			if(bombCount < 250){
				bombCount++;
			}else {
				bombCount =0;
				makeBomb();
			}

			bombRectangle.clear();
			for(int i=0 ; i< bombXs.size() ; i++)
			{
				if(speedB%10==0){
					speedB= speedB + 3;
				}

				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i,bombXs.get(i)-speedB);
				bombRectangle.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			//TOUCH

			if(Gdx.input.isTouched()){
				velocity = -10;
			}

			// PAUSE AND RUN
			if(pause < 8){
				pause++;
			}else {
				pause = 0;

				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;

			if(manY <= 0){
				manY =0;
			}

		}else if(gamestate == 0){
			//Waiting To Start

			batch.draw(jumpstart,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

			if(Gdx.input.justTouched()){
				gamestate =1;
			}

		}else if (gamestate == 2){
			//GAME OVER
			batch.draw(gameover,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
			//font.draw(batch,"Tap To\nPlay Again",150,2000);
			if(Gdx.input.justTouched()){
				gamestate =1;
			 manY = Gdx.graphics.getHeight() /2;
			 score = 0;
			 speedB = 10;
			 speedC = 10;
			 velocity = 0;
			 coinXs.clear();
			 coinYs.clear();
			 coinRectangle.clear();
			 coinCount=0;

				bombXs.clear();
				bombYs.clear();
				bombRectangle.clear();
				bombCount=0;

			}

		}


// BASIC DRAWS COMMON

		if (gamestate == 2) {
			batch.draw(dizzy,Gdx.graphics.getWidth() /2 - man[manState].getWidth() /2   ,manY);

			
		}else {
			batch.draw(man[manState],Gdx.graphics.getWidth() /2 - man[manState].getWidth() /2   ,manY);
		}

		manRectangle= new Rectangle(Gdx.graphics.getWidth() /2 - man[manState].getWidth() /2   ,manY, man[manState].getWidth(), man[manState].getHeight());

		for(int i=0; i<coinRectangle.size() ;i++){
			if(Intersector.overlaps(manRectangle , coinRectangle.get(i))){

				Gdx.app.log("Coin ","Collision");
				score++;

				coinRectangle.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;

			}
		}

		for(int i=0; i<bombRectangle.size() ;i++){
			if(Intersector.overlaps(manRectangle , bombRectangle.get(i))){

				Gdx.app.log("Bomb ","Collision");
				gamestate = 2;

			}
		}

		font.draw(batch,String.valueOf(score),100,200);

		batch.end();


	}

	@Override
	public void dispose () {
		batch.dispose();

	}
}
