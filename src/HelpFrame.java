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
public class HelpFrame extends Frame {
    public HelpFrame() {
        super();

        ImageIcon img = new ImageIcon("logo.png");
        setIconImage(img.getImage());

        setSize(800, 540);
        setResizable(false);

        setAlwaysOnTop(true);

        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent we) {
                                  dispose();
                              }
                          }
        );
        setLayout(new BorderLayout());

        setTitle("Help");

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
                "This software collects data from an Arduino or other board connected via a serial port<br>" +
                "Each value sent via serial has to be separated by a newline character (ASCII code 10)<br>" +
                "Default baud rate is 115200 and is not configurable via the interface<br>" +
                "Each data value is shown on the charts as soon as the newline character is received<br>" +
                "<br>" +
                "The first chart contains all the data that will be saved in the CSV file<br>" +
                "The seconds chart shows the last data values,<br>" +
                "the number of data values shown in the second chart <br>" +
                "can be configured using the 'Shown values' element of the interface<br>" +
                "<br>" +
                "Collected data can be saved using the 'Save as CSV' button in the 'File' menu<br>" +
                "An exported CSV file contains two columns with one row for each data value,<br>" +
                "the first one indicates the value <br>" +
                "while the second one indicates the time at which the value has been acquired.<br>" +
                "The time is expressed as milliseconds since Unix epoch (1 Jan 1970)<br>" +
                "As any other CSV file it can be imported in all the major spreadsheet software<br>" +
                "<br>" +
                "A sensor type can be selected using the 'Sensor' menu<br>" +
                "the application displays the correct unit on the charts based on the selected sensor.<br>" +
                "When selecting the 'Generic' sensor, no unit is displayed<br>" +
                "<br>" +
                "The serial port used by the software can be selected in the 'Device>Port' menu<br>" +
                "On Linux-based OSes Arduino boards usually display as ttyACMx or ttyUSBx<br>" +
                "The serial port list is refreshed on startup or when 'Device>Refresh port list' is clicked<br>" +
                "<br>" +
                "<br>" +
                "Software developed by: <br>" +
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
