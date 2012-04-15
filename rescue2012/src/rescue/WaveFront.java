package rescue;


public class WaveFront {
	
	Map2D scanMap;
	Map2D overlay;
	
	int x;
	int y;
	
	private int up;
	private int upRight;
	private int right;
	private int downRight;
	private int down;
	private int downLeft;
	private int left;
	private int upLeft;
	private int center;
	
	private boolean finished = false;
	
	public WaveFront(Map2D scanMap) {
		
		this.scanMap = scanMap;
		overlay = new Map2D(scanMap);
		x = 0;
		y = 0;
		
	}
	
	int goal;
	
	public Map2D makeWave(int goal) {
		
		overlay = new Map2D(scanMap);
		this.goal = goal;
		
		int counter = 0;
		int n;
		
		while(!finished) {
			counter++;
			for(x = 1; x < Map2D.COLS-1; x++) {
				if(finished) {
					break;
				}
				for(y = 1; y < Map2D.ROWS-1; y++) {
					
					center = overlay.grid[x][y];
					up = overlay.grid[x][y + 1];
					upRight = overlay.grid[x + 1][y + 1];
					right = overlay.grid[x + 1][y];
					downRight = overlay.grid[x + 1][y - 1];
					down = overlay.grid[x][y - 1];
					downLeft = overlay.grid[x - 1][y - 1];
					left = overlay.grid[x - 1][y];
					upLeft = overlay.grid[x - 1][y + 1];
					
					if (isSquareSet(center)||(center == goal)) {
						if (isCenterAjacentTo(Map2D.ROBOT)) {
							finished = true;
						} 
					} else if((center != goal)&&(center != Map2D.ROBOT)&&(center > 4)){
						n = center-1;
						while(n > 4) {
							if (isCenterAjacentTo(n)) {
								center = n+1;
							}
							n--;
						}
						if(isCenterAjacentTo(goal)) {
							center = 5;
						}
					}
					overlay.grid[x][y] = center;
				}
			}
			System.out.println("Iteration "+counter);
		}
		finished = false;
		return overlay;
	}
	
	public String pathTo(int x, int y) {
		int temp = scanMap.grid[x][y];
		scanMap.grid[x][y] = -1;
		makeWave(-1);
		String path = makePath();
		scanMap.grid[x][y] = temp;
		return path;
	}
	
	public String makePath () {
		int n;
		StringBuilder path = new StringBuilder();
		int[] robotPos;
		robotPos = overlay.findCoordinates(Map2D.ROBOT);
		x = robotPos[0];
		y = robotPos[1];
		
		n = 5;
		while (!finished) {
			center = overlay.grid[x][y];
			up = overlay.grid[x][y + 1];
			upRight = overlay.grid[x + 1][y + 1];
			right = overlay.grid[x + 1][y];
			downRight = overlay.grid[x + 1][y - 1];
			down = overlay.grid[x][y - 1];
			downLeft = overlay.grid[x - 1][y - 1];
			left = overlay.grid[x - 1][y];
			upLeft = overlay.grid[x - 1][y + 1];
			
			if(isCenterAjacentTo(goal)) {
				path.append(dirTo(goal));
				finished = true;
				
			} else if ((up == n)) {
				y++;
				n--;
				path.append("w");
				
			} else if (upRight == n) {
				x++;
				y++;
				n--;
				path.append("e");
				
			} else if (right == n) {
				x++;
				n--;
				path.append("d");
				
			} else if (downRight == n) {
				x++;
				y--;
				n--;
				path.append("c");
				
			} else if (down == n) {
				y--;
				n--;
				path.append("x");
				
			} else if (downLeft == n) {
				x--;
				y--;
				n--;
				path.append("z");
			} else if (left == n) {
				y--;
				n--;
				path.append("a");
			} else if (upLeft == n) {
				x++;
				y--;
				n--;
				path.append("q");
			} else {
				n++;
			}
		}
		
		
		return path.toString();
	}
	
	
	public static boolean isSquareSet(int num) {
		if((num > 4)&&(num < 9)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isCenterAjacentTo(int val) {
		
		if ((up == val)) {
			return true;
			
		} else if (upRight == val) {
			return true;
			
		} else if (right == val) {
			return true;
			
		} else if (downRight == val) {
			return true;
			
		} else if (down == val) {
			return true;
			
		} else if (downLeft == val) {
			return true;
			
		} else if (left == val) {
			return true;
			
		} else if (upLeft == val) {
			return true;
			
		} else {
			return false;
		}
	}
	
	public char dirTo(int val) {
		
		if ((up == val)) {
			return 'w';
			
		} else if (upRight == val) {
			return 'e';
			
		} else if (right == val) {
			return 'd';
			
		} else if (downRight == val) {
			return 'c';
			
		} else if (down == val) {
			return 'x';
			
		} else if (downLeft == val) {
			return 'z';
			
		} else if (left == val) {
			return 'a';
			
		} else if (upLeft == val) {
			return 'q';
			
		} else {
			return 's';
		}
		
	}
	
	

//	public static void main(String[] args) {
//		Map2D test = new Map2D();
//		
//		test.seed();
//		test.print();
//		WaveFront path = new WaveFront(test);
//		System.out.println("Starting pathing");
//		Map2D result = path.makeWave(Map2D.CAN);
//		System.out.println("Wave created");
//		result.print();
//		System.out.println("----------");
//		System.out.println("Making Path");
//		System.out.println(path.makePath());
//		System.out.println("Path made");
//	}
	

}