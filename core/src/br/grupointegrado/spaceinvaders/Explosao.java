package br.grupointegrado.spaceinvaders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Antonio on 31/08/2015.
 */
public class Explosao {

    private static final float tempo_troca = 1f / 17f; // Tempo de cada imagem da explosão

    private int estagio = 0; // Controla o estagio de 0 a 16
    private Array<Texture> texturas;
    private Image ator;
    private float tempoAcumulado = 0;

    public Explosao(Image ator, Array<Texture> texturas) {
        this.texturas = texturas;
        this.ator = ator;
    }

    public int getEstagio() {
        return estagio;
    }

    public Image getAtor() {
        return ator;
    }

    /**
     * Calcula o Tempo Acumulado e realiza a troca do estágio da explosão
     * Exemplo:
     * Cada quadro demora 0,016 segundos
     * Cada imagem da explosão deve permanecer 0,05 segundos
     *

     * @param delta
     */
    public void atualizar(float delta) {
        tempoAcumulado = tempoAcumulado + delta;
        if (tempoAcumulado >= tempo_troca) {
            tempoAcumulado = 0;
            estagio++;
            Texture textura = texturas.get(estagio);
            ator.setDrawable(new SpriteDrawable(new Sprite(textura)));
        }
    }
}
