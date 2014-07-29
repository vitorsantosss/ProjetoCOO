import java.awt.Color;
import java.util.ArrayList;

public class Main {
	
	/* Constantes relacionadas aos estados que os elementos   */
	/* do jogo (player, projeteis ou inimigos) podem assumir. */
	
	public static final int INACTIVE = 0;
	public static final int ACTIVE = 1;
	public static final int EXPLODING = 2;
	

	/* Espera, sem fazer nada, até que o instante de tempo atual seja */
	/* maior ou igual ao instante especificado no parâmetro "time.    */
	
	public static void busyWait(long time){
		
		while(System.currentTimeMillis() < time) Thread.yield();
	}
	
	/* Encontra e devolve o primeiro índice do  */
	/* array referente a uma posição "inativa". */
	
	public static int findFreeIndex(int [] stateArray){
		
		int i;
		
		for(i = 0; i < stateArray.length; i++){
			
			if(stateArray[i] == INACTIVE) break;
		}
		
		return i;
	}
	
	/* Encontra e devolve o conjunto de índices (a quantidade */
	/* de índices é defnida através do parâmetro "amount") do */
	/* array, referentes a posições "inativas".               */ 

	public static int [] findFreeIndex(int [] stateArray, int amount){

		int i, k;
		int [] freeArray = { stateArray.length, stateArray.length, stateArray.length };
		
		for(i = 0, k = 0; i < stateArray.length && k < amount; i++){
				
			if(stateArray[i] == INACTIVE) { 
				
				freeArray[k] = i; 
				k++;
			}
		}
		
		return freeArray;
	}
	
	/* Método principal */
	
	public static void main(String [] args){

		/* Indica que o jogo está em execução */
		boolean running = true;

		/* variáveis usadas no controle de tempo efetuado no main loop */
		long delta;
		long currentTime = System.currentTimeMillis();

		/* variáveis do player */
		Jogador jogador = Jogador.getJogador();
		jogador.setEstado(new EstadoAtivo());								// estado
		jogador.setX(GameLib.WIDTH / 2);					// coordenada x
		jogador.setY(GameLib.HEIGHT * 0.90);				// coordenada y
		jogador.setVelocidadeX(0.25);								// velocidade no eixo x
		jogador.setVelocidadeY(0.25);							// velocidade no eixo y
		jogador.setRaio(12.0);							// raio (tamanho aproximado do player)
		jogador.setInicioExplosao(0);						// instante do início da explosão
		jogador.setFimExplosao(0);						// instante do final da explosão
		jogador.setProximoTiro(currentTime);					// instante a partir do qual pode haver um próximo tiro

		/* variaveis dos projeteis disparados pelo player */
		ArrayList<Projetil> projetilJogador = new ArrayList<Projetil>();

		/* variaveis dos inimigos tipo 1 */
		ArrayList<Inimigo> inimigoTipo1 = new ArrayList<Inimigo>();
		for(int i = 0; i < 10; i++) inimigoTipo1.add(new Inimigo());
		
		/* variaveis dos inimigos tipo 2 */
		ArrayList<Inimigo> inimigoTipo2 = new ArrayList<Inimigo>();
		for(int i = 0; i < 10; i++) inimigoTipo2.add(new Inimigo());
		
		/* variáveis dos projéteis lançados pelos inimigos (tanto tipo 1, quanto tipo 2) */
		ArrayList<Projetil> projetilInimigo = new ArrayList<Projetil>();
		for(int i = 0; i < 200; i++) projetilInimigo.add(new Projetil());
		
		double e_projectile_radius = 2.0;						// raio (tamanho dos projéteis inimigos)
		
		/* estrelas que formam o fundo de primeiro e segundo plano */
		ArrayList<EstrelaFundo> fundo = new ArrayList<EstrelaFundo>();
		fundo.add(new EstrelaFundo(0.070, 0.0));
		fundo.add(new EstrelaFundo(0.045, 0.0));
		
		/* Inicializacoes */
		
		for(int i = 0; i < projetilJogador.size(); i++){
			projetilJogador.get(i).setEstado(new EstadoInativo());
		}
		
		for(int i = 0; i < projetilInimigo.size(); i++){
			projetilInimigo.get(i).setEstado(new EstadoInativo());
			projetilInimigo.get(i).setRaio(2.0);
		}
		
		for(int i = 0; i < inimigoTipo1.size(); i++){
			inimigoTipo1.get(i).setEstado(new EstadoInativo());
			inimigoTipo1.get(i).setRaio(9.0);
			inimigoTipo1.get(i).setProximoInimigo(currentTime + 2000);
		}
		
		for(int i = 0; i < inimigoTipo2.size(); i++){
			inimigoTipo2.get(i).setEstado(new EstadoInativo());
			inimigoTipo2.get(i).setXProximo(GameLib.WIDTH * 0.20); // coordenada x do próximo inimigo tipo 2 a aparecer
			inimigoTipo2.get(i).setContagem(0); // contagem de inimigos tipo 2 (usada na "formação de voo")
			inimigoTipo2.get(i).setRaio(12.0); // raio (tamanho aproximado do inimigo 2)
			inimigoTipo2.get(i).setProximoInimigo(currentTime + 7000); // instante em que um novo inimigo 2 deve aparecer
		}
		
		for(EstrelaFundo background : fundo){
			for(int i = 0; i < 50; i++){
				background.getX().add(Math.random() * GameLib.WIDTH);
				background.getY().add(Math.random() * GameLib.HEIGHT);
			}
		}
		
		/* iniciado interface grafica */
		
		GameLib.initGraphics();
		
		/*************************************************************************************************/
		/*                                                                                               */
		/* Main loop do jogo                                                                             */
		/*                                                                                               */
		/* O main loop do jogo possui executa as seguintes operações:                                    */
		/*                                                                                               */
		/* 1) Verifica se há colisões e atualiza estados dos elementos conforme a necessidade.           */
		/*                                                                                               */
		/* 2) Atualiza estados dos elementos baseados no tempo que correu desde a última atualização     */
		/*    e no timestamp atual: posição e orientação, execução de disparos de projéteis, etc.        */
		/*                                                                                               */
		/* 3) Processa entrada do usuário (teclado) e atualiza estados do player conforme a necessidade. */
		/*                                                                                               */
		/* 4) Desenha a cena, a partir dos estados dos elementos.                                        */
		/*                                                                                               */
		/* 5) Espera um período de tempo (de modo que delta seja aproximadamente sempre constante).      */
		/*                                                                                               */
		/*************************************************************************************************/
		
		while(running){
		
			/* Usada para atualizar o estado dos elementos do jogo    */
			/* (player, projéteis e inimigos) "delta" indica quantos  */
			/* ms se passaram desde a última atualização.             */
			
			delta = System.currentTimeMillis() - currentTime;
			
			/* Já a variável "currentTime" nos dá o timestamp atual.  */
			
			currentTime = System.currentTimeMillis();
			
			/***************************/
			/* Verificação de colisões */
			/***************************/
						
			if(jogador.getEstado() instanceof EstadoAtivo){
				
				/* colisões player - projeteis (inimigo) */
				
				for(int i = 0; i < projetilInimigo.size(); i++){
					
					double dx = projetilInimigo.get(i).getX() - jogador.getX();
					double dy = projetilInimigo.get(i).getY() - jogador.getY();
					double dist = Math.sqrt(dx * dx + dy * dy);
					
					if(dist < (jogador.getRaio() + e_projectile_radius) * 0.8){
						
						jogador.setEstado(new EstadoExplodido());
						jogador.setInicioExplosao(currentTime);
						jogador.setFimExplosao(currentTime + 2000); 
					}
				}
			
				/* colisões player - inimigos */
							
				for(int i = 0; i < inimigoTipo1.size(); i++){
					
					double dx = inimigoTipo1.get(i).getX() - jogador.getX();
					double dy = inimigoTipo1.get(i).getY() - jogador.getY();
					double dist = Math.sqrt(dx * dx + dy * dy);
					
					if(dist < (jogador.getRaio() + inimigoTipo1.get(i).getRaio()) * 0.8){
						
						jogador.setEstado(new EstadoExplodido());
						jogador.setInicioExplosao(currentTime);
						jogador.setFimExplosao(currentTime + 2000); 
					}
				}
				
				for(int i = 0; i < inimigoTipo2.size(); i++){
					
					double dx = inimigoTipo2.get(i).getX() - jogador.getX();
					double dy = inimigoTipo2.get(i).getX() - jogador.getY();
					double dist = Math.sqrt(dx * dx + dy * dy);
					
					if(dist < (jogador.getRaio() + inimigoTipo2.get(i).getRaio()) * 0.8){
						
						jogador.setEstado(new EstadoExplodido());
						jogador.setInicioExplosao(currentTime);
						jogador.setFimExplosao(currentTime + 2000); 
					}
				}
			}
			
			/* colisões projeteis (player) - inimigos */
			
			for(int k = 0; k < projetilInimigo.size(); k++){
				
				for(int i = 0; i < inimigoTipo1.size(); i++){
					
					if(inimigoTipo1.get(i).getEstado().equals(ACTIVE)){ //corrigir implementacao, getEstado tem que retonar um inteiro 
					
						double dx = inimigoTipo1.get(i).getX() - projetilInimigo.get(i).getX();
						double dy = inimigoTipo1.get(i).getY() - projetilInimigo.get(i).getY();
						double dist = Math.sqrt(dx * dx + dy * dy);
						
						if(dist < inimigoTipo1.get(i).getRaio()){
							
							inimigoTipo1.get(i).setEstado(new EstadoExplodido());
							inimigoTipo1.get(i).setInicioExplosao(currentTime);
							inimigoTipo1.get(i).setFimExplosao(currentTime + 500);
						}
					}
				}
				
				for(int i = 0; i < inimigoTipo2.size(); i++){
					
					if(inimigoTipo2.get(i).getEstado().equals(ACTIVE)){
						
						double dx = inimigoTipo2.get(i).getX() - projetilInimigo.get(i).getX();
						double dy = inimigoTipo2.get(i).getY() - projetilInimigo.get(i).getY();
						double dist = Math.sqrt(dx * dx + dy * dy);
						
						if(dist < inimigoTipo2.get(i).getRaio()){
							
							inimigoTipo2.get(i).setEstado(new EstadoExplodido());
							inimigoTipo2.get(i).setInicioExplosao(currentTime);
							inimigoTipo2.get(i).setFimExplosao(currentTime + 500);
						}
					}
				}
			}
				
			/***************************/
			/* Atualizações de estados */
			/***************************/
			
			/* projeteis (player) */
			
			for(int i = 0; i < projetilJogador.size(); i++){
				
				if(projetilJogador.get(i).getEstado().equals(ACTIVE)){
					
					/* verificando se projétil saiu da tela */
					if(projetilJogador.get(i).getY() < 0) {
						
						projetilJogador.get(i).setEstado(new EstadoInativo());
					}
					else {
					
						projetilJogador.get(i).setX(projetilJogador.get(i).getX() * delta);
						projetilJogador.get(i).setY(projetilJogador.get(i).getY() * delta);
					}
				}
			}
			
			/* projeteis (inimigos) */
			
			for(int i = 0; i < projetilInimigo.size(); i++){
				
				if(projetilInimigo.get(i).getEstado().equals(ACTIVE)){
					
					/* verificando se projétil saiu da tela */
					if(projetilInimigo.get(i).getY() > GameLib.HEIGHT) {
						
						projetilInimigo.get(i).setEstado(new EstadoInativo());
					}
					else {
					
						projetilInimigo.get(i).setX(projetilInimigo.get(i).getVelocidadeX() * delta);
						projetilInimigo.get(i).setY(projetilInimigo.get(i).getVelocidadeY() * delta);
					}
				}
			}
			
			/* inimigos tipo 1 */
			
			for(int i = 0; i < inimigoTipo1.size(); i++){
				
				if(inimigoTipo1.get(i).getEstado().equals(EXPLODING)){
					
					if(currentTime > inimigoTipo1.get(i).getFimExplosao()){
						
						inimigoTipo1.get(i).setEstado(new EstadoInativo());
					}
				}
				
				if(inimigoTipo1.get(i).getEstado().equals(ACTIVE)){
					
					/* verificando se inimigo saiu da tela */
					if(inimigoTipo1.get(i).getY() > GameLib.HEIGHT + 10) {
						
						inimigoTipo1.get(i).setEstado(new EstadoInativo());
					}
					else {
					
						inimigoTipo1.get(i).setX(inimigoTipo1.get(i).getVelocidade() * Math.cos(inimigoTipo1.get(i).getAngulo()) * delta);
						inimigoTipo1.get(i).setY(inimigoTipo1.get(i).getVelocidade() * Math.cos(inimigoTipo1.get(i).getAngulo()) * delta * (-1.0));
						inimigoTipo1.get(i).setAngulo(inimigoTipo1.get(i).getVelocidadeRotacao() * delta);
						
						if(currentTime > inimigoTipo1.get(i).getProximoTiro() && inimigoTipo1.get(i).getY() < jogador.getY()){
																							
							int free = findFreeIndex(projetile_projectile_states);
							
							if(free < e_projectile_states.length){
								
								e_projectile_X[free] = inimigoTipo1.get(i).getX();
								e_projectile_Y[free] = inimigoTipo1.get(i).getY();
								e_projectile_VX[free] = Math.cos(enemy1_angle[i]) * 0.45;
								e_projectile_VY[free] = Math.sin(enemy1_angle[i]) * 0.45 * (-1.0);
								e_projectile_states[free] = 1;
								
								inimigoTipo1.get(i).setProximoTiro((long) (currentTime + 200 + Math.random() * 500));
							}
						}
					}
				}
			}
			
			/* inimigos tipo 2 */
			
			for(int i = 0; i < enemy2_states.length; i++){
				
				if(enemy2_states[i] == EXPLODING){
					
					if(currentTime > enemy2_explosion_end[i]){
						
						enemy2_states[i] = INACTIVE;
					}
				}
				
				if(enemy2_states[i] == ACTIVE){
					
					/* verificando se inimigo saiu da tela */
					if(	enemy2_X[i] < -10 || enemy2_X[i] > GameLib.WIDTH + 10 ) {
						
						enemy2_states[i] = INACTIVE;
					}
					else {
						
						boolean shootNow = false;
						double previousY = enemy2_Y[i];
												
						enemy2_X[i] += enemy2_V[i] * Math.cos(enemy2_angle[i]) * delta;
						enemy2_Y[i] += enemy2_V[i] * Math.sin(enemy2_angle[i]) * delta * (-1.0);
						enemy2_angle[i] += enemy2_RV[i] * delta;
						
						double threshold = GameLib.HEIGHT * 0.30;
						
						if(previousY < threshold && enemy2_Y[i] >= threshold) {
							
							if(enemy2_X[i] < GameLib.WIDTH / 2) enemy2_RV[i] = 0.003;
							else enemy2_RV[i] = -0.003;
						}
						
						if(enemy2_RV[i] > 0 && Math.abs(enemy2_angle[i] - 3 * Math.PI) < 0.05){
							
							enemy2_RV[i] = 0.0;
							enemy2_angle[i] = 3 * Math.PI;
							shootNow = true;
						}
						
						if(enemy2_RV[i] < 0 && Math.abs(enemy2_angle[i]) < 0.05){
							
							enemy2_RV[i] = 0.0;
							enemy2_angle[i] = 0.0;
							shootNow = true;
						}
																		
						if(shootNow){

							double [] angles = { Math.PI/2 + Math.PI/8, Math.PI/2, Math.PI/2 - Math.PI/8 };
							int [] freeArray = findFreeIndex(e_projectile_states, angles.length);

							for(int k = 0; k < freeArray.length; k++){
								
								int free = freeArray[k];
								
								if(free < e_projectile_states.length){
									
									double a = angles[k] + Math.random() * Math.PI/6 - Math.PI/12;
									double vx = Math.cos(a);
									double vy = Math.sin(a);
										
									e_projectile_X[free] = enemy2_X[i];
									e_projectile_Y[free] = enemy2_Y[i];
									e_projectile_VX[free] = vx * 0.30;
									e_projectile_VY[free] = vy * 0.30;
									e_projectile_states[free] = 1;
								}
							}
						}
					}
				}
			}
			
			/* verificando se novos inimigos (tipo 1) devem ser "lançados" */
			
			if(currentTime > nextEnemy1){
				
				int free = findFreeIndex(enemy1_states);
								
				if(free < enemy1_states.length){
					
					enemy1_X[free] = Math.random() * (GameLib.WIDTH - 20.0) + 10.0;
					enemy1_Y[free] = -10.0;
					enemy1_V[free] = 0.20 + Math.random() * 0.15;
					enemy1_angle[free] = 3 * Math.PI / 2;
					enemy1_RV[free] = 0.0;
					enemy1_states[free] = ACTIVE;
					enemy1_nextShoot[free] = currentTime + 500;
					nextEnemy1 = currentTime + 500;
				}
			}
			
			/* verificando se novos inimigos (tipo 2) devem ser "lançados" */
			
			if(currentTime > nextEnemy2){
				
				int free = findFreeIndex(enemy2_states);
								
				if(free < enemy2_states.length){
					
					enemy2_X[free] = enemy2_spawnX;
					enemy2_Y[free] = -10.0;
					enemy2_V[free] = 0.42;
					enemy2_angle[free] = (3 * Math.PI) / 2;
					enemy2_RV[free] = 0.0;
					enemy2_states[free] = ACTIVE;

					enemy2_count++;
					
					if(enemy2_count < 10){
						
						nextEnemy2 = currentTime + 120;
					}
					else {
						
						enemy2_count = 0;
						enemy2_spawnX = Math.random() > 0.5 ? GameLib.WIDTH * 0.2 : GameLib.WIDTH * 0.8;
						nextEnemy2 = (long) (currentTime + 3000 + Math.random() * 3000);
					}
				}
			}
			
			/* Verificando se a explosão do player já acabou.         */
			/* Ao final da explosão, o player volta a ser controlável */
			if(jogador.getEstado() instanceof EstadoExplodido){
				
				if(currentTime > jogador.getFimExplosao()){
					
					jogador.setEstado(new EstadoAtivo());;
				}
			}
			
			/********************************************/
			/* Verificando entrada do usuário (teclado) */
			/********************************************/
			
			if(jogador.getEstado() instanceof EstadoAtivo){
				
				if(GameLib.iskeyPressed(GameLib.KEY_UP)) jogador.setY(jogador.getY() - (delta * jogador.getVelocidadeY()));
				if(GameLib.iskeyPressed(GameLib.KEY_DOWN)) jogador.setY(jogador.getY() + (delta * jogador.getVelocidadeY()));
				if(GameLib.iskeyPressed(GameLib.KEY_LEFT)) jogador.setX(jogador.getX() - (delta * jogador.getVelocidadeX()));
				if(GameLib.iskeyPressed(GameLib.KEY_RIGHT)) jogador.setX(jogador.getX() + (delta * jogador.getVelocidadeX()));
				if(GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {
					
					if(currentTime > jogador.getProximoTiro()){
						
						int free = findFreeIndex(projectile_states);
												
						if(free < projectile_states.length){
							
							projectile_X[free] = jogador.getX();
							projectile_Y[free] = jogador.getY() - 2 * jogador.getRaio();
							projectile_VX[free] = 0.0;
							projectile_VY[free] = -1.0;
							projectile_states[free] = 1;
							jogador.setProximoTiro(currentTime + 100);
						}
					}	
				}
			}
			
			if(GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) running = false;
			
			/* Verificando se coordenadas do player ainda estão dentro	*/
			/* da tela de jogo após processar entrada do usuário.       */
			
			if(jogador.getX() < 0.0) jogador.setX(0.0);
			if(jogador.getX() >= GameLib.WIDTH) jogador.setY(GameLib.WIDTH - 1);
			if(jogador.getY() < 25.0) jogador.setY(25.0);
			if(jogador.getY() >= GameLib.HEIGHT) jogador.setY(GameLib.HEIGHT - 1);

			/*******************/
			/* Desenho da cena */
			/*******************/
			
			/* desenhando plano fundo distante */
			
			GameLib.setColor(Color.DARK_GRAY);
			background2_count += background2_speed * delta;
			
			for(int i = 0; i < background2_X.length; i++){
				
				GameLib.fillRect(background2_X[i], (background2_Y[i] + background2_count) % GameLib.HEIGHT, 2, 2);
			}
			
			/* desenhando plano de fundo próximo */
			
			GameLib.setColor(Color.GRAY);
			background1_count += background1_speed * delta;
			
			for(int i = 0; i < background1_X.length; i++){
				
				GameLib.fillRect(background1_X[i], (background1_Y[i] + background1_count) % GameLib.HEIGHT, 3, 3);
			}
						
			/* desenhando player */
			
			if(jogador.getEstado() instanceof EstadoExplodido){
				
				double alpha = (currentTime - jogador.getInicioExplosao()) / (jogador.getFimExplosao() - jogador.getInicioExplosao());
				GameLib.drawExplosion(jogador.getX(), jogador.getY(), alpha);
			}
			else{
				
				GameLib.setColor(Color.BLUE);
				GameLib.drawPlayer(jogador.getX(), jogador.getY(), jogador.getRaio());
			}
				
			
			/* deenhando projeteis (player) */
			
			for(int i = 0; i < projectile_states.length; i++){
				
				if(projectile_states[i] == ACTIVE){
					
					GameLib.setColor(Color.GREEN);
					GameLib.drawLine(projectile_X[i], projectile_Y[i] - 5, projectile_X[i], projectile_Y[i] + 5);
					GameLib.drawLine(projectile_X[i] - 1, projectile_Y[i] - 3, projectile_X[i] - 1, projectile_Y[i] + 3);
					GameLib.drawLine(projectile_X[i] + 1, projectile_Y[i] - 3, projectile_X[i] + 1, projectile_Y[i] + 3);
				}
			}
			
			/* desenhando projeteis (inimigos) */
		
			for(int i = 0; i < e_projectile_states.length; i++){
				
				if(e_projectile_states[i] == ACTIVE){
	
					GameLib.setColor(Color.RED);
					GameLib.drawCircle(projetilInimigo.get(i).getX(), projetilInimigo.get(i).getY(), e_projectile_radius);
				}
			}
			
			/* desenhando inimigos (tipo 1) */
			
			for(int i = 0; i < enemy1_states.length; i++){
				
				if(enemy1_states[i] == EXPLODING){
					
					double alpha = (currentTime - enemy1_explosion_start[i]) / (enemy1_explosion_end[i] - enemy1_explosion_start[i]);
					GameLib.drawExplosion(inimigoTipo1.get(i).getX(), inimigoTipo1.get(i).getY(), alpha);
				}
				
				if(enemy1_states[i] == ACTIVE){
			
					GameLib.setColor(Color.CYAN);
					GameLib.drawCircle(inimigoTipo1.get(i).getX(), inimigoTipo1.get(i).getY(), enemy1_radius);
				}
			}
			
			/* desenhando inimigos (tipo 2) */
			
			for(int i = 0; i < enemy2_states.length; i++){
				
				if(enemy2_states[i] == EXPLODING){
					
					double alpha = (currentTime - enemy2_explosion_start[i]) / (enemy2_explosion_end[i] - enemy2_explosion_start[i]);
					GameLib.drawExplosion(enemy2_X[i], enemy2_Y[i], alpha);
				}
				
				if(enemy2_states[i] == ACTIVE){
			
					GameLib.setColor(Color.MAGENTA);
					GameLib.drawDiamond(enemy2_X[i], enemy2_Y[i], enemy2_radius);
				}
			}
			
			/* chamama a display() da classe GameLib atualiza o desenho exibido pela interface do jogo. */
			
			GameLib.display();
			
			/* faz uma pausa de modo que cada execução do laço do main loop demore aproximadamente 5 ms. */
			
			busyWait(currentTime + 5);
		}
		
		System.exit(0);
	}
}
