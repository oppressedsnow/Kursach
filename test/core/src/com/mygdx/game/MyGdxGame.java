package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;



//import jdk.tools.jmod.Main;

public class MyGdxGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private Rectangle MainCharacter;
	private Texture MCImage;
	SpriteBatch batch;
	Texture background;
	Texture MC;
	//тут був я (Євгеша))
	private float VelocityY; // текущая вертикальная скорость объекта
	private boolean isJumping = false; // флаг, указывающий, прыгает ли объект
	private static final float Jump_height = 100f; // максимальная высота прыжка
	private static final float Gravity = -200f; // ускорение свободного падения в м/с^2 (влияет на висоту прыжка я хз)


	@Override
	public void create () {

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080);
		batch = new SpriteBatch();
		MCImage = new Texture(Gdx.files.internal("pp.jpg"));
		background = new Texture(Gdx.files.internal("bg0.png")); // картинка 1920х1080 (все працює чотко)
		MainCharacter = new Rectangle();
		MainCharacter.x = 1920 / 4 - 1080 / 4;
		MainCharacter.y = 0;
		MainCharacter.width = 64;
		MainCharacter.height = 64;
	}



	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		batch.begin();
		batch.draw(background, 0, 0);
		batch.end();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(MCImage, MainCharacter.x, MainCharacter.y);
		batch.end();
		if(MainCharacter.x <= 0) MainCharacter.x = 0;
		if(MainCharacter.x >= 1920 - 64) MainCharacter.x = 1920 - 64;
		if(MainCharacter.y >= 1080 - 64) MainCharacter.y = 1080 - 64;
		if(Gdx.input.isKeyPressed(Input.Keys.A)) MainCharacter.x -= 400 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.D)) MainCharacter.x += 400 * Gdx.graphics.getDeltaTime();

		if (isJumping) { // объект движется только если он прыгает
			VelocityY += Gravity * Gdx.graphics.getDeltaTime();
			MainCharacter.y += VelocityY * Gdx.graphics.getDeltaTime();
		}
		if (MainCharacter.y <= 0) { // объект остановится, когда достигнет поверхности
			MainCharacter.y = 0;
			VelocityY = 0;
			isJumping = false;
		}
		else if (MainCharacter.y >= Jump_height) { // переключаем персонажа в режим падения
			VelocityY = -Gravity * Gdx.graphics.getDeltaTime(); // устанавливаем начальную скорость падения
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isJumping) {
			VelocityY = 200; // задаем начальную вертикальную скорость объекта при прыжке
			isJumping = true;
		}

	}

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
