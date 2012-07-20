import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.sound.sampled.*;

public class MakeSound {

    public static void playSound(String sound) throws Exception {
    	try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(sound));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
    }
    
    catch(UnsupportedAudioFileException uae) {
            System.out.println(uae);
    }
    catch(IOException ioe) {
            System.out.println(ioe);
    }
    catch(LineUnavailableException lua) {
            System.out.println(lua);
    }
    }
}