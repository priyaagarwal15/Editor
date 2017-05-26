package editor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ScrollBar;
import javafx.geometry.Orientation;

public class Editor extends Application {
    private final Rectangle cursor;
    private Group root = new Group();
    Group textRoot = new Group();
    int widthTracker;
    int heightTracker;
    int cursorXPos;
    int cursorYPos;
    public final int margin = 5;
    int windowWidth = 500;
    int windowHeight = 500;
    int nodeCount = 0;
    int rowNumber = 0;
    int textOriginX;
    int textOriginY;
    LinkedListText<Text> multipleChar = new LinkedListText<Text>();
    LinkedListText<Text> undoRedoChar = new LinkedListText<Text>();
    int fontSize = 12;
    int cursorNode = 0;
    int indexToStart;
    double usableScreenWidth;
    ScrollBar scrollBar = new ScrollBar();

    // ArrayList that keeps track of the widths of each row.
    ArrayList<Integer> rowTracker = new ArrayList<Integer>();
    // ArrayList that keeps track of the first node in every line.
    ArrayList<Text> firstNodeTracker = new ArrayList<Text>();
    // ArrayList that keeps track of the count of nodes in every line.
    ArrayList<Integer> nodeTracker = new ArrayList<Integer>();
    // ArrayList that keeps track of the index number of the first node in every line.
    ArrayList<Integer> indexTracker = new ArrayList<Integer>();



    public Editor() {
        // Create a cursor that gets displayed upon opening of the screen. Initializes
        // with the height of the font and width of 1 pixel.
        cursor = new Rectangle(margin, 0, 1, 12);
        widthTracker = margin;
        cursorXPos = widthTracker;
        cursorYPos = heightTracker;
    }

	/** An EventHandler to handle keys that get pressed. */
    private class KeyEventHandler implements EventHandler<KeyEvent> {

        /** The Text to display on the screen. */
        public Text displayText = new Text(250, 250, "");

        private String fontName = "Verdana";

        public KeyEventHandler(final Group textRoot, int windowWidth, int windowHeight) {
            textOriginX = margin;
            textOriginY = 0;

            // Initialize some empty text and add it to root so that it will be displayed.
            displayText = new Text(textOriginX, textOriginY, "");
            // Always set the text origin to be VPos.TOP! Setting the origin to be VPos.TOP means
            // that when the text is assigned a y-position, that position corresponds to the
            // highest position across all letters (for example, the top of a letter like "I", as
            // opposed to the top of a letter like "e"), which makes calculating positions much
            // simpler!
            displayText.setTextOrigin(VPos.TOP);
            displayText.setFont(Font.font (fontName, fontSize));

            // All new Nodes need to be added to the root in order to be displayed.
            textRoot.getChildren().add(displayText);
        }

        @Override
        public void handle(KeyEvent keyEvent) {

            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED && !keyEvent.isShortcutDown()) {
                // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                // the KEY_TYPED event, javafx handles the "Shift" key and associated
                // capitalization.
                String characterTyped = keyEvent.getCharacter();
                Text characterTypedText = new Text(cursorXPos, cursorYPos, characterTyped);
                characterTypedText.setTextOrigin(VPos.TOP);
                characterTypedText.setFont(Font.font (fontName, fontSize));
                double textWidth = characterTypedText.getLayoutBounds().getWidth();
                double textHeight = characterTypedText.getLayoutBounds().getHeight();
                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                    if (widthTracker == textOriginX && heightTracker == textOriginY) {
                        rowTracker.add(widthTracker);
                        nodeTracker.add(nodeCount);
                        // widthTracker += (int) Math.round(textWidth);
                        multipleChar.addLast(characterTypedText);
                        textRoot.getChildren().add(characterTypedText);
                        render();
                        cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX())) + (int) Math.round(multipleChar.getItemPrevious().getLayoutBounds().getWidth());
                        cursorNode += 1;
                        cursorYPos = heightTracker;
                        rowTracker.set(rowNumber, widthTracker);
                        nodeTracker.set(rowNumber, nodeCount);
                        keyEvent.consume();
                        centerTextAndUpdateCursor();
                    }
                    else {
                        multipleChar.addLast(characterTypedText);
                        textRoot.getChildren().add(characterTypedText);
                        int tempy = (int) Math.round(multipleChar.getItemPrevious().getY());
                        render();
                        // widthTracker += (int) Math.round(textWidth);
                        cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX())) + (int) Math.round(multipleChar.getItemPrevious().getLayoutBounds().getWidth());
                        cursorYPos = (int) Math.round(multipleChar.getItemPrevious().getY());
                        if (cursorYPos != tempy) {
                            cursorNode = 1;
                        }
                        else {
                            cursorNode += 1;
                        }
                        rowTracker.set(rowNumber, widthTracker);
                        nodeTracker.set(rowNumber, nodeCount);
                        keyEvent.consume();
                        centerTextAndUpdateCursor();
                    }        
                }
                centerTextAndUpdateCursor();

            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                // events have a code that we can check (KEY_TYPED events don't have an associated
                // KeyCode).
                KeyCode code = keyEvent.getCode();
                if (keyEvent.isShortcutDown()) {
                    if ((code == KeyCode.PLUS) || (code == KeyCode.EQUALS)) {
                        fontSize += 4;
                        fontSizeRender();
                        centerTextAndUpdateCursor();
                    }
                    if (code == KeyCode.MINUS) {
                        if (fontSize >= 4) {
                            fontSize -= 4;
                            widthTracker = margin;
                            fontSizeRender();
                            centerTextAndUpdateCursor();
                        }
                    }
                    if (code == KeyCode.S) {
                        OpenSaveFile newFile = new OpenSaveFile("hello.txt", multipleChar);
                        newFile.save();
                    }
                    if (code == KeyCode.Z) {
                        try {
                            if (undoRedoChar.size()<=100 && multipleChar.size()>0) {
                                undoRedoChar.addLast(multipleChar.get(multipleChar.size()-1));
                                cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX()));
                                cursorYPos = (int) Math.round(multipleChar.getItemPrevious().getY());
                                Text lastRemoved = multipleChar.removeLast();
                                
                                textRoot.getChildren().remove(lastRemoved);
                                double textWidth = lastRemoved.getLayoutBounds().getWidth();
                                widthTracker -= (int) Math.round(textWidth);
                                nodeCount -= 1;
                                render();
                                rowTracker.set(rowNumber, widthTracker);
                                centerTextAndUpdateCursor();
                                keyEvent.consume(); 
                            }
                        }
                        catch (Exception e) {
                            return;
                        }
                    }
                    if (code == KeyCode.R) {
                        try {
                            if (undoRedoChar.size()>0) {
                                multipleChar.addLast(undoRedoChar.get(undoRedoChar.size()-1));
                                Text characterTypedText = undoRedoChar.removeLast();
                                textRoot.getChildren().add(characterTypedText);
                                render();
                                cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX())) + (int) Math.round(multipleChar.getItemPrevious().getLayoutBounds().getWidth());
                                cursorYPos = (int) Math.round(multipleChar.getItemPrevious().getY());
                                nodeCount += 1;
                                rowTracker.set(rowNumber, widthTracker);
                                keyEvent.consume();
                                centerTextAndUpdateCursor();
                            }
                        }
                        catch (Exception e) {
                            return;
                        }
                    }
                }
                else if (code == KeyCode.ENTER) {
                    rowTracker.add(widthTracker);
                    rowNumber += 1;
                    widthTracker = textOriginX;
                    cursorXPos = textOriginX;
                    heightTracker += fontSize;
                    cursorYPos += fontSize;
                }
                else if (code == KeyCode.UP) {
                    if (rowNumber == 0) {
                    } 
                    else {
                        render();
                        indexToStart = indexTracker.get(rowNumber - 1);
                        LinkedListText.Node nodeToStart = multipleChar.getNode(indexToStart);
                        int temp;
                        LinkedListText.Node nodeToPlace = null;
                        for (int i = 0; i < cursorNode; i++) {
                            temp = indexToStart + i;
                            nodeToPlace = multipleChar.getNode(temp);
                        }
                        multipleChar.detachAndAttach(nodeToPlace);
                        cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX()));
                        cursorYPos = (int) Math.round(multipleChar.getItemPrevious().getY());
                        render();
                        rowNumber -= 1;
                        centerTextAndUpdateCursor();
                    }
                }
                else if (code == KeyCode.DOWN) {
                    if (rowNumber == ((heightTracker / fontSize) - 1)) {
                    }
                    else {
                        render();
                        indexToStart = indexTracker.get(rowNumber + 1);
                        LinkedListText.Node nodeToStart2 = multipleChar.getNode(indexToStart);
                        int temp;
                        LinkedListText.Node nodeToPlace2 = null;
                        for (int i = 0; i <= cursorNode; i++) {
                            temp = indexToStart + i;
                            nodeToPlace2 = multipleChar.getNode(temp);
                        }
                        multipleChar.detachAndAttach(nodeToPlace2);
                        cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX()));
                        cursorYPos = (int) Math.round(multipleChar.getItemPrevious().getY());
                        render();
                        rowNumber += 1;
                        centerTextAndUpdateCursor();
                    }
                }
                else if (code == KeyCode.RIGHT) {
                    if (cursorXPos < widthTracker) {
                        cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX())) + (int) Math.round(multipleChar.getItemPrevious().getLayoutBounds().getWidth());
                        cursorYPos = (int) Math.round(multipleChar.getItemPrevious().getY());
                        cursorNode += 1;
                        centerTextAndUpdateCursor();
                        multipleChar.moveCursorRight();
                    }
                    else if (cursorXPos == rowTracker.get(rowNumber)) {
                        if (rowNumber == (rowTracker.size() - 1)) {
                        }
                        else {
                            rowNumber += 1;
                            cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX()));
                            cursorYPos = (int) Math.round(multipleChar.getItemPrevious().getY()) + fontSize;
                            cursorNode = 0;
                            centerTextAndUpdateCursor();
                            widthTracker = rowTracker.get(rowNumber);
                            nodeCount = nodeTracker.get(rowNumber);
                        }
                    }
                }
                else if (code == KeyCode.LEFT) {
                    if (cursorXPos > textOriginX) {
                        Text itemPrevious = multipleChar.getItemPrevious();
                        double previousWidth = itemPrevious.getLayoutBounds().getWidth();
                        cursorXPos = (int) Math.round(itemPrevious.getX());
                        cursorYPos = (int) Math.round(multipleChar.getItemPrevious().getY());
                        cursorNode -= 1;
                        centerTextAndUpdateCursor();
                        multipleChar.moveCursorLeft();
                    }
                    else if (cursorXPos == textOriginX && cursorYPos == textOriginY) {
                    }
                    else if (cursorXPos == textOriginX) {
                        rowNumber -= 1;
                        cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX())) + (int) Math.round(multipleChar.getItemPrevious().getLayoutBounds().getWidth());
                        cursorYPos = (int) Math.round(multipleChar.getItemPrevious().getY());
                        widthTracker = rowTracker.get(rowNumber);
                        nodeCount = nodeTracker.get(rowNumber);
                        cursorNode = nodeCount;
                        centerTextAndUpdateCursor();
                    }
                }
                else if (code == KeyCode.BACK_SPACE) {
                    if ((multipleChar.size() > 0) || (cursorXPos > textOriginX)) {
                        cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX()));
                        cursorYPos = (int) Math.round(multipleChar.getItemPrevious().getY());
                	    Text lastRemoved = multipleChar.removeLast();
                        textRoot.getChildren().remove(lastRemoved);
                        render();
                        int tempy = (int) Math.round(multipleChar.getItemPrevious().getY());
                        double textWidth = lastRemoved.getLayoutBounds().getWidth();
                        widthTracker -= (int) Math.round(textWidth);
                        if (tempy != cursorYPos) {
                            rowNumber -= 1;
                            cursorNode = nodeTracker.get(rowNumber);
                        }
                        else {
                            cursorNode -= 1;
                        }
                        render();
                        rowTracker.set(rowNumber, widthTracker);
                        nodeTracker.set(rowNumber, nodeCount);
                        centerTextAndUpdateCursor();
                        keyEvent.consume();
                    }
                    else if (cursorXPos == textOriginX && cursorYPos == textOriginY) {
                    }
                    else {
                        rowTracker.remove(rowNumber);
                        nodeTracker.remove(rowNumber);
                        render();
                        rowNumber -= 1;
                        widthTracker = rowTracker.get(rowNumber);
                        heightTracker -= fontSize;
                        cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX()));
                        cursorYPos = ((int) Math.round(multipleChar.getItemPrevious().getY()));
                        cursorNode = nodeTracker.get(rowNumber);
                        render();
                        centerTextAndUpdateCursor();
                    }
                }
            }
        }

        public void fontSizeRender() {
            for (int i = 0; i < multipleChar.size(); i++) {
                Text temptext = multipleChar.get(i);
                temptext.setTextOrigin(VPos.TOP);                        
                temptext.setFont(Font.font(fontName, fontSize));
            }
            render();
            cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX())) + (int) Math.round(multipleChar.getItemPrevious().getLayoutBounds().getWidth());
            cursorYPos = (int) Math.round(multipleChar.getItemPrevious().getY());
        }

        private void centerTextAndUpdateCursor() {
            // Figure out the size of the current text.
            double textHeight = displayText.getLayoutBounds().getHeight();
            double textWidth = displayText.getLayoutBounds().getWidth();

            // Calculate the position so that the text will be centered on the screen.
            double textTop = textOriginY - textHeight / 2;
            double textLeft = textOriginX - textWidth / 2;

            // Re-position the text.
            displayText.setX(textOriginX);
            displayText.setY(textOriginY);

            //Re-size the cursor.
            cursor.setWidth(1);
            cursor.setHeight(fontSize);

            cursor.setX(cursorXPos);
            cursor.setY(cursorYPos);
            rowNumber = cursorYPos / fontSize;

            // Make sure the text appears in front of any other objects you might add.
            displayText.toFront();
        }
    }

    /** An EventHandler to handle changing the color of the rectangle. */
    private class CursorBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors =
                {Color.BLACK, Color.WHITE};

        CursorBlinkEventHandler() {
            // Set the color to be the first color in the list.
            changeColor();
        }

        private void changeColor() {
            cursor.setFill(boxColors[currentColorIndex]);
            currentColorIndex = 1 - currentColorIndex;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    /** Makes the cursor blink periodically. */
    public void makeCursorColorChange() {
        // Create a Timeline that will call the "handle" function of CursorBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        CursorBlinkEventHandler cursorChange = new CursorBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public void render() {
            int tempWidthTracker = textOriginX;
            int tempHeightTracker = textOriginY;
            int tempNodeCount = 0;
            int tempRowNumber = 0;
            while (tempNodeCount <= multipleChar.size()) {
                Text temptext = multipleChar.get(tempNodeCount);
                if (temptext == null) {
                    tempNodeCount++;
                }
                else {
                    double tempTextWidth = temptext.getLayoutBounds().getWidth();
                    temptext.setX(tempWidthTracker);
                    temptext.setY(tempHeightTracker);
                    if (tempWidthTracker == textOriginX) {
                        if (tempRowNumber == (firstNodeTracker.size())) {
                            firstNodeTracker.add(temptext);
                            indexTracker.add(tempNodeCount);
                        }
                        else {
                            firstNodeTracker.set(tempRowNumber, temptext);
                            indexTracker.set(tempRowNumber, tempNodeCount);
                        }
                    }
                    tempWidthTracker += (double) Math.round(tempTextWidth);
                    if (tempWidthTracker >= ((windowWidth) - margin)) {
                        while (!(multipleChar.get(tempNodeCount).getText().equals(" ")) && (multipleChar.get(tempNodeCount - 1) != null)) {
                            double tempWidth = multipleChar.get(tempNodeCount).getLayoutBounds().getWidth();
                            tempWidthTracker -= (int) Math.round(tempWidth);
                            tempNodeCount--;
                        }
                        if (tempRowNumber == (rowTracker.size() - 1)) {
                            rowTracker.set(tempRowNumber, tempNodeCount);
                            tempWidthTracker = textOriginX;
                            tempHeightTracker += fontSize;
                            rowTracker.add(tempWidthTracker);
                            nodeTracker.add(tempNodeCount);
                            tempRowNumber += 1;
                        }
                        else {
                            rowTracker.set(tempRowNumber, tempNodeCount);
                            tempWidthTracker = textOriginX;
                            tempHeightTracker += fontSize;
                            tempRowNumber += 1;
                        }    
                    }
                    if (tempHeightTracker > (windowHeight - margin)) {
                        scrollBar.setMax(tempHeightTracker);
                    }
                    tempNodeCount++;
                }
            }
            widthTracker = tempWidthTracker;
            heightTracker = tempHeightTracker;
            nodeCount = tempNodeCount-1;
            scrollBar.setLayoutX(windowWidth - scrollBar.getLayoutBounds().getWidth());
            scrollBar.setPrefHeight(windowHeight);
        }

    private int getDimensionInsideMargin(int outsideDimension) {
        return outsideDimension - 2 * margin;
    }

    private class MouseClickEventHandler implements EventHandler<MouseEvent> {
        MouseClickEventHandler(Group root) {
            
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            // Because we registered this EventHandler using setOnMouseClicked, it will only called
            // with mouse events of type MouseEvent.MOUSE_CLICKED.  A mouse clicked event is
            // generated anytime the mouse is pressed and released on the same JavaFX node.
            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();
            int roundedmousePressedX = (int) Math.round(mousePressedX);
            int clickRowNumber = (((int) Math.round(mousePressedY) / fontSize));
            if (clickRowNumber >= (firstNodeTracker.size()+1)) {
            }
            else {
                render();
                indexToStart = indexTracker.get(clickRowNumber);
                LinkedListText.Node nodeToStart3 = multipleChar.getNode(indexToStart);
                int temp;
                LinkedListText.Node nodeToPlace3 = null;
                for (int i = 0; i < (nodeTracker.get(clickRowNumber)); i++) {
                    temp = indexToStart + i;
                    Text temp2 = multipleChar.getNode(temp - 1).item;
                    Text temp3 = multipleChar.getNode(temp).item;
                    if ((temp2.getX() <= mousePressedX) && (temp3.getX() > mousePressedX)) {
                        nodeToPlace3 = multipleChar.getNode(temp);
                    }
                }

                multipleChar.detachAndAttach(nodeToPlace3);
                cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX()));
                cursorYPos = (int) Math.round(multipleChar.getItemPrevious().getY());
                render();
                cursor.setX(cursorXPos);
                cursor.setY(cursorYPos);
            }

        }
    }
    // public void refresh(){
    //     scrollBar.setMax(windowHeight)

    @Override
    public void start(Stage primaryStage) {
    	// Create a Node that will be the parent of all things displayed on the screen.
        // The Scene represents the window: its height and width will be the height and width
        // of the window displayed.
        Scene scene = new Scene(root, windowWidth, windowHeight, Color.WHITE);
        OpenSaveFile newFile = new OpenSaveFile("hello.txt", multipleChar);
        newFile.open();

        root.getChildren().add(textRoot);
        

        // To get information about what keys the user is pressing, create an EventHandler.
        // EventHandler subclasses must override the "handle" function, which will be called
        // by javafx.
        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(textRoot, windowWidth, windowHeight);
        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);

        scene.setOnMouseClicked(new MouseClickEventHandler(textRoot));

        // All new Nodes need to be added to the root in order to be displayed.
        textRoot.getChildren().add(cursor);
        makeCursorColorChange();

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                windowWidth = getDimensionInsideMargin(newScreenWidth.intValue());
                render();
                cursorXPos = ((int) Math.round(multipleChar.getItemPrevious().getX()));
                cursor.setX(cursorXPos);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                windowHeight = getDimensionInsideMargin(newScreenHeight.intValue());
                render();
                cursorYPos = ((int) Math.round(multipleChar.getItemPrevious().getY()));
                cursor.setY(cursorYPos);
            }
        });

        scrollBar.setOrientation(Orientation.VERTICAL);
        // Set the height of the scroll bar so that it fills the whole window.
        scrollBar.setPrefHeight(windowHeight);
        scrollBar.setMin(0);
        scrollBar.setMax(0);
        root.getChildren().add(scrollBar);
        usableScreenWidth = windowWidth - scrollBar.getLayoutBounds().getWidth();
        scrollBar.setLayoutX(usableScreenWidth);

        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                textRoot.setLayoutY(-newValue.doubleValue());
            }
        });

        primaryStage.setTitle("New Editor");

        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}