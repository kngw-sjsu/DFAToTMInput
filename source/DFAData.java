package source;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * DFAData - Simple DFA representation class
 * @author jkkan
 */
class DFAData {

    static final String OK = "#ABEBC6";
    static final String NG = "#F5B7B1";

    private HashMap<Integer, String> states;
    private HashMap<Character, String> symbols;
    private String[] finalStates;
    private HBox[] transitionRules;
    private String inputString;
    private String[] nextStates;
    private int size;

    {
        states = new HashMap<>();
        symbols = new HashMap<>();
        inputString = "";
        nextStates = null;
    }

    DFAData(ArrayList<Integer> states, ArrayList<Character> symbols, ArrayList<Integer> finalStates) {
        String s = "";
        for (Integer state : states) {
            s += "1";
            this.states.put(state, s);
        }
        s = "";
        for (Character symbol : symbols) {
            s += "1";
            this.symbols.put(symbol, s);
        }

        this.finalStates = new String[finalStates.size()];
        int i = 0;
        for (int finalState : finalStates) {
            this.finalStates[i] = this.states.get(finalState);
            i++;
        }
        size = states.size() * symbols.size();
    }

    HBox[] generateTransitionRules(final Button convertButton, final Button clearButton) {
        transitionRules = new HBox[size];
        int i = 0;
        for (int state : states.keySet()) {
            for (char symbol : symbols.keySet()) {
                HBox transitionRule = new HBox(8);
                transitionRule.getStyleClass().add("hbox");
                Label symbolLabel = new Label("δ");
                symbolLabel.getStyleClass().add("symbol");
                Label functionLabel = new Label("(q" + state + ", " + symbol + ") = ");
                functionLabel.getStyleClass().add("transition-rule");
                TextField newStateField = new TextField();
                newStateField.setPrefWidth(40.0);
                newStateField.textProperty().addListener(e -> {
                    String input = newStateField.getText();
                    newStateField.setStyle("-fx-control-inner-background: "
                            + (input.matches("^(?:0(?!\\w)|[1-9])[0-9]*$")
                            && isState(Integer.parseInt(input)) ? OK : NG));
                    for (HBox eachRule : transitionRules) /* ここもっとマシなコードにならんの？ */ {
                        TextField field = (TextField) eachRule.getChildren().get(2); // 3番目の要素がTextField
                        if (!field.getStyle().contains(OK)) {
                            convertButton.setDisable(true);
                            break;
                        }
                        convertButton.setDisable(false);
                    }
                    for (HBox eachRule : transitionRules) /* うーん.. ここももうちょっと縮められそうなんだよなぁ.. */ {
                        TextField field = (TextField) eachRule.getChildren().get(2);
                        if (field.getText().length() > 0) {
                            clearButton.setDisable(false);
                            break;
                        }
                        clearButton.setDisable(true);
                    }
                });
                transitionRule.getChildren().addAll(symbolLabel, functionLabel, newStateField);
                transitionRules[i] = transitionRule;
                i++;
            }
        }
        return transitionRules;
    }

    int getSize() {
        return size;
    }

    HashMap<Integer, String> getStates() {
        return states;
    }

    HashMap<Character, String> getSymbols() { return symbols; }

    private boolean isState(int inputState) {
        for (int eachState : states.keySet()) {
            if (inputState == eachState)
                return true;
        }
        return false;
    }

    void setInputString(String s) {
        inputString = s;
    }

    void setNextStates(String[] nextStates) {
        this.nextStates = nextStates;
    }

    @Override
    public String toString() {
        if (nextStates == null) return "";
        String finalProduct = "";
        for (int i = 0; i < inputString.length(); i++)
            finalProduct += (symbols.get(inputString.charAt(i))
                    + (i == inputString.length() - 1 ? "" : "0"));
        int count = 0;
        for (HashMap.Entry<Integer, String> state : states.entrySet()) {
            for (HashMap.Entry<Character, String> symbol : symbols.entrySet()) {
                finalProduct += "D" + state.getValue() + "0" + symbol.getValue() + "0" + nextStates[count];
                count++;
            }
        }
        finalProduct += "F";
        for (String finalState : finalStates)
            finalProduct += finalState + "0";
        return finalProduct.substring(0,finalProduct.length() - 1);
    }
}
