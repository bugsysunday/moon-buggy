#if !defined( _CMD_H_ )
#define _CMD_H_

#include "Arduino.h"

typedef int16_t(*Handler)(const uint8_t* , uint16_t , uint8_t* , uint16_t );

struct Cmd
{
   int8_t header;
   Handler handler;
};

#endif

