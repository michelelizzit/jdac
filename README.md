# JDAC

A simple open-source data collection system that collects data from different sensors using an Arduino board.  

JDAC is a Java application, that connects to the Arduino, plots the data in real-time and allows the user to export the data in CSV or PNG format.  

More informations on the project are available on the [project website](https://lizzit.it/jdac)

## Java application
JDAC can be launched by executing the jar file available in this repository.  

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
In order to connect to a Casio CLAB or Casio EA200, the Arduino must be connected to the 3.5mm jack connector on the unit, as specified in the Config.h file.  
Information on the protocol used by the unit is available on the official Casio website: http://support.casio.com/storage/en/manual/pdf/EN/004/EA200_TechnicalReference_EN.pdf  
The Arduino firmware "EA200" implements the protocol as specified in the official technical reference and collects the data from the sensor specified in the Config.h file.  
The Config.h file is placed in the "casioEA200" and contains some preprocessor directives that configure the firmware during compilation.  
