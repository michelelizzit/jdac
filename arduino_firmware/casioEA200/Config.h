/*
 * Sensor type:
 * 1) Auto
 * 2) Voltage (0-10V)
 * 4) Resistance (1-100kOhm)
 * 5) Period (sec)
 * 6) Frequency (Hz)
 * 7) Temperature (-29 - 130C)
 * 8) Temperature (-4 - 266F)
 * 9) Light (100-999)
 * 10) Voltage (0-5V)
 * 11) Time (sec)
 */
#define SENSOR_TYPE 7

/*
 * Channel:
 * 1) CH1
 * 2) CH2
 * 3) CH3
 */
#define CHANNEL 1

/*
 * These pins have to be connected to the 3.5mm jack connector on CLAB
 */
#define CASIO_RX_PIN 10
#define CASIO_TX_PIN 11

//#define DEBUG

//Sample period (ms)
//Comment to achieve max sample rate
#define SAMPLE_PERIOD 10
