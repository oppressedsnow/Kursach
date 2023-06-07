package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;


public class MyGdxGame extends ApplicationAdapter {

	private OrthographicCamera camera;
	private Rectangle MainCharacter;
	private Rectangle GroundRectangle;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	float prevx;
	float prevy;
	private Texture MCImage;
	private Texture Ground;
	private Texture Coin;
	private Sound CoinPick;
	private Music forest;
	private BitmapFont font;
	SpriteBatch batch;
	Texture background;
	private float VelocityY; // текущая вертикальная скорость объекта
	private boolean isJumping = false; // флаг, указывающий, прыгает ли объект
	private static final float Gravity = -600f; // ускорение свободного падения в м/с^2 (влияет на висоту прыжка я хз)
	int ScoreNum;
	String YourScoreNum;

	@Override
	public void create () {
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080);
		batch = new SpriteBatch();
		MCImage = new Texture(Gdx.files.internal("idle.png")); //картінка нашого перса
		background = new Texture(Gdx.files.internal("bg0.png")); // картинка 1920х1080 (все працює чотко)
		MainCharacter = new Rectangle(84, 256, MCImage.getWidth(), MCImage.getHeight());
		MainCharacter.x = 400; //положення персонажа по х
		MainCharacter.y = 400; //положення персонажа по у
		Ground = new Texture(Gdx.files.internal("Ground.png"));
		GroundRectangle = new Rectangle(64, 64, Ground.getWidth(), Ground.getHeight());
		GroundRectangle.x = 960; //положення об'єкта по х
		GroundRectangle.y = 120; //положення об'єкта по у
		prevx = 0;
		prevy = 0;

		CoinPick = Gdx.audio.newSound(Gdx.files.internal("CoinPick.mp3"));
		forest = Gdx.audio.newMusic(Gdx.files.internal("forest.mp3"));
		forest.setLooping(true);
		forest.play();

		font = new BitmapFont();
		font.setColor(Color.RED);
		ScoreNum = 0;
		YourScoreNum = "score: 0";
	}


	private void spawnRaindrop() {
		Coin = new Texture(Gdx.files.internal("coin.png"));
		Rectangle coin = new Rectangle(64, 64, Coin.getWidth(), Coin.getHeight());
		coin.x = MathUtils.random(200, 1600);
		coin.y = 900;
		coin.width = 32;
		coin.height = 32;
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
		if(MainCharacter.x >= 1920 - 84) MainCharacter.x = 1920 - 84;
		if(MainCharacter.y >= 1080 - 256) MainCharacter.y = 1080 - 256;
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			prevx = MainCharacter.x;
			MainCharacter.x -= 650 * Gdx.graphics.getDeltaTime();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			prevx = MainCharacter.x;
			MainCharacter.x += 650 * Gdx.graphics.getDeltaTime();
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
			VelocityY = 650; // задаем начальную вертикальную скорость объекта при прыжке
			isJumping = true;
		}
		if (MainCharacter.y > 0){
			isJumping = true;
		}
		if (GroundRectangle.overlaps(MainCharacter)){
			MainCharacter.x = prevx;
			if (MainCharacter.y > GroundRectangle.y){
				MainCharacter.y = GroundRectangle.y + 64;
				VelocityY = 0;
				isJumping = false;

			}
		}
		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle coin = iter.next();
			coin.y -= 200 * Gdx.graphics.getDeltaTime();
			if(coin.y + 64 < 0) iter.remove();
			if(coin.overlaps(MainCharacter)) {
				ScoreNum++;
				YourScoreNum = "score: " + ScoreNum;
				CoinPick.play();
				iter.remove();
			}
		}
		batch.begin();
		font.draw(batch, YourScoreNum, 100, 1000);
		batch.end();
	}

	@Override
	public void dispose () {
		font.dispose();
		batch.dispose();
		Coin.dispose();
		background.dispose();
		MCImage.dispose();
		CoinPick.dispose();
		forest.dispose();
	}
}