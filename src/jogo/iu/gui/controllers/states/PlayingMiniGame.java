package jogo.iu.gui.controllers.states;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import jogo.iu.gui.Connect4UI;
import jogo.iu.gui.GameWindowStateManager;
import jogo.iu.gui.ResourceLoader;

public class PlayingMiniGame extends AbstractWindowState implements Initializable {
	
	public Label feedBackLabel;
	public Label gameObjectiveLabel;
	public Label questionLabel;
	public TextField answerTextField;
	public Button startButton;
	public Label timerLabel;
	
	private boolean isFinished;
	private boolean hasStarted;
	private Thread timerThread;
	
	public PlayingMiniGame(GameWindowStateManager windowStateManager) {
		super(windowStateManager, ResourceLoader.FXML_PLAYING_MINIGAME);
	}
	
	@Override
	public void show() {
		System.out.println("PlayingMiniGame");
		super.show();
		
		isFinished = false;
		hasStarted = false;
		
		var stateMachine = getWindowStateManager().getStateMachine();
		gameObjectiveLabel.setText(stateMachine.getMiniGameObjective());
		timerLabel.setText(Integer.toString(stateMachine.getMiniGameAvailableTime()));
		
		questionLabel.setText("");
		answerTextField.clear();
		feedBackLabel.setText("");
		startButton.setDisable(false);
	}
	
	@FXML
	public void triedToAnswer(ActionEvent actionEvent) {
		if (!hasStarted)
			return;
		var stateMachine = getWindowStateManager().getStateMachine();
		
		String answer = answerTextField.getText();
		stateMachine.answerMiniGame(answer);
		
		if (stateMachine.isMiniGameFinished()) {
			System.out.println("Minigame Finished");
			finishedMiniGame();
		} else {
			questionLabel.setText(stateMachine.getMiniGameQuestion());
			answerTextField.clear();
		}
		
		if (stateMachine.playerGotMiniGameQuestionAnswerRight())
			feedBackLabel.setText("That is the right answer");
		else
			feedBackLabel.setText("You got it wrong");
	}
	
	
	private void finishedMiniGame() {
		System.out.println("Finished minigame");
		isFinished = true;
		timerThread.interrupt();
		timerThread = null;
		
		var stateMachine = getWindowStateManager().getStateMachine();
		stateMachine.isMiniGameFinished();
		
		var messageStart = "It appears that player '";
		String message;
		if (stateMachine.didPlayerWinMiniGame()) {
			var playerName = stateMachine.getCurrentPlayerObj().getName();
			message = messageStart + playerName + "' as won the minigame";
		} else {
			var playerName = stateMachine.getPlayer(stateMachine.getCurrentPlayer().getOther()).getName();
			message = messageStart + playerName + "' as lost the minigame and the turn";
		}
		Connect4UI.getInstance().openMessageDialog(Alert.AlertType.INFORMATION, "Minigame Result", message);
	}
	
	@FXML
	public void startMiniGame(ActionEvent actionEvent) {
		startButton.setDisable(true);
		
		var stateMachine = getWindowStateManager().getStateMachine();
		
		stateMachine.startMiniGameTimer();
		hasStarted = true;
		
		questionLabel.setText(stateMachine.getMiniGameQuestion());
		
		timerThread = new Thread(() -> {
			try {
				int remainingTime = stateMachine.getMiniGameAvailableTime() + 1; // +1 To fix a bug
				while (remainingTime > 0) {
					Thread.sleep(1000);
					int finalRemainingTime = --remainingTime;
					Platform.runLater(() ->
							timerLabel.setText(Integer.toString(finalRemainingTime)));
				}
				if (!isFinished) {
					stateMachine.answerMiniGame("");
					Platform.runLater(this::finishedMiniGame);
				}
			} catch (InterruptedException ignored) {
			}
		}, "MiniGame Timer Thread");
		timerThread.setDaemon(true);
		timerThread.start();
	}
}
