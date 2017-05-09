/*  
 *  Arduino firmware for JDAC
 *  This firmware generates random data without requiring any sensor connected.
 *  
 *  Author: Michele Lizzit - lizzit.it
 *  v1.1 - 9/5/2017
 *  
 *  Please visit https://lizzit.it/jdac for more informations about the project
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

void setup() {
  Serial.begin(115200);
}

void loop() {
  Serial.println((float)random(10000)/100);
  delay(100);
}
