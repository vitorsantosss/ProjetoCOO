
public class Jogador {
	
	private Estado estado;
	private static Jogador jogador = null;
	private double x;									// coordenada x
	private double y;									// coordenada y
	private double velocidadeX;							// velocidade no eixo x
	private double velocidadeY;							// velocidade no eixo y
	private double raio;							    // raio (tamanho aproximado do player)
	private double inicioExplosao;						// instante do início da explosão
	private double fimExplosao;						    // instante do final da explosão
	private long proximoTiro;
	
	private Jogador(){
		
	}
	
	public static synchronized Jogador getJogador(){
		if(jogador == null){
			jogador = new Jogador();
		}
		return jogador;
	}
	
	public double getInicioExplosao() {
		return inicioExplosao;
	}
	public void setInicioExplosao(double inicioExplosao) {
		this.inicioExplosao = inicioExplosao;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getVelocidadeX() {
		return velocidadeX;
	}
	public void setVelocidadeX(double velocidadeX) {
		this.velocidadeX = velocidadeX;
	}
	public double getVelocidadeY() {
		return velocidadeY;
	}
	public void setVelocidadeY(double velocidadeY) {
		this.velocidadeY = velocidadeY;
	}
	public double getRaio() {
		return raio;
	}
	public void setRaio(double raio) {
		this.raio = raio;
	}
	public double getFimExplosao() {
		return fimExplosao;
	}
	public void setFimExplosao(double fimExplosao) {
		this.fimExplosao = fimExplosao;
	}
	public long getProximoTiro() {
		return proximoTiro;
	}
	public void setProximoTiro(long proximoTiro) {
		this.proximoTiro = proximoTiro;
	}
}
