package pc.code;

public class Map2D {
	public static final int SCALE = 30;
	public static final int COLS = (120/SCALE)+2;
	public static final int ROWS = (90/SCALE)+2;
	
	public static final int WALL = 1;		// Wall
	public static final int PLATFORM = 2;	// Platform
	public static final int CAN = 3;		// Can
	public static final int ROBOT = 4;		// Robot
	
	int[][] grid = new int[COLS][ROWS];
	
	public Map2D() {
		
		reset();
		
		/*
		room3[randRange(2,8)][randRange(2,11)] = CAN;
		
		switch (randRange(1,3)) {
		case 1:
			room3[1][1] = PLATFORM;
			room3[1][2] = PLATFORM;
			room3[1][3] = PLATFORM;
			room3[2][1] = PLATFORM;
			room3[3][1] = PLATFORM;
			room3[2][2] = PLATFORM;
			break;
		case 2:
			room3[1][COLS-2] = PLATFORM;
			room3[1][COLS-3] = PLATFORM;
			room3[1][COLS-4] = PLATFORM;
			room3[2][COLS-2] = PLATFORM;
			room3[3][COLS-2] = PLATFORM;
			room3[2][COLS-3] = PLATFORM;
			
			break;
		case 3:
			room3[ROWS-2][COLS-2] = PLATFORM;
			room3[ROWS-3][COLS-2] = PLATFORM;
			room3[ROWS-4][COLS-2] = PLATFORM;
			room3[ROWS-2][COLS-3] = PLATFORM;
			room3[ROWS-2][COLS-4] = PLATFORM;
			room3[ROWS-3][COLS-3] = PLATFORM;
			break;
		default:
			
		}
		*/
		
	}
	
	public Map2D(Map2D copy) {
		for(int i = 0; i < COLS; i++) {
			for(int j = 0; j < ROWS; j++) {
				this.grid[i][j] = copy.grid[i][j];
			}
		}
		
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
		grid[1][2] = CAN;
		grid[4][1] = PLATFORM;
		grid[2][1] = ROBOT;
		grid[1][3] = 0;
	}
	
	public void print() {
		System.out.println("------------");
		for (int j = ROWS - 1; j >= 0; j--) {
			for (int i = 0; i < COLS; i++) {
				if(grid[i][j] == PLATFORM) {
					System.out.print("P" + " ");
				} else if(grid[i][j] == CAN) {
					System.out.print("C" + " ");
				} else if(grid[i][j] == ROBOT) {
					System.out.print("R" + " ");
				} else {
					System.out.print(grid[i][j] + " ");
				}
				
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
	  
	public static void main(String[] args) {
		Map2D array = new Map2D();
		array.seed();
		array.print();
		
	}
}
