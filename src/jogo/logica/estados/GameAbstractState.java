package jogo.logica.estados;

import jogo.logica.dados.dataViewers.GameDataViewer;
import jogo.logica.dados.dataViewers.PlayerViewer;
import jogo.logica.dados.observables.GameDataObservable;
import jogo.logica.dados.*;

public abstract class GameAbstractState implements GameState {
	
	protected GameDataObservable game;
	
	public GameAbstractState(GameDataObservable game) {
		this.game = game;
	}
	
	@Override
	public GameState playAt(int column) {
		return this;
	}
	
	@Override
	public GameState clearColumn(int column) {
		return this;
	}
	
	@Override
	public GameState acceptMiniGame() {
		return this;
	}
	
	@Override
	public GameState ignoreMiniGame() {
		return this;
	}
	
	@Override
	public GameState executePlay() {
		return this;
	}
	
	@Override
	public GameState rollback(int amount) {
		return this;
	}
	
	@Override
	public GameState startGameWithPlayers(Player player1, Player player2) {
		return this;
	}
	
	// MiniGame Related --------------------
	@Override
	public GameState answerMiniGame(String answer) {
		return this;
	}
	
	@Override
	public void startMiniGameTimer() {
	}
	
	@Override
	public String getMiniGameQuestion() {
		return null;
	}
	
	@Override
	public String getMiniGameObjective() {
		return null;
	}
	
	@Override
	public int getMiniGameAvailableTime() {
		return -1;
	}
	
	@Override
	public boolean didPlayerWinMiniGame() {
		return game.didPlayerWinMiniGame();
	}
	
	@Override
	public boolean isMiniGameFinished() {
		return game.isMiniGameFinished();
	}
	
	@Override
	public boolean hasMiniGameStarted() {
		return game.hasMiniGameStarted();
	}
	
	@Override
	public boolean playerGotMiniGameQuestionAnswerRight() {
		return game.playerGotMiniGameQuestionAnswerRight();
	}
	// -------------------------------------
	
	@Override
	public GameDataViewer getGameViewer() {
		return new GameDataViewer(game);
	}
	
	@Override
	public Piece[][] getGameArea() {
		return game.getGameArea();
	}
	
	@Override
	public Piece getWinner() {
		return null;
	}
	
	@Override
	public PlayerViewer getPlayer(Piece playerPiece) {
		return new PlayerViewer(game.getPlayerFromEnum(playerPiece));
	}
	
	@Override
	public PlayerViewer getCurrentPlayer() {
		return new PlayerViewer(game.getPlayerFromEnum(getCurrentPlayerPiece()));
	}
	
	@Override
	public Piece getCurrentPlayerPiece() {
		return game.getCurrentPlayerPiece();
	}
	
	protected GameFinished checkFinishedState() {
		Piece winner = game.checkWinner();
		if (winner != null)
			return new GameFinished(game, winner);
		if (game.isFull())
			return new GameFinished(game, null);
		return null;
	}
	
	protected GameState stateAfterPlay() {
		GameFinished finishedState = checkFinishedState();
		if (finishedState != null) return finishedState;
		
		if (game.isCurrentPlayerBot())
			return new ComputerPlays(game);
		
		if (game.isMiniGameAvailable()) {
			return new CheckPlayerWantsMiniGame(game);
		}
		return new WaitingPlayerMove(game);
	}
}
