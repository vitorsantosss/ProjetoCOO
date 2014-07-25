
public class Projetil {
	private Estado estado = new EstadoInativo();
	private double x;									// coordenada x
	private double y;									// coordenada y
	private double velocidadeX;							// velocidade no eixo x
	private double velocidadeY;							// velocidade no eixo y
	private double raio;
	
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
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public double getRaio() {
		return raio;
	}
	public void setRaio(double raio) {
		this.raio = raio;
	}
}
