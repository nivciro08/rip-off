package com.rondeocreates.ripoff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;

public class Wall  extends Entity {

    Texture texture;

    public Wall( float x, float y, Stage s, World<Entity> world ) {
        this.setBounds( x, y, 10, 10 );
        item = new Item<Entity>( this );
        world.add( item, x, y, 10, 10 );

        texture = new Texture( Gdx.files.internal( "wall.png" ) );

        s.addActor( this );
    }

    @Override
    public void draw( Batch batch, float alpha ) {
        batch.draw( texture, getX(), getY(), getWidth(), getHeight() );
    }

    public void act( float dt ) {
        super.act( dt );
    }

    public void dispose() {
        texture.dispose();
    }

}