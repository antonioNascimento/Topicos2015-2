package br.grupointegrado.spaceinvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Created by Antonio on 03/08/2015.
 */
public class TelaGame extends TelaBase {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage palco;
    private Stage palcoInformacoes;
    private BitmapFont fonte;
    private Label lbPontuacao;
    private Label lbGameOver;
    private Image jogador;
    private Texture texturaJogador;
    private Texture texturaJogadorDireita;
    private Texture texturaJogadorEsquerda;
    private boolean indoDireita;
    private boolean indoEsquerda;
    private boolean atirando;
    private Array<Image> tiros = new Array<Image>();
    private Texture texturaTiro;
    private Texture texturaMeteoro1;
    private Texture texturaMeteoro2;
    private Array<Image> meteoro1 = new Array<Image>();
    private Array<Image> meteoro2 = new Array<Image>();
    private Array<Texture> texturasExplosao = new Array<Texture>();
    private Array<Explosao> explosoes = new Array<Explosao>();


    /**
     * Construtor padrão da tela de jogo
     *
     * @param mainGame Referência para a classe principal
     */
    public TelaGame(MainGame mainGame) {
        super(mainGame);
    }


    /**
     * Chamado quando a tela é exibida
     */
    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));
        palcoInformacoes = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initTexturas();
        initFonte();
        initInformacoes();
        initJogador();
    }

    private void initTexturas() {
        texturaTiro = new Texture("sprites/shot.png");
        texturaMeteoro1 = new Texture("sprites/enemie-1.png");
        texturaMeteoro2 = new Texture("sprites/enemie-2.png");

        for (int i = 1; i <= 17; i++) {
            Texture texture = new Texture("sprites/explosion-" + i + ".png");
            texturasExplosao.add(texture);
        }
    }

    /**
     * Instancia os objetos do Jogador e adiciona no palco.
     */
    private void initJogador() {
        texturaJogador = new Texture("sprites/player.png");
        texturaJogadorDireita = new Texture("sprites/player-right.png");
        texturaJogadorEsquerda = new Texture("sprites/player-left.png");

        jogador = new Image(texturaJogador);
        float x = camera.viewportWidth / 2 - jogador.getWidth() / 2;
        float y = 15;
        jogador.setPosition(x, y);
        palco.addActor(jogador);
    }

    /**
     * Instancia as informações escritas na Tela.
     */
    private void initInformacoes() {
        Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;

        lbPontuacao = new Label("Pontuação: 0 ", lbEstilo);
        palcoInformacoes.addActor(lbPontuacao);

        lbGameOver = new Label("GAME OVER", lbEstilo);
        lbGameOver.setVisible(false);
        palcoInformacoes.addActor(lbGameOver);
    }

    /**
     * Instancia os objetos de Fonte.
     */
    private void initFonte() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.WHITE;
        parameter.size = 24;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = Color.BLACK;

        //fonte = new BitmapFont();
        fonte = generator.generateFont(parameter);
        generator.dispose();
    }

    /**
     * Chamado a todo quadro de atualização do Jogo (FPS)
     *
     * @param delta Tempo entre um quadro e outro (em segundos)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbPontuacao.setPosition(10, camera.viewportHeight - lbPontuacao.getPrefHeight() - 10);

        lbPontuacao.setText(pontuacao + " pontos");
        lbGameOver.setPosition(camera.viewportWidth / 2 - lbGameOver.getPrefWidth() / 2, camera.viewportHeight / 2);
        lbGameOver.setVisible(gameOver);

        atualizarExplosoes(delta);

        if (!gameOver) {
            capturaTeclas();
            atualizarJogador(delta);
            atualizarTiros(delta);
            atualizarMeteoros(delta);
            detectarColisoes(meteoro1, 5);
            detectarColisoes(meteoro2, 10);
        }
        // Atualiza a situação do Palco
        palco.act(delta);
        // Desenha o palco na Tela
        palco.draw();

        // Desenha o palco de INformações
        palcoInformacoes.act(delta);
        palcoInformacoes.draw();
    }

    private void atualizarExplosoes(float delta) {
        for (Explosao explosao : explosoes) {
            //Verifica se a Explosão chegou ao fim
            if (explosao.getEstagio() >= 16) {
                explosoes.removeValue(explosao, true); // Remove a explosão do Array
                explosao.getAtor().remove(); // Remove o ator do palco
            } else {
                // Explosão não chegou ao fim
                explosao.atualizar(delta);
            }
        }
    }

    private void detectarColisoes(Array<Image> meteoros, int valorPonto) {
        recJogador.set(jogador.getX(), jogador.getY(), jogador.getImageWidth(), jogador.getImageHeight());
        for (Image meteoro : meteoros) {
            recMeteoro.set(meteoro.getX(), meteoro.getY(), meteoro.getImageWidth(), meteoro.getImageHeight());
            // Detecta Colisão com o Tiro
            for (Image tiro : tiros) {
                recTiro.set(tiro.getX(), tiro.getY(), tiro.getImageWidth(), tiro.getImageHeight());
                if (recMeteoro.overlaps(recTiro)) {
                    // Colisão do Tiro com o Meteoro
                    pontuacao += valorPonto;
                    tiro.remove();
                    tiros.removeValue(tiro, true); // Remove da Lista
                    meteoro.remove(); // Remove do Palco
                    meteoros.removeValue(meteoro, true);
                    criarExplosao(meteoro.getX(), meteoro.getY());
                }
            }
        }
        // Detecta Colisão com o Player
        if (recJogador.overlaps(recMeteoro)) {
            // Colisão com o Player
            gameOver = true;
        }
    }

    /**
     * Cria a Explosao na posição X e Y
     *
     * @param x
     * @param y
     */
    private void criarExplosao(float x, float y) {
        Image ator = new Image(texturasExplosao.get(0));
        ator.setPosition(x, y);
        palco.addActor(ator);

        Explosao explosao = new Explosao(ator, texturasExplosao);
        explosoes.add(explosao);
    }

    private Rectangle recJogador = new Rectangle();
    private Rectangle recTiro = new Rectangle();
    private Rectangle recMeteoro = new Rectangle();
    private boolean gameOver = false;
    private int pontuacao = 0;

    private void atualizarMeteoros(float delta) {

        int qtdMeteoros = meteoro1.size + meteoro2.size; // Retorna qtde de meteoros criados

        if (qtdMeteoros < 5) {
            int tipo = MathUtils.random(1, 4);
            if (tipo == 1) {
                // Cria o Meteoro 1
                Image meteoro = new Image(texturaMeteoro1);
                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoro1.add(meteoro);
                palco.addActor(meteoro);
            } else if (tipo == 2) {
                // Cria o Meteoro 2
                Image meteoro = new Image(texturaMeteoro2);
                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoro1.add(meteoro);
                palco.addActor(meteoro);
            }
        }

        float velocidade1 = 150;
        for (Image meteoro : meteoro1) {
            float x = meteoro.getX();
            float y = meteoro.getY() - velocidade1 * delta;
            meteoro.setPosition(x, y); //Atualiza a posição do Meteoro
            // Remove os tiros que sairam da Tela
            if (meteoro.getY() + meteoro.getHeight() < 0) {
                meteoro.remove(); // Remove do Palco
                meteoro1.removeValue(meteoro, true); // Remove da Lista
            }
        }

        float velocidade2 = 150;
        for (Image meteoro : meteoro1) {
            float x = meteoro.getX();
            float y = meteoro.getY() - velocidade2 * delta;
            meteoro.setPosition(x, y); //Atualiza a posição do Meteoro
            // Remove os tiros que sairam da Tela
            if (meteoro.getY() + meteoro.getHeight() < 0) {
                meteoro.remove(); // Remove do Palco
                meteoro2.removeValue(meteoro, true); // Remove da Lista
            }
        }

    }


    private final float MIN_INTERVALO_TIROS = 0.3f; // Minimo de Tempo entre os Tiros
    private float intervaloTiros = 0;   // Tempo acumulado entre os Tiros

    private void atualizarTiros(float delta) {
        intervaloTiros = intervaloTiros + delta; // Acumula o tmepo percorrido
        // Cria um novo tiro se necessário
        if (atirando) {
            // Verifica se o Tempo minimo foi atingido
            if (intervaloTiros >= MIN_INTERVALO_TIROS) {
                Image tiro = new Image(texturaTiro);
                float x = jogador.getX() + jogador.getWidth() / 2 - tiro.getWidth() / 2;
                float y = jogador.getY() + jogador.getHeight();
                tiro.setPosition(x, y);
                tiros.add(tiro);
                palco.addActor(tiro);
                intervaloTiros = 0;
            }
        }
        float velocidade = 300; // Velocidade de Movimentação do Tiro
        // Percorre todos os Tiros existentes
        for (Image tiro : tiros) {
            // Movimenta o tiro em direção ao Topo
            float x = tiro.getX();
            float y = tiro.getY() + velocidade * delta;
            tiro.setPosition(x, y);
            // Remove os tiros que sairam da Tela
            if (tiro.getY() > camera.viewportHeight) {
                tiros.removeValue(tiro, true);  // Remove da Lista
                tiro.remove();  // Remove do Palco
            }
        }
    }

    /**
     * Atualiza a posição do Jogador
     *
     * @param delta
     */
    private void atualizarJogador(float delta) {
        float velocidade = 400; // Velocidade de movimento do Jogador

        // Verifica se o jogador está indo para Direita e se está dentro da Tela
        if (indoDireita && jogador.getX() < camera.viewportWidth - jogador.getWidth()) {
            float x = jogador.getX() + velocidade * delta;
            float y = jogador.getY();
            jogador.setPosition(x, y);
        }


        // Verifica se o jogador está indo para Esquerda e se está dentro da Tela
        if (indoEsquerda && jogador.getX() > 0) {
            float x = jogador.getX() - velocidade * delta;
            float y = jogador.getY();
            jogador.setPosition(x, y);
        }

        if (indoDireita) {
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogadorDireita)));
        } else if (indoEsquerda) {
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogadorEsquerda)));
        } else {
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogador)));
        }

    }

    /**
     * Verifica se as teclas estão pressionadas
     */
    private void capturaTeclas() {
        indoDireita = false;
        indoEsquerda = false;
        atirando = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            indoEsquerda = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            indoDireita = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            atirando = true;
        }
    }

    /**
     * Chamado sempre que há uma alteração no tamanho da tela
     *
     * @param width  Novo valor de largura da tela
     * @param height Novo valor de altura da tela
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    /**
     * Chamado sempre que o jogo for minimizado
     */
    @Override
    public void pause() {

    }

    /**
     * Chamado sempre que o jogo voltar para o primeiro plano
     */
    @Override
    public void resume() {

    }

    /**
     * Chamado quando a tela for destruida
     */
    @Override
    public void dispose() {
        batch.dispose();
        palco.dispose();
        palcoInformacoes.dispose();
        fonte.dispose();
        texturaJogador.dispose();
        texturaJogadorDireita.dispose();
        texturaJogadorEsquerda.dispose();
        texturaTiro.dispose();
        texturaMeteoro1.dispose();
        texturaMeteoro2.dispose();

        for (Texture texture : texturasExplosao) {
            texture.dispose();
        }
    }

}
