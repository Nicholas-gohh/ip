package alice.ui;

import alice.Alice;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Connects the main chat-window layout to Alice's command processing.
 */
public class MainWindow {
    private Alice alice;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    /**
     * Adds Alice's greeting after the FXML controls have been created.
     */
    @FXML
    public void initialize() {
        DialogBox welcomeDialog = DialogBox.getBotDialog(Ui.getWelcomeMessage());
        welcomeDialog.getStyleClass().add("welcome-dialog");
        dialogContainer.getChildren().add(welcomeDialog);
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the shared Alice instance used to process chat commands.
     *
     * @param alice The bot instance for this application session.
     */
    public void setAlice(Alice alice) {

        this.alice = alice;
    }

    /**
     * Sends the text-field command to Alice and displays both messages.
     */
    @FXML
    private void handleUserInput() {
        String command = userInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        dialogContainer.getChildren().add(DialogBox.getUserDialog(command));
        dialogContainer.getChildren().add(DialogBox.getBotDialog(alice.getResponse(command)));
        userInput.clear();
        if (command.equals("bye")) {
            Platform.exit();
        }
    }
}
