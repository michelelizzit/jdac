/*
 *  JDAC Casio
 *  Software for data collection using Casio EA200/CLAB
 *  
 *  Compatible with JDAC
 *  Please visit https://lizzit.it/jdac for more informations about the project
 *
 *  Written by: Michele Lizzit <michele@lizzit.it>, 5 Feb 2017
 *  Last update: 5 Feb 2017
 *  Version: 1.0
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
 *  
 */
 
#include <SoftwareSerial.h>

#include "Config.h"

SoftwareSerial casio(CASIO_RX_PIN, CASIO_TX_PIN); // RX, TX

void setup() {
  Serial.begin(115200);

  casio.begin(38400);
  
  delay(100);


  //beep
  beep("1,1");
  delay(1500);
  
  //if sampling stop
  sendPacket("6,0");
  delay(1500);

  //beep
  beep("1,1");
  delay(1500);

  //select channel
  int type = SENSOR_TYPE;
  String sType = String(type);
  int channel = CHANNEL;
  String sChannel = String(channel);
  sendPacket("1," + sChannel + "," + sType + ",0");
  //real time
  sendPacket("12,0");
  //1s sampling
  sendPacket("3,0.0001,-1,0,-1,0,0,0");
  delay(1500);

  //beep
  beep("0.7,1");
  delay(200);
  beep("0.7,1");
  delay(200);
  beep("1,1");
  delay(1500);
}

void loop() {
  if (casio.available()) {
    receivePacket();
  }
  #ifdef SAMPLE_PERIOD
    delay(SAMPLE_PERIOD);
  #endif
  Serial.println(requestPacket()); 
}

void beep(String param) {
  #ifdef DEBUG
    Serial.println("Sending beep command");
  #endif
  sendPacket("11,0," + param);
}
