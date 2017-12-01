package source;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Main controller（もっと具体性のある説明文を考えろ）
 * @author jkkan
 */
public class MainController implements Initializable {

    private DFAData dfa;

    @FXML private TextField states;
    @FXML private TextField symbols;
    @FXML private TextField finalState;
    @FXML private Button setButton;
    @FXML private VBox transitionRulesBox;
    @FXML private TextField inputString;
    @FXML private Button convertButton;
    @FXML private Button clearButton;
    @FXML private TextField output;

    @FXML
    private void clearRuleInputs() {
        List rules = transitionRulesBox.getChildren();
        for (Object rule1 : rules) {
            HBox rule = (HBox) rule1;
            TextField field = (TextField) rule.getChildren().get(2); // pos(2) == textfield
            field.setText("");
        }
    }

    @FXML
    private void convertDFA() {
        String[] nextStates = new String[dfa.getSize()];
        int i = 0;
        for (Node rule : transitionRulesBox.getChildren()) {
            HBox hBox = (HBox) rule;
            TextField tf = (TextField) hBox.getChildren().get(2);
            nextStates[i] = dfa.getStates().get(Integer.parseInt(tf.getText()));
            i++;
        }
        dfa.setInputString(inputString.getText());
        dfa.setNextStates(nextStates);
        String finalProduct = dfa.toString();
        output.setText(finalProduct);
        output.setDisable(false);
    }

    private void checkStatesSymbolsEntered() {
        setButton.setDisable(states.getText().equals("")
                || symbols.getText().equals("") || finalState.getText().equals(""));
    }

    @FXML
    private void exit() {
        Platform.exit();
    }

    @FXML
    private void openAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Version: 1.1.2\n©Joe Kanagawa, San Jose State University");
        alert.setHeaderText("DFA To TM Input Converter");
        alert.showAndWait();
    }

    @FXML
    private void setStatesAndSymbols() {
        try {
            ArrayList<Integer> allStates = new ArrayList<>();
            for (String state : states.getText().split(","))
                allStates.add(Integer.parseInt(state));
            ArrayList<Character> allSymbols = trimDuplicates(symbols.getText().toCharArray());
            ArrayList<Integer> allFinalStates = new ArrayList<>();
            for (String finalState : finalState.getText().split(","))
                allFinalStates.add(Integer.parseInt(finalState));
            if (!isMemberOf(allFinalStates, allStates)) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Some final state is not an element of Q!");
                alert.setHeaderText("Invalid final state");
                alert.showAndWait();
                return;
            }
            dfa = new DFAData(allStates, allSymbols, allFinalStates);
            transitionRulesBox.getChildren().setAll(dfa.generateTransitionRules(convertButton, clearButton));
            transitionRulesBox.getScene().getWindow().sizeToScene();
            convertButton.setDisable(true);
            clearButton.setDisable(true);
            inputString.setDisable(false);
            output.setText("");
            output.setDisable(true);
            setButton.setText("Update");
        } catch (NumberFormatException nfe) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    nfe.getMessage() + ", invalid input.\n" +
                            "Make sure the states are all comma-separated",
                    ButtonType.OK);
            alert.setHeaderText(nfe.getClass().getName());
            alert.showAndWait();
        }
    }

    private boolean isMemberOf(ArrayList<Integer> numbers, ArrayList<Integer> intArr) {
        for (int number : numbers) {
            int checker = 0;
            for (int eachInt : intArr)
                checker |= number == eachInt ? 1 : 0;
            if (checker == 0) return false;
        }
        return true;
    }

    private ArrayList<Character> trimDuplicates(char[] arr) {
        ArrayList<Character> c = new ArrayList<>();
        for (char each : arr) {
            if (!c.contains(each))
                c.add(each);
        }
        return c;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (TextField tf : new TextField[]{states,symbols,finalState}) {
            tf.textProperty().addListener(e -> checkStatesSymbolsEntered());
        }
        inputString.textProperty().addListener(e -> {
            for (char inputSymbol : inputString.getText().toCharArray()) {
                if (!dfa.getSymbols().keySet().contains(inputSymbol)) {
                    inputString.setStyle("-fx-control-inner-background: "+ DFAData.NG);
                    convertButton.setDisable(true);
                    return;
                }
            }
            inputString.setStyle("-fx-control-inner-background: "+ DFAData.OK);
            convertButton.setDisable(false);
        });
    }
}
