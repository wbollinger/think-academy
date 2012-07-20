import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class DebugWindow extends JFrame implements ActionListener{
	
	JButton IncreaseLevel;
	JButton DecreaseLevel;
	JButton Reload;
	JButton DramaticSound;
	
	
	public DebugWindow () {
		JPanel content = new JPanel();
		content.setLayout(new GridLayout());
		this.setContentPane(content);
		setResizable(false);
		setTitle("Debug Options");
		
		IncreaseLevel = new JButton("Increase Level");
		IncreaseLevel.addActionListener(this);
		DecreaseLevel = new JButton("Decrease Level");
		DecreaseLevel.addActionListener(this);
		Reload = new JButton("Reload Level");
		Reload.addActionListener(this);
		DramaticSound = new JButton("Dramatic Sound");
		DramaticSound.addActionListener(this);
		
		
		content.add(IncreaseLevel);
		content.add(DecreaseLevel);
		content.add(Reload);
		content.add(DramaticSound);
		
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JButton b = (JButton) e.getSource();
		
		if(b.getText().equals("Increase Level")) {
			HeistCore.heist.increaseLevel();
			HeistCore.heist.loadCurrentLevel();
		} else if(b.getText().equals("Decrease Level")) {
			HeistCore.heist.decreaseLevel();
			HeistCore.heist.loadCurrentLevel();
		} else if(b.getText().equals("Reload Level")) {
			HeistCore.heist.loadCurrentLevel();
		} else if(b.getText().equals("Dramatic Sound")) {
			try {
				MakeSound.playSound("Sounds/inception_sound.wav");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
//			b.setBackground(Color.YELLOW);
		}
	}
	
}
