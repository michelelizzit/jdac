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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by michele on 29/01/17.
 */
public class LabelClear {
    Timer t;
    public LabelClear(JLabel label) {
        t = new Timer(2000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                label.setText("  ");
            }
        });
        t.setRepeats(false);
    }

    public void resetTime() {
        if (t.isRunning()) t.stop();
        t.restart();
    }
}
