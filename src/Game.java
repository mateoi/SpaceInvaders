import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Game implements Runnable {
   public void run() {
      // Top-level frame
      final JFrame frame = new JFrame("Space Invaders");
      frame.setLocation(300, 300);
      frame.setResizable(false);

      // Main playing area
      final Battlefield field = new Battlefield();
      frame.add(field, BorderLayout.CENTER);

      // Reset button
      final JPanel panel = new JPanel();
      frame.add(panel, BorderLayout.NORTH);
      final JButton reset = new JButton("Reset");
      reset.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            field.reset();
         }
      });
      final JButton help = new JButton("Help");
      help.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			field.help();
			
		}
	});
      panel.add(reset);
      panel.add(help);

      // Put the frame on the screen
      frame.pack();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);

      // Start the game running
      field.reset();
      }

   /*
    * Get the game started!
    */
   public static void main(String[] args) {
       SwingUtilities.invokeLater(new Game());
   }

}
