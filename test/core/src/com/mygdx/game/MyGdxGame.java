package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;


public class MyGdxGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private Rectangle MainCharacter;
	private Rectangle GroundRectangle;
	private Array<Rectangle> raindrops;
	private long lastDropTime;

	float MCWidth = 100;
	float MCHeight = 128;
	float prevx;
	float prevy;
	private Texture MCImage;
	private Texture Ground;
	private Texture Coin;
	private Sound CoinPick;
	private Music forest;


	SpriteBatch batch;
	Texture background;
	private float VelocityY; // текущая вертикальная скорость объекта
	private boolean isJumping = false; // флаг, указывающий, прыгает ли объект
	private static final float Gravity = -600f; // ускорение свободного падения в м/с^2 (влияет на висоту прыжка я хз)

	@Override
	public void create () {
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080);
		batch = new SpriteBatch();
		MCImage = new Texture(Gdx.files.internal("idle.png")); //картінка нашого перса
		background = new Texture(Gdx.files.internal("bg0.png")); // картинка 1920х1080 (все працює чотко)
		MainCharacter = new Rectangle(MCWidth, MCHeight, MCImage.getWidth(), MCImage.getHeight());
		MainCharacter.x = 100; //положення персонажа по х
		MainCharacter.y = 500; //положення персонажа по у
		Ground = new Texture(Gdx.files.internal("Ground.png"));
		GroundRectangle = new Rectangle(64, 64, Ground.getWidth(), Ground.getHeight());
		GroundRectangle.x = 100; //положення об'єкта по х
		GroundRectangle.y = 0; //положення об'єкта по у
		prevx = 0;
		prevy = 0;
		Coin = new Texture(Gdx.files.internal("coin.png"));
		CoinPick = Gdx.audio.newSound(Gdx.files.internal("CoinPick.mp3"));
		forest = Gdx.audio.newMusic(Gdx.files.internal("forest.mp3"));
		forest.setLooping(true);
		forest.play();
	}

	private void spawnRaindrop() {
		Rectangle coin = new Rectangle();
		coin.x = MathUtils.random(0, 800-64);
		coin.y = 480;
		coin.width = 64;
		coin.height = 64;
		raindrops.add(coin);
		lastDropTime = TimeUtils.nanoTime();

	}


	@Override
	public void render () {
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
		ScreenUtils.clear(0, 0, 0.2f, 1);
		batch.begin();
		batch.draw(background, 0, 0);
		batch.end();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(MCImage, MainCharacter.x, MainCharacter.y);
		for(Rectangle raindrop: raindrops) {
			batch.draw(Coin, raindrop.x, raindrop.y);
		}
		batch.end();
		batch.begin();
		batch.draw(Ground, GroundRectangle.x, GroundRectangle.y);
		batch.end();
		if(MainCharacter.x <= 0) MainCharacter.x = 0;
		if(MainCharacter.x >= 1920 - 64) MainCharacter.x = 1920 - 64;
		if(MainCharacter.y >= 1080 - 64) MainCharacter.y = 1080 - 64;
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			prevx = MainCharacter.x;
			MainCharacter.x -= 400 * Gdx.graphics.getDeltaTime();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			prevx = MainCharacter.x;
			MainCharacter.x += 400 * Gdx.graphics.getDeltaTime();
		}

		if (isJumping) { // объект движется только если он прыгает
			VelocityY += Gravity * Gdx.graphics.getDeltaTime();
			MainCharacter.y += VelocityY * Gdx.graphics.getDeltaTime();
		}
		if (MainCharacter.y <= 0) { // объект остановится, когда достигнет поверхности
			MainCharacter.y = 0;
			VelocityY = 0;
			isJumping = false;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !isJumping) {
			VelocityY = 600; // задаем начальную вертикальную скорость объекта при прыжке
			isJumping = true;
		}
		if (MainCharacter.y > 0){
			isJumping = true;
		}
		if (GroundRectangle.overlaps(MainCharacter)){
			if (MainCharacter.y < GroundRectangle.y + 63) {
				if (MainCharacter.x > GroundRectangle.x) {
					MainCharacter.x = GroundRectangle.x + 64;
				}
				if (MainCharacter.x < GroundRectangle.x) {
					MainCharacter.x = GroundRectangle.x - 64;
				}
			}
			if (MainCharacter.y > GroundRectangle.y){
				MainCharacter.y = GroundRectangle.y + 64;
				VelocityY = 0;
				isJumping = false;

			}
			else if (MainCharacter.y < GroundRectangle.y) {
				MainCharacter.y = GroundRectangle.y - 64;
				VelocityY = -300;
				isJumping = true;
			}
		}
		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle coin = iter.next();
			coin.y -= 200 * Gdx.graphics.getDeltaTime();
			if(coin.y + 64 < 0) iter.remove();
			if(coin.overlaps(MainCharacter)) {
				CoinPick.play();
				iter.remove();
			}
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		Coin.dispose();
		background.dispose();
		MCImage.dispose();
		CoinPick.dispose();
		forest.dispose();
	}
}