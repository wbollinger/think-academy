package pc.code;

public class RandomCW {
  
  static String coinFlip() {
    double rand = Math.random();
    if (rand < 0.49) {
      return("Heads");
    }
    else if(rand > 0.51) {
      return("Tails");
    }
    else {
      return("Edge");
    }
  }

  static int diceRoll6() {
    double rand = Math.random();

    if (rand < 0.16) {
      return(1);
    }
    else if((rand > 0.16) && (rand < .33)) {
      return(2);
    }
    else if((rand > 0.33) && (rand < .5)) {
      return(3);
    }
    else if((rand > 0.5) && (rand < .66)) {
      return(4);
    }
    else if((rand > 0.67) && (rand < .83)) {
      return(5);
    }
    else {
      return(6);
    }
  }

  static int diceRoll20() {
    double rand = Math.random();

    if (rand < 0.05) {
      return(1);
    }
    else if((rand > 0.05) && (rand < .1)) {
      return(2);
    }
    else if((rand > 0.1) && (rand < .15)) {
      return(3);
    }
    else if((rand > 0.15) && (rand < .2)) {
      return(4);
    }
    else if((rand > 0.2) && (rand < .25)) {
      return(5);
    }
    else if((rand > 0.25) && (rand < .3)) {
      return(6);
    }
    else if((rand > 0.3) && (rand < .35)) {
      return(7);
    }
    else if((rand > 0.35) && (rand < .4)) {
      return(8);
    }
    else if((rand > 0.4) && (rand < .45)) {
      return(9);
    }
    else if((rand > 0.45) && (rand < .5)) {
      return(10);
    }
    else if((rand > 0.5) && (rand < .55)) {
      return(11);
    }
    else if((rand > 0.55) && (rand < .6)) {
      return(12);
    }
    else if((rand > 0.6) && (rand < .65)) {
      return(13);
    }
    else if((rand > 0.65) && (rand < .7)) {
      return(14);
    }
    else if((rand > 0.7) && (rand < .75)) {
      return(15);
    }
    else if((rand > 0.75) && (rand < .8)) {
      return(16);
    }
    else if((rand > 0.8) && (rand < .85)) {
      return(17);
    }
    else if((rand > 0.85) && (rand < .9)) {
      return(18);
    }
    else if((rand > 0.9) && (rand < .95)) {
      return(19);
    }
    else {
      return(20);
    }
  }

  static int diceRoll(int sides) {

    double rand = Math.random();
    int count = 0;
        
    while (rand > 0.0) {
      rand -=(1.0/sides);
      count++;
    }

    return(count);

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
    int roll;
    int stat[] = new int[6];
    
    for(int count = 0; count < 10000; count++) {
      roll = diceRoll(6);
      stat[roll-1]++;
    }
    System.out.println(stat[0] + "," + stat[1] + "," + stat[2] + "," + stat[3] + "," + stat[4] + "," + stat[5]); 
  }
}