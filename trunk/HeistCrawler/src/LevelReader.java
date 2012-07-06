import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class LevelReader {
	String path;

	public LevelReader(String dir) {
		this.path = dir;
	}

	public Level readLevelFile(int levelnum) {
		Level level = new Level("Level_" + levelnum);
		try {
			// Open the file that is the first
			// command line parameter
			File file = new File(path + "/Layout" + levelnum + ".txt");
			// FileInputStream fstream = new FileInputStream("Layout.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), Charset.forName("UTF-8")));

			int c;
			String strLine;
			strLine = reader.readLine();
			int xx = 10;
			int yy = 10;
			int ln = 0;
			int lnlng = strLine.length();
			int size = 5;

			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), Charset.forName("UTF-8")));
			while ((c = reader.read()) != -1) {
				ln++;
				char character = (char) c;

				if (character == 'X') {
					level.walls.add(new Wall(xx, yy, size, size));
				}
				
				if (ln == lnlng + 2) {
					xx = 10;
					yy = yy + size;
					ln = 0;
				}
				if (ln == 0) {
				} else {
					xx = xx + size;
				}
			}

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return level;
	}
}
