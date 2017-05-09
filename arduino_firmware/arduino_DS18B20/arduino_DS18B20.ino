/*
 *  JDAC DS18B20
 *  Software for data collection using a DB18B20 temperature probe
 *  
 *  Compatible with JDAC
 *  Please visit https://lizzit.it/jdac for more informations about the project
 *
 *  Written by: Michele Lizzit <michele@lizzit.it>, 9 May 2017
 *  Last update: 9 May 2017
 *  Version: 1.1
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

#include <OneWire.h>

#define VCC_PIN 4
#define GND_PIN 3
#define DS18B20_PIN 2

OneWire ds(DS18B20_PIN);

void setup() {
  Serial.begin(115200);
  
  pinMode(VCC_PIN, OUTPUT);
  pinMode(GND_PIN, OUTPUT);
  digitalWrite(VCC_PIN, HIGH);
  digitalWrite(GND_PIN, LOW);
}

void loop() {
  getTemp();
}

void getTemp() {
  byte i;
  byte data[12];
  byte addr[8];
 
 
  ds.reset_search();
  if ( !ds.search(addr)) {
      Serial.print("ERR\n");
      delay(800);
      ds.reset_search();
      return;
  }



  ds.reset();
  ds.select(addr);
  ds.write(0x44,1);

  delay(100);

  ds.reset();
  ds.select(addr);    
  ds.write(0xBE);

  for ( i = 0; i < 9; i++) {
    data[i] = ds.read();
  }
  
  int dataLowByte = data[0];
  int dataHighByte = data[1];
  int tmp_temp = (dataHighByte << 8) + dataLowByte;
  int isNegative = tmp_temp & 0x8000;
  if (isNegative) {
    tmp_temp = (tmp_temp ^ 0xffff) + 1;
  }
  float temperature = (6.25 * (float)tmp_temp);


  if (isNegative) {
     Serial.print("-");
  }
  Serial.print((float)temperature / 100);

  Serial.print("\n");
}
