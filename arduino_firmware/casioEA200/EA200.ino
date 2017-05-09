/*
 *  EA200
 *  Support methods for communication with Casio EA200
 *
 *  Written by: Michele Lizzit <michele@lizzit.it>, 5 Feb 2017
 *  Last update: 5 Feb 2017
 *  Version: 1.0
 *
 *  Copyright (c) 2017 Michele Lizzit
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *  
 */

void receivePacket() {
  byte b = casio.read();
    if (b == (byte)21) {
      casio.write((byte)19);
      //Serial.println("\n\nOK CodeA Receiving header");
      while (casio.available() < 15); //wait for buffer to fill

      casio.read();
      
      if ((char)casio.read() == 'R') {
        //Serial.println("ERR: receive not implemented");
        return;
      }
      
      for (int i = 0; i < 4; i++) casio.read();
      int pSize = 0;
      for (int i = 0; i < 4; i++) pSize = ((pSize << 8) + casio.read());
      for (int i = 0; i < 5; i++) casio.read();
      
      //Serial.print("Received header\npSize = ");
      //Serial.println(pSize);

      casio.write(6); //Code B OK
      //Serial.println("Sending Code B");

      int bToRec = 2 + pSize;//num bytes to receive

      while (casio.available() < bToRec); //wait for buffer to fill

      byte tmp = casio.read();
      if ((char)tmp == ':') {
        //Serial.println("Receiving packet");
      }
      else {
        Serial.println("Packet error, unexpected ");
        Serial.print(tmp);
      }

      char packet[pSize];

      for (int i = 0; i < pSize; i++) {
        packet[i] = casio.read();
        casio.read();
      }

      //Serial.println("Sending Code B OK");
      casio.write(6); //Code B OK
      
      //Serial.println("Packet received\nContent: ");

      for (int i = 0; i < pSize; i++) {
        //Serial.println(packet[i]);
      }

    }
}

void sendPacket(String content) {
  #ifdef DEBUG
  Serial.print("Sending packet: ");
  Serial.println(content);
  #endif
  
  #ifdef DEBUG
  Serial.println("Buffer content was: ");
  #endif
  
  while (casio.available()) {
    byte b = casio.read();
    #ifdef DEBUG
    Serial.print(b, HEX);
    Serial.print(",");
    Serial.write(b);
    Serial.print(" ");
    #endif
  }
  #ifdef DEBUG
  Serial.print("\n");
  #endif
  
  casio.write(21); //0x15
  while (!casio.available());

  byte b = casio.read();
  #ifdef DEBUG
  Serial.print("Received ");
  Serial.println(b, HEX);
  #endif
  delayMicroseconds(50);
  if (b != 19) Serial.println("ERR");

  #ifdef DEBUG
  Serial.println("Sending header...");
  #endif

  char header[15];
  header[0] = ':';
  header[1] = 'N';
  //type
  header[2] = 'A';
  //form
  header[3] = 'V';
  //line
  header[4] = 0;
  header[5] = 1;
  //offset
  header[6] = 0;
  header[7] = 0;
  header[8] = 0;
  header[9] = 1;
  //size
  int pLen = content.length();
  header[11] = (byte)pLen;
  header[10] = (byte)pLen >> 8;
  //0xFF
  header[12] = (byte)255;
  //Area
  header[13] = 'A';
  //Checksum
  long sum = 0;
  for (int i = 1; i < 14; i++) {
    sum += byte(header[i]);
  }
  byte res = byte(sum);
  res = ~res;
  res++;
  header[14] = res;
  //send header
  for (int i = 0; i < 15; i++) {
    casio.write(byte(header[i]));
    delayMicroseconds(100);
    #ifdef DEBUG
    Serial.print(byte(header[i]), HEX);
    Serial.print(" ");
    #endif
  }
  #ifdef DEBUG
  Serial.println("\nHeader sent");
  #endif

  //wait for response
  while (!casio.available());
  b = casio.read();
  #ifdef DEBUG
  Serial.print("Received ");
  Serial.println(b, HEX);
  #endif
  delayMicroseconds(500);
  if (b != 6) Serial.println("ERR");


  //Serial.println("Sending packet...");

  int pSize = pLen + 2;
  char packet[pSize];
  packet[0] = ':';
  int cnt = 0;
  for (int i = 0; i < content.length(); i++) {
    #ifdef DEBUG
    Serial.println(int(content.charAt(i)));
    #endif
    packet[++cnt] = content.charAt(i);
  }
  //checksum
  sum = 0;
  for (int i = 1; i < pSize - 1; i++) {
    sum += byte(packet[i]);
  }
  res = (byte)sum;
  res = ~res;
  res++;
  packet[pSize - 1] = res;
  //send packet
  for (int i = 0; i < pSize; i++) {
    casio.write(byte(packet[i]));
    delayMicroseconds(100);
    /*Serial.print(byte(packet[i]), HEX);
    Serial.print(",");
    Serial.write(byte(packet[i]));
    Serial.print(" ");*/
  }
  
  //Serial.println("\nPacket sent");

  //wait for response
  while (!casio.available());
  b = casio.read();
  #ifdef DEBUG
  Serial.print("Received ");
  Serial.println(b, HEX);
  #endif
  delayMicroseconds(100);
  if (b != 6) Serial.println("ERR");
  
  //Serial.println("Success\n");
}

String requestPacket() {
  //Serial.println("Requesting packet...");
  
  //Serial.println("Buffer content was: ");
  while (casio.available()) {
    byte b = casio.read();
    /*Serial.print(b, HEX);
    //Serial.print(",");
    //Serial.write(b);
    //Serial.print(" ");*/
  }
  //Serial.print("\n");
  casio.write(21); //0x15
  while (!casio.available());

  byte b = casio.read();
  //Serial.print("Received ");
  //Serial.println(b, HEX);
  if (b != 19) Serial.println("ERR");

  //Serial.println("Sending header...");

  char header[15];
  header[0] = ':';
  header[1] = 'R';
  //type
  header[2] = 'A';
  //header[2] = 'L';
  //form
  header[3] = 'V';
  //all 0xFF
  for (int i = 4; i < 14; i++) {
    header[i] = byte(0xFF);
  }
  //Checksum
  long sum = 0;
  for (int i = 1; i < 14; i++) {
    sum += byte(header[i]);
  }
  byte res = byte(sum);
  res = ~res;
  res++;
  header[14] = res;
  //send header
  for (int i = 0; i < 15; i++) {
    casio.write(byte(header[i]));
    delayMicroseconds(10);
    //Serial.print(byte(header[i]), HEX);
    //Serial.print(" ");
  }
  //Serial.println("\nHeader sent");

  //wait for response
  while (casio.available() < 15);

  //Serial.println("Received header");
  //byte header[15];
  for (int i = 0; i < 15; i++) {
    header[i] = casio.read();
    //Serial.print(byte(header[i]), HEX);
    //Serial.print(";");
    //Serial.write(byte(header[i]));
    //Serial.print(" ");
  }
  //Serial.print("\n");
  
  //Serial.println("Sending code B");
  casio.write(6);

  int pLen = (header[10] << 8) + header[11];
  int pSize = pLen + 2;
  //Serial.print("Declared packet size is: ");
  //Serial.print(pSize);
  //Serial.print("\n");
  
  //wait for response
  while (casio.available() < pSize);
  //Serial.println("Received packet");

  byte packet[pSize];
  for (int i = 0; i < pSize; i++) {
    packet[i] = casio.read();
    //Serial.print(byte(packet[i]), HEX);
    //Serial.print(";");
    //Serial.write(byte(packet[i]));
    //Serial.print(" ");
  }
  //Serial.print("\n");

  //Serial.println("Sending code B");
  casio.write(6);
  
  String result = "";
  for (int i = 1; i < pSize - 1; i++) {
    result += char(packet[i]);
  }
  return result;
}
