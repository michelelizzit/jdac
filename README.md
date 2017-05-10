# JDAC

A simple open-source data collection system that collects data from different sensors using an Arduino board.  

JDAC is a Java application, that connects to the Arduino, plots the data in real-time and allows the user to export the data in CSV or PNG format.  

_More informations on the project are available on the [project website](https://lizzit.it/jdac)_

## Java application

### Description of the application interface
JDAC has a simple and user-friendly interface.  
   
The first chart contains all the data that will be saved in the CSV file, the seconds chart shows only the last data values, the number of data values shown in the second chart can be configured using the 'Shown values' element of the interface.  
Collected data can be saved using the 'Save as CSV' button in the 'File' menu.  
An exported CSV file contains two columns with one row for each data value, the first one indicates the value while the second one indicates the time at which the value has been acquired.  
The time in the CSV file is expressed as milliseconds since Unix epoch (1 Jan 1970).  
As any other CSV file it can be imported in all the major spreadsheet software.  
  
A sensor type can be selected using the 'Sensor' menu the application displays the correct unit on the charts based on the selected sensor.  
When selecting the 'Generic' sensor, no unit is displayed.  
  
The serial port used by the software can be selected in the 'Device>Port' menu.  
On Linux-based OSes Arduino boards usually display as ttyACMx or ttyUSBx.  
The serial port list is refreshed on startup or when 'Device>Refresh port list' is clicked.  

| ![Logo](/screenshots/screen1.png) |
|---|
| A screenshot of the JDAC Java application |

### How to collect data
JDAC can be launched by executing the jar file available in this repository.  
In order to start collecting data the user has to:
* Make sure that a valid firmware is loaded on the Arduino
* Connect the Arduino to a USB port on the computer
* Start JDAC by executing the jar file
* Select a valid serial port from Device>Port (on Linux is usually ttyACMx or ttyUSBx, on windows COMx)
* Connect to the Arduino by clicking Device>Connect
* Start collecting data by clicking 'Start'
* Run the experiment, data will show on the charts in real time
* Stop collecting data by clicking 'Stop'
* Save the collected data by clicking File>Save as CSV or discard it by clicking 'Reset'
* The exported CSV file can be then imported on any major spreadsheet software

| ![Logo](/logo.png) |
|---|
| The application logo |

## Arduino firmware
JDAC can connect to any Arduino board that sends the data, separated by the newline character, via serial at 115200bps.  
In this repository there are some example Arduino sketches:  
### Random
Generates random data without requiring any sensor to be connected.  
Useful for testing purposes.  
### DS18B20
It interfaces with a DS18B20 temperature probe.
### Casio EA200
Connects to a Casio EA200 or Casio CLAB, and collects the data from one of the sensors connected.  
In order to connect to a Casio CLAB or Casio EA200, the Arduino must be connected to the 2.5mm jack connector on the unit, as specified in the Config.h file.  
The 2.5mm jack on the Casio unit is actually a 5V TTL serial port working at 38400baud, the unit usually connects to a Casio calculator and transfers the data to the calculator, however it can be connected to any other device implementing the right protocol.  
More information on the protocol used by the unit is available on the official Casio website: http://support.casio.com/storage/en/manual/pdf/EN/004/EA200_TechnicalReference_EN.pdf  
The Arduino firmware "EA200" implements the protocol as specified in the official technical reference and collects the data from the sensor specified in the Config.h file.  
The Config.h file is placed in the "casioEA200" folder and contains some preprocessor directives that configure the firmware during compilation.  
### Custom firmware
In order to write a JDAC compatible firmware the firmware has to collect the data and send it via the USB serial port at 115200baud, each value has to be separated by a newline character ("\n", ASCII code 10) and has to use a dot (".") as decimal mark, as soon as a value is received the JDAC application adds a point to the chart.
