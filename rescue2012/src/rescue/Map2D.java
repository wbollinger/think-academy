package rescue;



public class Map2D {
	public static final int SCALE = 30;
	public static final int COLS = (120/SCALE)+2;
	public static final int ROWS = (90/SCALE)+2;
	
	public static final int WALL = 1;		// Wall
	public static final int CAN = 2;		// Can
	public static final int PLATFORM = 3;	// Platform
	public static final int ROBOT = 4;		// Robot
	
	int[][] grid = new int[COLS][ROWS];
	
	public Map2D() {
		
		reset();
		
	}
	
	public Map2D(Map2D copy) {
		for(int i = 0; i < COLS; i++) {
			for(int j = 0; j < ROWS; j++) {
				this.grid[i][j] = copy.grid[i][j];
			}
		}
		
	}
	
	public boolean isInCenter(int goal) {
		int[] coordinates = findCoordinates(goal);
		int x = coordinates[0];
		int y = coordinates[1];
		if(((x == 2)||(x == 3)) && (y == 2)) {
			return true;
		}
		return false;
	}
	
	public int[] findCoordinates(int goal) {
		boolean finished = false;
		int[] coordinates = new int[2];
		int y = 1;
		int x = 1;
		for(x = 1; x < Map2D.COLS-1; x++) {
			for(y = 1; y < Map2D.ROWS-1; y++) {
				if(grid[x][y]==goal) {
					finished = true;
					break;
				}
			}
			if(finished) {
				break;
			}
		}
		coordinates[0] = x;
		coordinates[1] = y;
		return coordinates;
		
	}
	
	public void reset() {
		for(int i = 0; i < COLS; i++) {
			for(int j = 0; j < ROWS; j++) {
				if(i == 0 || i == (COLS-1) || j == 0 || j == (ROWS-1)) {
					grid[i][j] = WALL;
				} else {
					grid[i][j] = 0;
				}
			}
		}
	}
	
	public void seed() {
		for(int i = 0; i < COLS; i++) {
			for(int j = 0; j < ROWS; j++) {
				if(i == 0 || i == (COLS-1) || j == 0 || j == (ROWS-1)) {
					grid[i][j] = WALL;
				} else {
					grid[i][j] = 9;
				}
			}
		}
		grid[3][3] = CAN;
		grid[1][3] = PLATFORM;
		grid[2][3] = ROBOT;
	}
	
	public void run() {
		for(int j = ROWS-1; j >= 0; j--) {
			for(int i = 0; i < COLS; i++) {
				System.out.print(grid[i][j]);
				System.out.print(" ");
			}
			System.out.println("");
		}
	}
	
	static int randRange(int num1, int num2) {
		
	    double rand = Math.random();
	    int range = Math.abs(num2-num1);
	    int count = 0;
	        
	    while (rand > 0.0) {
	      rand -=(1.0/range);
	      count++;
	    }
	    count += num1;
	    return(count);    
	} 
	  
	public static void test_main(String[] args) {
		Map2D array = new Map2D();
		array.run();
		
	}
}
