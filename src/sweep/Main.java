package sweep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		
		try {
			
			File file = new File("src/sweep/words");

			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append(" ");
			}
			String[] splitted = stringBuffer.toString().split(" ");
			Sweeper.sweep(splitted);
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
