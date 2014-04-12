#include <string.h>
#include <Wire.h>
#include <SoftwareSerial.h>
#include <AFMotor.h>
#include <Servo.h>
#include <string.h>
#include <assert.h>
#include <ADXL345.h>
#include "connection.h"
#include "protocol.h"
#include "hmc5883.h"

struct UltrasonicSensor
{
  char echoPin;
  char trigPin;
};

enum Direction {
  FORWARD_DIRECTION = 0,
  BACKWARD_DIRECTION = 1,
  LEFT_TURN = 2,
  RIGHT_TURN = 3
};

//  ----  Context ----
AF_DCMotor leftHeadMotor(1);
AF_DCMotor leftTailMotor(4);
AF_DCMotor rightHeadMotor(2);
AF_DCMotor rightTailMotor(3);

AF_DCMotor gMotors[] = { leftHeadMotor, leftTailMotor, rightHeadMotor, rightTailMotor };

Servo servo;

UltrasonicSensor ultrasonicSensors[] = 
{
  {char(26),char(27)},
  {char(30),char(31)},
  {char(34),char(35)},
  {char(52),char(53)}
};

const unsigned gUltrasonicSensorsNumber = sizeof(ultrasonicSensors) / sizeof(ultrasonicSensors[0]);

ADXL345 accelMeter;
HMC5883 compas;

const uint16_t ledsPin = 45;
//   ----

int16_t initSensors( ::UltrasonicSensor* sensors, int16_t number ){
  int16_t i = 0;
  for(  ;i < number; ++i ){
    pinMode(sensors[i].trigPin, OUTPUT);
    pinMode(sensors[i].echoPin, INPUT);
  }
}

int16_t measureDistance( ::UltrasonicSensor sensor ){
    int16_t duration, distance; // Duration used to calculate distance

    digitalWrite(sensor.trigPin, LOW); 
    delayMicroseconds(10); 

    digitalWrite(sensor.trigPin, HIGH);
    delayMicroseconds(6); 

    digitalWrite(sensor.trigPin, LOW);
    duration = pulseIn(sensor.echoPin, HIGH);

    distance = (duration / 100) * 1.657;
    return distance; 
}

int16_t moveHandler(const uint8_t* requestBuffer, 
                 uint16_t size, 
                 uint8_t* response, 
                 uint16_t responseSize){
  
  const uint8_t direction = requestBuffer[0];
  const uint8_t speed = requestBuffer[1];

  leftHeadMotor.setSpeed((uint8_t)speed);
  leftTailMotor.setSpeed((uint8_t)speed);
  rightHeadMotor.setSpeed((uint8_t)speed);
  rightTailMotor.setSpeed((uint8_t)speed);

  switch( direction ){
    case FORWARD_DIRECTION:
      leftHeadMotor.run(FORWARD);
      leftTailMotor.run(FORWARD);
      rightHeadMotor.run(FORWARD);
      rightTailMotor.run(FORWARD);
      break;

    case BACKWARD_DIRECTION:
      leftHeadMotor.run(BACKWARD);
      leftTailMotor.run(BACKWARD);
      rightHeadMotor.run(BACKWARD);
      rightTailMotor.run(BACKWARD);
      break;

    case LEFT_TURN:
      leftHeadMotor.run(BACKWARD);
      leftTailMotor.run(BACKWARD);
      rightHeadMotor.run(FORWARD);
      rightTailMotor.run(FORWARD);
      break;

    case RIGHT_TURN:
      leftHeadMotor.run(FORWARD);
      leftTailMotor.run(FORWARD);
      rightHeadMotor.run(BACKWARD);
      rightTailMotor.run(BACKWARD);
      break;
  }
  
  return 0;  
}

int16_t stopHandler(const uint8_t* requestBuffer, 
                 uint16_t size, 
                 uint8_t* response, 
                 uint16_t responseSize){
  
  leftHeadMotor.run(RELEASE);
  leftTailMotor.run(RELEASE);
  rightHeadMotor.run(RELEASE);
  rightTailMotor.run(RELEASE);

  return 0;
}

int16_t servoHandler(const uint8_t* requestBuffer, 
                 uint16_t size, 
                 uint8_t* response, 
                 uint16_t responseSize){
  servo.write( (uint8_t)requestBuffer[0] );
  return 0;
}



int16_t distanceMeasureHandler(const uint8_t* requestBuffer, 
                           uint16_t size, 
                           uint8_t* response, 
                           uint16_t responseSize){
  uint8_t sensorNumber = requestBuffer[0];
  
  int16_t result = measureDistance( ultrasonicSensors[sensorNumber] );
  response[0] = (result >> 8) & 0xff;
  response[1] = result & 0xff;
                    
  return 2; 
}



int16_t accelMeasureHandler(const uint8_t* requestBuffer, 
                        uint16_t size, 
                        uint8_t* response, 
                        uint16_t responseSize){ 
  int16_t vector[3];
  
  accelMeter.read(&vector[0],&vector[1],&vector[2]); 
  int16_t responseLen = 0; 
  
  for( int16_t i = 0; i < sizeof(vector) / sizeof(vector[0]); ++i ){
    memcpy( &response[responseLen], (void*)&vector[i], sizeof(vector[i]));
    responseLen += sizeof(vector[i]);
  }
  
  return responseLen;
}



int16_t compasMeasureHandler(const uint8_t* requestBuffer, 
                        uint16_t size, 
                        uint8_t* response, 
                        uint16_t responseSize){ 
  int16_t vector[3];
  compas.read(&vector[0],&vector[1],&vector[2]);
  
  int16_t responseLen = 0;
  
  for( int16_t i = 0; i < sizeof(vector) / sizeof(vector[0]); ++i ){
    memcpy( &response[responseLen], (void*)&vector[i], sizeof(vector[i]));
    responseLen += sizeof(vector[i]);
  }
  
  return responseLen;
}

int16_t motorControllHandler(const uint8_t* requestBuffer, 
                         uint16_t size, 
                         uint8_t* response, 
                         uint16_t responseSize){
  
  const uint8_t direction = requestBuffer[0];
  const uint8_t speed = requestBuffer[1];
  const uint8_t motorNumber = requestBuffer[2];
  
  if( motorNumber > 3 )
    return 0;
   
  gMotors[motorNumber].setSpeed(speed);
  
  switch( direction ){
    case FORWARD_DIRECTION:
      gMotors[motorNumber].run(FORWARD);
      break;
    default:
      gMotors[motorNumber].run(BACKWARD);
  }
 
  return 0; 
}

int16_t directionVectorHandler(const uint8_t* requestBuffer, 
                           uint16_t size, 
                           uint8_t* response, 
                           uint16_t responseSize){
  
  int16_t* xy = (int16_t*)requestBuffer;
  int16_t x = xy[0];
  int16_t y = xy[1];
  
  int16_t leftMotorsPercents = (y - ((float)x * 3 / 2)) * 3;
  int16_t leftMotorsDirection = FORWARD;
  if( leftMotorsPercents < 0 )
      leftMotorsDirection = BACKWARD;

  leftMotorsPercents = abs(leftMotorsPercents);
  if( leftMotorsPercents > 255)
      leftMotorsPercents = 255;
  
  gMotors[0].setSpeed(leftMotorsPercents);
  gMotors[1].setSpeed(leftMotorsPercents);
  gMotors[0].run(leftMotorsDirection);
  gMotors[1].run(leftMotorsDirection);

  int16_t rightMotorsPercents = y + ((float)x * 3 / 2) * 3;
  int16_t rightMotorsDirection = FORWARD;
  if( rightMotorsPercents < 0 )
      rightMotorsDirection = BACKWARD;

  rightMotorsPercents = abs(rightMotorsPercents);
  if( rightMotorsPercents > 255)
      rightMotorsPercents = 255;

  gMotors[2].setSpeed(rightMotorsPercents * 3);
  gMotors[3].setSpeed(rightMotorsPercents * 3);
  gMotors[2].run(rightMotorsDirection);
  gMotors[3].run(rightMotorsDirection);
 
  return 0; 
}



int16_t ledsHandler(const uint8_t* requestBuffer, 
                uint16_t size, 
                uint8_t* response, 
                uint16_t responseSize){
  if( 0 != requestBuffer[0] ){
    digitalWrite(ledsPin, HIGH);
  }
  else{
    digitalWrite(ledsPin, LOW);
  }
  
  return 0;
}                             

Cmd gCommands[] = 
{
  {int8_t(1), moveHandler},
  {int8_t(2), stopHandler},
  {int8_t(3), servoHandler},
  {int8_t(4), distanceMeasureHandler},
  {int8_t(5), accelMeasureHandler},
  {int8_t(6), compasMeasureHandler},
  {int8_t(7), motorControllHandler},
  {int8_t(8), directionVectorHandler},
  {int8_t(9), ledsHandler},
};

const unsigned gCommands_size = sizeof(gCommands) / sizeof(Cmd);



void setup() {
    Wire.begin();
    accelMeter.begin();
    Serial.begin(9600);
    Serial2.begin(19200);
    Serial3.begin(9600);
    
    Serial3.print( "AT+BAUD8");
    delay(1000);
    Serial3.end();
    Serial3.begin(115200, SERIAL_8N1);
    
    compas.initialize();
    servo.attach(9);    
           
    leftHeadMotor.setSpeed(200);
    leftTailMotor.setSpeed(200);
    rightHeadMotor.setSpeed(200);
    rightTailMotor.setSpeed(200);
    
    leftHeadMotor.run(RELEASE);
    leftTailMotor.run(RELEASE);
    rightHeadMotor.run(RELEASE);
    rightTailMotor.run(RELEASE);
    
    initSensors( ultrasonicSensors, sizeof(ultrasonicSensors) / sizeof(ultrasonicSensors[0]));
    
    pinMode(ledsPin, OUTPUT);
    digitalWrite(ledsPin, LOW);
 }
  
 void loop() {             
         servo.write(90);
         
         Connection connection_bt((Serial3));
         Connection connection_rfid((Serial2));
         uint8_t receiveBuffer[256];
         uint8_t sendBuffer[256];
         
         Protocol protocol_bt(connection_bt,
                           receiveBuffer,
                           sizeof(receiveBuffer),
                           sendBuffer,
                           sizeof(sendBuffer),
                           gCommands,
                           gCommands_size);
                           
         Protocol protocol_rfid(connection_rfid,
                           receiveBuffer,
                           sizeof(receiveBuffer),
                           sendBuffer,
                           sizeof(sendBuffer),
                           gCommands,
                           gCommands_size);                           
         for( ;; ){
           protocol_bt.run();
           protocol_rfid.run();   
         }           
  }
