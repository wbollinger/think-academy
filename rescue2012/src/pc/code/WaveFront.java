package pc.code;

public class WaveFront {
	
	Map2D scanMap;
	
	
	boolean finished = false;
	
	public WaveFront(Map2D scanMap) {
		this.scanMap = scanMap;
		
		
	}
	
	int goal;
	
	int robot;
	
	public Map2D findPath(int goal) {
		
		Map2D overlay = new Map2D(scanMap);
		
		int up;
		int upRight;
		int right;
		int downRight;
		int down;
		int downLeft;
		int left;
		int upLeft;
		int center;
		
		robot = 4;
		
		
		int n = 0;
		
		while(!finished) {
			n++;
			for(int i = 1; i < Map2D.COLS-1; i++) {
				if(finished) {
					break;
				}
				for(int j = 1; j < Map2D.ROWS-1; j++) {
					
					center = overlay.grid[i][j];
					up = overlay.grid[i][j + 1];
					upRight = overlay.grid[i + 1][j + 1];
					right = overlay.grid[i + 1][j];
					downRight = overlay.grid[i + 1][j - 1];
					down = overlay.grid[i][j - 1];
					downLeft = overlay.grid[i - 1][j - 1];
					left = overlay.grid[i - 1][j];
					upLeft = overlay.grid[i - 1][j + 1];
					
					
					
					if ((center > 4)&&(center < 9)) {
						
						if ((up == robot)) {
							finished = true;
						} else if (upRight == robot) {
							finished = true;
						} else if (right == robot) {
							finished = true;
						} else if (downRight == robot) {
							finished = true;
						} else if (down == robot) {
							finished = true;
						} else if (downLeft == robot) {
							finished = true;
						} else if (left == robot) {
							finished = true;
						} else if (upLeft == robot) {
							finished = true;
						}
					} else if((center != goal)&&(center != robot)&&(center > 4)){
						
						if ((up > 4)&&(up < 9)) {
							if(up < center) {
								center = up+1;
							}
						}
						if ((upRight > 4)&&(upRight < 9)) {
							if(upRight < center) {
								center = upRight+1;
							}
						}
						if ((right > 4)&&(right < 9)) {
							if(right < center) {
								center = right+1;
							}
						}
						if ((downRight > 4)&&(downRight < 9)) {
							if(downRight < center) {
								center = downRight+1;
							}
						}
						if ((down > 4)&&(down < 9)) {
							if(down < center) {
								center = down+1;
							}
						}
						if ((downLeft > 4)&&(downLeft < 9)) {
							if(downLeft < center) {
								center = downLeft+1;
							}
						}
						if ((left > 4)&&(left < 9)) {
							if(left < center) {
								center = left+1;
							}
						}
						if ((upLeft > 4)&&(upLeft < 9)) {
							if(upLeft < center) {
								center = upLeft+1;
							}
						}
						
						if ((up == goal)) {
							center = 5;
							
						} else if (upRight == goal) {
							center = 5;
							
						} else if (right == goal) {
							center = 5;
							
						} else if (downRight == goal) {
							center = 5;
							
						} else if (down == goal) {
							center = 5;
							
						} else if (downLeft == goal) {
							center = 5;
							
						} else if (left == goal) {
							center = 5;
							
						} else if (upLeft == goal) {
							center = 5;
							
						}
						
					}
					overlay.grid[i][j] = center;
				}
			}
			System.out.println("Iteration "+n);
		}
		return overlay;
	}
	
	public static void main(String[] args) {
		Map2D test = new Map2D();
		test.seed();
		test.print();
		WaveFront path = new WaveFront(test);
		Map2D result = path.findPath(0);
		result.print();
	}
	
}
