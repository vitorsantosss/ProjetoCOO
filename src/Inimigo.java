
public class Inimigo {
	private Estado estado;
	private double x;									// coordenada x
	private double y;									// coordenada y
	private double velocidade;							// velocidade
	private double angulo; 								// angulo do movimento
	private double velocidadeRotacao;
	private double inicioExplosao;						// instante do início da explosão
	private double fimExplosao;						    // instante do final da explosão
	private long proximoTiro;
	private double raio;
	long proximoInimigo;
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
	public double getVelocidade() {
		return velocidade;
	}
	public void setVelocidade(double velocidade) {
		this.velocidade = velocidade;
	}
	public double getAngulo() {
		return angulo;
	}
	public void setAngulo(double angulo) {
		this.angulo = angulo;
	}
	public double getVelocidadeRotacao() {
		return velocidadeRotacao;
	}
	public void setVelocidadeRotacao(double velocidadeRotacao) {
		this.velocidadeRotacao = velocidadeRotacao;
	}
	public double getInicioExplosao() {
		return inicioExplosao;
	}
	public void setInicioExplosao(double inicioExplosao) {
		this.inicioExplosao = inicioExplosao;
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
	public double getRaio() {
		return raio;
	}
	public void setRaio(double raio) {
		this.raio = raio;
	}
	public long getProximoInimigo() {
		return proximoInimigo;
	}
	public void setProximoInimigo(long proximoInimigo) {
		this.proximoInimigo = proximoInimigo;
	}
	
}
