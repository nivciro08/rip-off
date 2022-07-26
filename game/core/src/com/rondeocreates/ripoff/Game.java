package com.rondeocreates.ripoff;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.dongbat.jbump.World;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
	ExtendViewport	viewport;
	Stage stage;

	World<Entity> world;
	Player player;
	Array<Entity> entities;

	final String MAP =
	"+--------------------------------------------------------------------------------------------------+\n" +
	"+------------------------+----------------------------------------------------------------------++-+\n" +
	"+------------------------+------------------------------------------------------------------++-----+\n" +
	"+-++--++-----------------+----------------+-+-+-+-+-+-----------------------------------++---------+\n" +
	"+------p-----------------+--------------------------------+------+------------------++-------------+\n" +
	"+-----------+++++++------+------++------------------------++++++++--------------++-----------------+\n" +
	"+-----------------+------+------++----------------------------------------++-----------------------+\n" +
	"+-----------------+-------------++-----------------------------------------------------------------+\n" +
	"+-----------------+-------------++-----------------------------------------------------------------+\n" +
	"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n" +
	"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n" +
	"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n" +
	"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
	
	@Override
	public void create () {
		camera = new OrthographicCamera();
		viewport = new ExtendViewport( 812/2, 375/2 );
		viewport.setCamera(camera);

		batch = new SpriteBatch();

		world = new World<Entity>(1f);
		stage = new Stage( viewport, batch );
		//player = new Player( 0, 0, stage, world );

		//stage.addActor( player );

		entities = new Array<Entity>();
		String[] lines = MAP.split("\n");
        for (int j = 0; j < lines.length; j++) {
            String line = lines[j];
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == '+') {
                    entities.add(new Wall(i*10, (lines.length - j)*10, stage, world));
                } else if (line.charAt(i) == 'p') {
					player = new Player(i*10, (lines.length - j)*10, stage, world, camera );
                    entities.add( player );
                }
            }
        }
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);

		stage.act( Gdx.graphics.getDeltaTime() );
		stage.draw();
		
	}

	@Override
	public void resize( int width, int height ) {
		viewport.update( width, height, true );
		batch.setProjectionMatrix( camera.combined );
	}
	
	@Override
	public void dispose () {
		stage.dispose();
		//player.dispose();
	}
}
