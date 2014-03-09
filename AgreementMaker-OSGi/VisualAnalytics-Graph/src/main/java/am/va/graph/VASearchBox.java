package am.va.graph;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class VASearchBox extends Region {
	private TextField textBox;
    private Button clearButton;
    private static VASearcher searcher = new VASearcher();
    
    private VAPanel vap;

    public VASearchBox(VAPanel v) {
    	this.vap = v;
        setId("SearchBox");
        setMinHeight(24);
        setPrefSize(200, 24);
        setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        textBox = new TextField();
        textBox.setPromptText("Search ontology");
        clearButton = new Button();
        clearButton.setVisible(false);
        getChildren().addAll(textBox, clearButton);
        clearButton.setOnAction(new EventHandler<ActionEvent>() {                
            @Override public void handle(ActionEvent actionEvent) {
                textBox.setText("");
                textBox.requestFocus();
            }
        });
        
        textBox.textProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	clearButton.setVisible(textBox.getText().length()!=0); 
            }
        });
        
        textBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
				System.out.println("Key Pressed: " + ke.getText());
				if (ke.getCode().equals(KeyCode.ENTER)) {
					String inputString = textBox.getText();
					if (inputString.length() != 0) {
						VAData result = searcher.search(inputString);
						if (result != null){
							//set 0 here
							vap.setUpButton(vap.getVal().generateNewGroup(VAVariables.ontologyType.Source, result, 0));
							vap.getVal().generateNewGroup(VAVariables.ontologyType.Source, result, 1);
							vap.updateLeftChart();
							vap.generateNewTree();
							System.out.println("result is "
									+ result.getNodeName());
							
						}
						else {
							System.out.println("No result");
						}
					}
				}
			}
        });
    }

    @Override
    protected void layoutChildren() {
        textBox.resize(getWidth(), getHeight());
        clearButton.resizeRelocate(getWidth() - 18, 6, 12, 13);
    }
}
