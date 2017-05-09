/*
 *  JDAC
 *  Author: Michele Lizzit - lizzit.it
 *  v1.2 - 9/5/2017
 *
 *  Please go to https://lizzit.it/jdac for more informations about the project
 *
 *
 *  Copyright (C) 2017  Michele Lizzit
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package JDAC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by michele on 29/01/17.
 */
public class AboutFrame extends Frame {
    public AboutFrame() {
        super();

        ImageIcon img = new ImageIcon("logo.png");
        setIconImage(img.getImage());

        setSize(460, 150);
        setResizable(false);

        setAlwaysOnTop(true);

        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent we) {
                                  dispose();
                              }
                          }
        );
        setLayout(new BorderLayout());

        setTitle("About " + Definitions.title);

        JPanel closePanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closePanel.add(closeButton);
        add(closePanel, BorderLayout.PAGE_END);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        closePanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        JPanel labelPanel = new JPanel();
        JLabel label = new JLabel("<html><center>" +
                "JDAC<br>" +
                "<br>" +
                "JDAC is an open-source data collection software written by<br>" +
                "Michele Lizzit<br>" +
                "https://lizzit.it/jdac" +
                "</center></html>");

        labelPanel.add(label);
        add(labelPanel);

        //center on screen
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);

        setVisible(true);
    }
}
