package com.mygdx.flappybird;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Random;

public class  MyGdxGame extends ApplicationAdapter {

	SpriteBatch batch;
	Texture[] passaros;
	Texture fundo;
	Texture canoBaixo;
	Texture canoTopo;
	Texture gameOver;
	Texture startLogo;
	Texture startTexto;
	Texture[] moeda;
	Texture pontuacao;

	ShapeRenderer shapeRenderer;

	Circle circuloPassaro;
	Circle circuloMoeda;
	Rectangle retanguloCanoCima;
	Rectangle retanguloCanoBaixo;
	Rectangle retanguloChao;
	Rectangle retanguloTopo;

	float larguraDispositivo;
	float alturaDispositivo;
	float variacao = 0;
	float gravidade = 2;
	float posicaoInicialVerticalPassaro;
	float posicaoCanoHorizontal;
	float posicaoMoedaHorizontal;
	float posicaoCanoVertical;
	float posicaoMoedaVertical;
	float espacoEntreCanos;
	float chaoMorteHorizontal;
	float topoMorteHorizontal;
	float chaoMorteVertical;
	float topoMorteVertical;
	Random random;
	int pontos = 0;
	int pontuacaoMaxima = 0;
	boolean passouCano = false;
	int estadoJogo = 0;
	float posicaoHorizontalPassaro;

	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;

	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;
	Sound somMoeda;

	double randomNum;
	int coinIndex;

	Preferences preferencias;

	OrthographicCamera camera;
	Viewport viewport;
	final float VIRTUAL_WIDTH = 720;
	final float VIRTUAL_HEIGHT = 1280;
	public void ApplicationAdapter(){}


	@Override
	public void create () {

		inicializarTexturas();
		inicializaObjetos();
		randomNum = Math.random();
		if (randomNum < 0.4) {
			coinIndex = 0;}
		else {
			coinIndex = 1;
		}

	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();
	}

	private void inicializarTexturas(){
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		startLogo = new Texture("Cool-Text-434103276361438.png");
		startTexto = new Texture("cooltext434103395484379.png");
		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");
		moeda = new Texture[2];
		moeda[0] = new Texture("Coin.png");
		moeda[1] = new Texture("Coin2.png");
	}

	private void inicializaObjetos(){
		batch = new SpriteBatch();
		random = new Random();

		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVerticalPassaro = alturaDispositivo/2;
		posicaoMoedaVertical = alturaDispositivo/2;
		posicaoMoedaHorizontal = larguraDispositivo;
		posicaoCanoHorizontal = larguraDispositivo;
		topoMorteVertical = alturaDispositivo;
		topoMorteHorizontal = larguraDispositivo/2;
		chaoMorteVertical = alturaDispositivo;
		espacoEntreCanos = 350;

		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(10);

		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(Color.GREEN);
		textoReiniciar.getData().setScale(2);

		textoMelhorPontuacao = new BitmapFont();
		textoMelhorPontuacao.setColor(Color.RED);
		textoMelhorPontuacao.getData().setScale(2);

		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		circuloMoeda = new Circle();
		retanguloCanoCima = new Rectangle();
		retanguloCanoBaixo = new Rectangle();
		retanguloTopo = new Rectangle();
		retanguloChao = new Rectangle();

		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		somMoeda = Gdx.audio.newSound(Gdx.files.internal("CoinSound.wav"));

		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferencias.getInteger("maxScore",0);

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2,0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
	}

	private void verificarEstadoJogo(){

		boolean toqueTela = Gdx.input.justTouched();
		if( estadoJogo == 0){
			if( toqueTela){
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}

		}else if (estadoJogo == 1){
			if(toqueTela){
				gravidade = -15;
				somVoando.play();
			}

			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			posicaoMoedaHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			if( posicaoCanoHorizontal < -canoTopo.getWidth()){
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoCanoVertical = random.nextInt(400) - 200;
				passouCano = false;
			}
			if( posicaoMoedaHorizontal < -moeda[0].getWidth()){
				posicaoMoedaHorizontal = larguraDispositivo + random.nextInt(Math.round((larguraDispositivo * .5f)));
				posicaoMoedaVertical = random.nextInt(Math.round(alturaDispositivo)) - 200;
			}
			if( posicaoInicialVerticalPassaro > 0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
			gravidade++;

		}else if( estadoJogo == 2){
			if (pontos> pontuacaoMaxima){
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
				preferencias.flush();
			}

			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime()*500;

			if(toqueTela){
				estadoJogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturaDispositivo/2;
				posicaoCanoHorizontal = larguraDispositivo;
				espacoEntreCanos = 350;
			}
		}
	}

	private void detectarColisoes(){
		circuloPassaro.set(
				50 + posicaoHorizontalPassaro + passaros[0].getWidth()/2,
				posicaoInicialVerticalPassaro + passaros[0].getHeight()/2,
				passaros[0].getWidth()/2
		);
		circuloMoeda.set(
				50 + posicaoMoedaHorizontal + moeda[0].getWidth()/2,
				posicaoMoedaVertical + moeda[0].getHeight()/2,
				moeda[0].getWidth()/2
		);
		retanguloCanoBaixo.set(
				posicaoCanoHorizontal,
				alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);
		retanguloCanoCima.set(
				posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical,
				canoTopo.getWidth(), canoTopo.getHeight()
		);
		retanguloTopo.set(
				topoMorteHorizontal - 300, topoMorteVertical + 100, larguraDispositivo, 300
		);
		retanguloChao.set(
				topoMorteHorizontal - 300, chaoMorteVertical - (chaoMorteVertical -1), larguraDispositivo, 1
		);
		randomNum = Math.random();
		boolean collidedCanoTopo = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
		boolean collidedCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);
		boolean collidedMoeda = Intersector.overlaps(circuloPassaro, circuloMoeda);
		boolean collidedMorte = Intersector.overlaps(circuloPassaro, retanguloTopo);
		boolean collidedMorte2 = Intersector.overlaps(circuloPassaro, retanguloChao);
		if(collidedMoeda){
			if(pontuacao == moeda[0]){
				pontos = pontos + 10;
			}
			else{
				pontos = pontos + 5;
			}
			somMoeda.play();
			if (randomNum < 0.2) {
				coinIndex = 0;
			}
			else {
				coinIndex = 1;
			}
			posicaoMoedaHorizontal = -alturaDispositivo;
		}
		if (collidedCanoTopo || collidedCanoBaixo){
			if (estadoJogo == 1){
				somColisao.play();
				estadoJogo = 2;
			}
		}
		if(collidedMorte){
			estadoJogo = 2;
		}
		if(collidedMorte2){
			estadoJogo = 2;
		}
	}

	private void desenharTexturas(){

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(fundo,0,0,larguraDispositivo, alturaDispositivo);
		batch.draw(passaros[(int) variacao],
				50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);
		batch.draw(canoBaixo, posicaoCanoHorizontal,
				alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 + posicaoCanoVertical);
		batch.draw(canoTopo, posicaoCanoHorizontal,
				alturaDispositivo/2 + espacoEntreCanos/2 + posicaoCanoVertical);
		textoPontuacao.draw(batch, String.valueOf(pontos), larguraDispositivo/2.2f,
				alturaDispositivo - 110);

		batch.draw(moeda[coinIndex],
				50 + posicaoMoedaHorizontal, posicaoMoedaVertical);
		pontuacao = moeda[coinIndex];
		if(estadoJogo == 2){
			batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2,
					alturaDispositivo/2);
			textoReiniciar.draw(batch,
					"Toque para reiniciar", larguraDispositivo/2 - 140,
					alturaDispositivo/2 - gameOver.getHeight()/2);
			textoMelhorPontuacao.draw(batch,
					"Seu recorde é: "+ pontuacaoMaxima + " pontos",
					larguraDispositivo/2-140, alturaDispositivo/2 - gameOver.getHeight());
		}

		if(estadoJogo == 0) {
			batch.draw(startLogo, larguraDispositivo / 2 - startLogo.getWidth() / 2,
					900);
			batch.draw(startTexto, larguraDispositivo / 2 - startTexto.getWidth() / 2,
					alturaDispositivo / 8);
		}
		batch.end();
	}

	public void validarPontos(){
		if( posicaoCanoHorizontal < 50-passaros[0].getWidth()){
			if (!passouCano){
				pontos++;
				passouCano = true;
				somPontuacao.play();
				if (pontos >= 20){
					espacoEntreCanos = 280;
				}
			}
		}

		variacao += Gdx.graphics.getDeltaTime() * 10;

		if (variacao > 3)
			variacao = 0;
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose () {
	}
}