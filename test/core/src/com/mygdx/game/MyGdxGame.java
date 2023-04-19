package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
//import jdk.tools.jmod.Main;

public class MyGdxGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private Rectangle MainCharacter;
	private Texture MCImage;
	SpriteBatch batch;
	Texture background;
	Texture MC;
	//тут був я (Євгеша)


	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		background = new Texture(Gdx.files.internal("background2.jpg"));
		MainCharacter = new Rectangle();
		MainCharacter.x = 800 / 4 - 64 / 4;
		MainCharacter.y = 35;
		MainCharacter.width = 64;
		MainCharacter.height = 64;
		MCImage = new Texture(Gdx.files.internal("mc1.png"));
	}

	public class Jump  {

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
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) MainCharacter.x -= 200 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) MainCharacter.x += 200 * Gdx.graphics.getDeltaTime();
		if(MainCharacter.x < 0) MainCharacter.x = 0;
		if(MainCharacter.x > 800 - 48) MainCharacter.x = 800 - 48;

		/*if(Gdx.input.isKeyPressed(Input.Keys.UP)) MainCharacter.y += 200 * Gdx.graphics.getDeltaTime();*/
	}

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
