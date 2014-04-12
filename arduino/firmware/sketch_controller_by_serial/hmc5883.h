#if !defined( _HMC5883_H_ )
# define _HMC5883_H_

#include "Arduino.h"

class HMC5883 {
  public:
    enum{ ADDRESS = 0x1E };
  
    void initialize()
    {
      Wire.beginTransmission(ADDRESS); //open communication with HMC5883
      Wire.write(0x02); //select mode register
      Wire.write(0x00); //continuous measurement mode
      Wire.endTransmission();      
    }
    
    void read( int* x, int* y, int* z ){
      Wire.beginTransmission(ADDRESS);
      Wire.write(0x02); //select mode register
      Wire.write(0x00); //continuous measurement mode
      Wire.endTransmission();
      
      Wire.requestFrom(ADDRESS, 6);
      if(6<=Wire.available()){
        *x = Wire.read()<<8; //X msb
        *x |= Wire.read(); //X lsb
        *z = Wire.read()<<8; //Z msb
        *z |= Wire.read(); //Z lsb
        *y = Wire.read()<<8; //Y msb
        *y |= Wire.read(); //Y lsb
      }
    }
};

#endif
