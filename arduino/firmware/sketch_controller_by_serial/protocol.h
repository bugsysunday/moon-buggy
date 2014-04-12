#if !defined(_PROTOCOL_H_)
#define _PROTOCOL_H_

#include "Arduino.h"
#include <string.h>

#include "connection.h"
#include "cmd.h"

class Protocol
{
  Connection& connection_;
  uint8_t* requestBuffer_;
  unsigned requestBufferSize_;
  Cmd* commands_;
  unsigned commandsNumber_;
  uint8_t* responseBuffer_;
  unsigned responseBufferSize_;

  public:
    Protocol(Connection& connection, 
             uint8_t* requestBuffer, 
             unsigned requestBufferSize,
             uint8_t* responseBuffer,
             unsigned responsBufferSize,
             Cmd* commads,
             unsigned commandsNumber)
     : connection_(connection),
       requestBuffer_(requestBuffer),
       requestBufferSize_(requestBufferSize),
       commands_(commads),
       commandsNumber_(commandsNumber),
       responseBuffer_(responseBuffer),
       responseBufferSize_(responsBufferSize) {}

    int run(){
      for(;;){
         memset(requestBuffer_, 0, requestBufferSize_);

         int bytesRead = connection_.read(requestBuffer_, requestBufferSize_);                
         if( bytesRead <= 0 ){
            return -1;
         }

         const char header = requestBuffer_[0];       
         for( unsigned i = 0; i < commandsNumber_; ++i ){           
           if( header == commands_[i].header){              
              int bytesResponse = commands_[i].handler( &requestBuffer_[1], 
                                                        bytesRead - 1, 
                                                        &responseBuffer_[1], 
                                                        requestBufferSize_ - 1);
              responseBuffer_[0] = header;              
              connection_.write(responseBuffer_, bytesResponse + 1);
           }
         }
       }
      
      return 0;       
    }
};
#endif
