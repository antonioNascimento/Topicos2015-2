package br.grupointegrado.spaceinvaders;

import com.badlogic.gdx.Screen;

/**
 * Created by Antonio on 03/08/2015.
 */
public abstract class TelaBase implements Screen {

    protected MainGame mainGame;

    public TelaBase (MainGame mainGame) {
        this.mainGame = mainGame;
    }

    @Override
    public void hide() {
        dispose();
    }
}
