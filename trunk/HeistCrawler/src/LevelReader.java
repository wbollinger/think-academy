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

	public Level readLevel(int levelnum) {
		Level level = new Level("Level_" + levelnum);
		try {
			// Open the file that is the first
			// command line parameter
			File file = new File(path + "/Layout" + levelnum + ".txt");
			// FileInputStream fstream = new FileInputStream("Layout.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), Charset.forName("UTF-8")));

			int lvlWidth;
			int lvlHeight;
			String strLine = reader.readLine();
			lvlWidth = Integer.parseInt(strLine.substring(strLine.indexOf('<')+1,
					strLine.indexOf(',')));
			lvlHeight = Integer.parseInt(strLine.substring(
					strLine.indexOf(',')+1, strLine.indexOf('>')));
			
			int xx = 10;
			int yy = 10;
			int size = 5;
			
			System.out.println("before Map Creation");
			char[][] map = new char[lvlWidth][lvlHeight];
			System.out.println("After Map Creation");
			for (int y = 0; y < lvlHeight; y++) {
				for (int x = 0; x < lvlWidth; x++) {
					char c = (char) reader.read();
					while(c == '\n') {
						c = (char) reader.read();
					}
						map[x][y] = c;
				}
			}
			System.out.println("After Map initializer");

			for (int y = 0; y < lvlHeight; y++) {
				for (int x = 0; x < lvlWidth; x++) {
					if(map[x][y] == 'X') {
						int xSize = 1;
						int ySize = 1;
						if(map[x+1][y] == 'X') {
							xSize++;
							while(map[x+xSize][y] == 'X') {
								xSize++;
								if(x+xSize >= lvlWidth) {
									break;
								}
							}
						} else if(map[x][y+1] == 'X') {
							ySize++;
							while(map[x][y+ySize] == 'X') {
								ySize++;
								if(y+ySize >= lvlHeight) {
									break;
								}
							}
						}
						level.walls.add(new Wall(xx+(5*x), yy+(5*y), size*xSize, size*ySize));
						System.out.println("Wall added");
						xSize--;
						ySize--;
						for(;xSize > 0;xSize--) {
							map[x+xSize][y] = 'o';
						}
						
						for(;ySize > 0;ySize--) {
							map[x][y+ySize] = 'o';
						}
						
					}
				}
			}

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error:" + e.getMessage());
		}
		return level;
	}
}
