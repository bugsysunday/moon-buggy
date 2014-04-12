#if !defined(__CONNECTION_H_)
#define __CONNECTION_H_

#include "Arduino.h"

class Connection{
public:    
    Connection(Stream& stream)
      : m_stream(stream)
    {}
    
    int read( uint8_t* buf, unsigned bufSize ){
      if( m_stream.available() ){       
        waitForPreamble();                   
             
        unsigned payoadLen = readPayloadLen();
        if( payoadLen > bufSize ){
          return -1;
        }      
  
        return readBytes( buf, bufSize, payoadLen );
      }else{
        return -1;
      }
    }

    void write( uint8_t* buf, unsigned bufSize ){
      m_stream.print("b00b");
      m_stream.write((uint8_t)bufSize);
      m_stream.write(buf, bufSize);
    }

private:
    Stream& m_stream;
    
    void waitForPreamble(){
       while( !m_stream.find("b00b"));
    }

    unsigned readPayloadLen(){
      char len = 0;
      m_stream.readBytes(&len, 1);      
      return len;      
    }

    int readBytes( uint8_t* dest, unsigned destSize, unsigned bytesToRead ){
        return m_stream.readBytes((char*)dest, bytesToRead);        
    }
};
#endif
