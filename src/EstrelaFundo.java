import java.util.ArrayList;


public class EstrelaFundo {
	private ArrayList<Double> x = new ArrayList<Double>();
	private ArrayList<Double> y = new ArrayList<Double>();
	private double velocidade = 0.070;
	private double contagem = 0.0;
	
	public EstrelaFundo(double velocidade, double contagem){
		this.velocidade = velocidade;
		this.contagem = contagem;
	}
	
	public double getVelocidade() {
		return velocidade;
	}
	public void setVelocidade(double velocidade) {
		this.velocidade = velocidade;
	}
	public double getContagem() {
		return contagem;
	}
	public void setContagem(double contagem) {
		this.contagem = contagem;
	}
	public ArrayList<Double> getY() {
		return y;
	}
	public void setY(ArrayList<Double> y) {
		this.y = y;
	}
	public ArrayList<Double> getX() {
		return x;
	}
	public void setX(ArrayList<Double> x) {
		this.x = x;
	}
}
