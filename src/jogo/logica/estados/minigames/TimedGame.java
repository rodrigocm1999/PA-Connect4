package jogo.logica.estados.minigames;

public abstract class TimedGame {
	
	private long startTime;
	private long endTime;
	
	protected String question;
	protected String answer;
	
	public void start() {
		startTime = System.currentTimeMillis();
	}
	
	public void stop() {
		endTime = System.currentTimeMillis() - startTime;
	}
	
	public String getQuestion(){
		return question;
	}
	
	public boolean checkAnswer(String answer) {
		return this.answer.equals(answer);
	}
	
	public abstract void initialize();
	
	public abstract long availableTime();
	
	public abstract boolean isFinished();
	
}