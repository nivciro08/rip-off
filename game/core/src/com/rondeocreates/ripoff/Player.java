package com.rondeocreates.ripoff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;
import com.dongbat.jbump.Response.Result;

public class Player extends Entity {
    private Animation<TextureRegion> running_anim, idle_anim, jump_anim;
	private TextureAtlas atlas;

    enum State {
        LEFT_IDLE,
        RIGHT_IDLE,
        LEFT_RUN,
        RIGHT_RUN,
        LEFT_JUMP,
        RIGHT_JUMP
    }

    private State state = State.RIGHT_IDLE;
    private World<Entity> world;
    private OrthographicCamera camera;

    public Player( float x, float y, Stage s, World<Entity> world, OrthographicCamera camera ) {
        this.setBounds(x, y, 46, 50 );
        item = new Item<Entity>( this );
        world.add( item, getX(), getY(), getWidth(), getHeight() );
        this.world = world;
        this.camera = camera;

		atlas = new TextureAtlas( "player.txt" );

        Array<TextureAtlas.AtlasRegion> running = atlas.findRegions( "running" );
        running_anim = new Animation<TextureRegion>( 0.1f, running );
        running_anim.setPlayMode( Animation.PlayMode.LOOP );

        Array<TextureAtlas.AtlasRegion> idle = atlas.findRegions( "idle" );
        idle_anim = new Animation<TextureRegion>( 0.1f, idle );
        idle_anim.setPlayMode( Animation.PlayMode.LOOP_PINGPONG );

        Array<TextureAtlas.AtlasRegion> jump = atlas.findRegions( "jump" );
        jump_anim = new Animation<TextureRegion>( 0.5f, jump );
        //jump_anim.setPlayMode( Animation.PlayMode.LOOP_PINGPONG );

        time = 0;
        Gdx.input.setInputProcessor( new mInput() );

        s.addActor( this );

        gravityY = -GRAVITY;
    }

    TextureRegion body_texture;
    boolean flip = false;
    boolean jumping = false;
    private float time;

    public static final float FRICTION = 250f;
    public static final float RUN_ACCELERATION = 1800f;
    public static final float RUN_SPEED = 800f;
    public static final float JUMP_SPEED = 1200f;
    public static final float BOUNCE_SPEED = 800f;
    public static final float GRAVITY = 3000f;
    public static final float JUMP_MAX_TIME = .25f;

    private float deltaX, deltaY, gravityX, gravityY;

    @Override
    public void draw( Batch batch, float alpha ) {

        flip = state == State.LEFT_IDLE || state == State.LEFT_RUN || state == State.LEFT_JUMP;
        
        switch( state ) {
            case LEFT_RUN:
            case RIGHT_RUN:
                body_texture = running_anim.getKeyFrame( time );
                batch.draw( body_texture, flip ? getX() + body_texture.getRegionWidth() : getX(), getY(), flip ? -body_texture.getRegionWidth() : body_texture.getRegionWidth(), body_texture.getRegionHeight() );
                break;
            case LEFT_IDLE:
            case RIGHT_IDLE:
                body_texture = idle_anim.getKeyFrame( time );
                batch.draw( body_texture, flip ? getX() + body_texture.getRegionWidth() : getX(), getY(), flip ? -body_texture.getRegionWidth() : body_texture.getRegionWidth(), body_texture.getRegionHeight() );
                break;
            case LEFT_JUMP:
            case RIGHT_JUMP:
            body_texture = jump_anim.getKeyFrame( time );
            batch.draw( body_texture, flip ? getX() + body_texture.getRegionWidth() : getX(), getY(), flip ? -body_texture.getRegionWidth() : body_texture.getRegionWidth(), body_texture.getRegionHeight() );
        }
    }

    public void act( float dt ) {

        time += dt;
        super.act( dt );

        deltaX += dt * gravityX;
        deltaY += dt * gravityY;
        moveBy( dt * deltaX, dt * deltaY );

        if( Gdx.input.isKeyPressed( Input.Keys.SPACE ) ) {
            jumping = true;
            deltaY = JUMP_SPEED;
            if( state == State.LEFT_RUN || state == State.LEFT_IDLE )
                state = State.LEFT_JUMP;
            if( state == State.RIGHT_JUMP || state == State.RIGHT_IDLE )
                state = State.RIGHT_JUMP;
        }

        if( Gdx.input.isKeyPressed( Input.Keys.LEFT ) ) {
            state = State.LEFT_RUN;
            deltaX = Utils.approach(deltaX, -RUN_SPEED, RUN_ACCELERATION * dt);
            //this.moveBy( -(SPEED * dt * ACC_X), 0 );
        }
        if( Gdx.input.isKeyPressed( Input.Keys.RIGHT ) ) {
            state = State.RIGHT_RUN;
            deltaX = Utils.approach(deltaX, RUN_SPEED, RUN_ACCELERATION * dt);
            //this.moveBy( SPEED * dt * ACC_X, 0 );
        }
        if( !Gdx.input.isKeyPressed( Input.Keys.LEFT ) && !Gdx.input.isKeyPressed( Input.Keys.RIGHT ) ) {
            deltaX = Utils.approach(deltaX, 0f, RUN_ACCELERATION * dt);
        }

        CollisionFilter playerCollisionFilter = new CollisionFilter() {
            @Override
            public Response filter(Item item, Item other) {
                if ( other.userData instanceof Wall ) return Response.slide;
                return null;
            }
          };
          
        Result result = world.move( item, getX(), getY(), playerCollisionFilter );
        for( int i=0; i<result.projectedCollisions.size(); i++ ){
            Collision collision = result.projectedCollisions.get(i);
            if( collision.other.userData instanceof Wall ) {
                if( collision.normal.x != 0 )
                    deltaX = collision.normal.x * BOUNCE_SPEED * dt;
                if( collision.normal.y != 0 ) {
                    deltaY = collision.normal.y * FRICTION * dt;
                    if( collision.normal.y == 1 ){
                        jumping = false;
                        gravityY = 0;
                    }
                }
                    
            }
        }

        if( result.projectedCollisions.size() <= 0 )
            gravityY = -GRAVITY;

        camera.position.set( getX(), getY(), 0 );
    }

    public void dispose() { }

    private class mInput implements InputProcessor{

        @Override
        public boolean keyDown(int keycode) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            
            if( keycode == Input.Keys.LEFT ) {
                state = State.LEFT_IDLE;
            }
            if( keycode == Input.Keys.RIGHT ) {
                state = State.RIGHT_IDLE;
            }

            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            // TODO Auto-generated method stub
            return false;
        }

    }

}
