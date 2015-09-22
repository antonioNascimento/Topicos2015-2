package br.grupointegrado.spaceinvaders;

import com.badlogic.gdx.Game;

public class MainGame extends Game {

    @Override
    public void create() {
        setScreen(new TelaMenu(this));
    }
}
