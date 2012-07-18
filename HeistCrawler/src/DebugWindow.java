import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


public class DebugWindow extends JFrame implements ActionListener{
	
	JButton IncreaseLevel;
	JButton DecreaseLevel;
	JButton Reload;
	JButton ChangePlayerCoord;
	
	
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
		ChangePlayerCoord = new JButton("Change Player Coordinates");
		ChangePlayerCoord.addActionListener(this);
		
		
		content.add(IncreaseLevel);
		content.add(DecreaseLevel);
		content.add(Reload);
		content.add(ChangePlayerCoord);
		
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
		} else if(b.getText().equals("Change Player Coordinates")) {
			b.setBackground(Color.YELLOW);
		}
	}
	
}
