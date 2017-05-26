package editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.text.Text;
import javafx.geometry.VPos;

public class OpenSaveFile {
	String filename;
	LinkedListText<Text> lst = new LinkedListText<Text>();
    Editor blank = new Editor();

	public OpenSaveFile(String fileName, LinkedListText newlst) {
		filename = fileName;
		lst = newlst;
	}

	public void open() {
        try {
            File inputFile = new File(filename);
            // Check to make sure that the input file exists!
            if (!inputFile.exists()) {
            	inputFile.createNewFile();
            } 
            FileReader reader = new FileReader(inputFile);
	        BufferedReader bufferedReader = new BufferedReader(reader);
	        int intRead = -1;
	        // Keep reading from the file input read() returns -1, which means the end of the file
	        // was reached.
	        while ((intRead = bufferedReader.read()) != -1) {
	            // The integer read can be cast to a char, because we're assuming ASCII.
	            char charRead = (char) intRead;
	            String stringRead = Character.toString(charRead);
	            Text textRead = new Text(stringRead);
                if (textRead == null) {
                }
                else {
	               textRead.setTextOrigin(VPos.TOP);
	               lst.addLast(textRead);
                }
	        }
	        System.out.println("Successfully saved file " + filename);
	        bufferedReader.close();
    	} 
    	catch (FileNotFoundException fileNotFoundException) {
        	System.out.println("File not found! Exception was: " + fileNotFoundException);
    	} 
    	catch (IOException ioException) {
            System.out.println("Error when opening; exception was: " + ioException);
        }
  	}

    public void save() {
    	try {
    		FileWriter writer = new FileWriter(filename);
    		for (int i = 0; i < lst.size(); i++) {
    			if (lst.get(i) == null) {
    				i++;
    			}
    			char charRead = lst.get(i).getText().charAt(0); 
    			writer.write(charRead);
    		}
    		writer.close();
        }
        catch (FileNotFoundException fileNotFoundException) {
        	System.out.println("File not found! Exception was: " + fileNotFoundException);
    	} 
    	catch (IOException ioException) {
            System.out.println("Error when saving; exception was: " + ioException);
        }

    }

}