namespace java moonbuggy 
namespace py moonbuggy

enum Direction
{
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT
}

exception InvalidRequest
{
    1: string why
    2: optional string stack
}

exception ServerError
{
    1: string why
    2: optional string stack
}

struct Point3d{
    1: i32 x
    2: i32 y
    3: i32 z
}

service MoonBuggyService 
{        
    void move( 1: Direction direction,
               2: i16 speed) throws ( 1: InvalidRequest ire,
                                      2: ServerError se )

    void stop()  throws ( 1: InvalidRequest ire,
                          2: ServerError se )

    void setCameraAngle( 1: i16 angle ) throws ( 1: InvalidRequest ire,
                                                 2: ServerError se )

    i16 measureDistance( 1: i16 sensorNumber ) throws ( 1: InvalidRequest ire,
                                                        2: ServerError se )

    void startStreamVideo( 1: string endPoint )  throws ( 1: InvalidRequest ire,
                                                          2: ServerError se )

    void stopStreamVideo() throws ( 1: InvalidRequest ire,
                                    2: ServerError se )

    Point3d measureAcceleration() throws ( 1: InvalidRequest ire,
                                           2: ServerError se )

    i16 measureCompass() throws ( 1: InvalidRequest ire,
                                  2: ServerError se )

    void setMotorParams( 1: Direction direction,
                         2: i16 speed,
                         3: i16 motor ) throws ( 1: InvalidRequest ire,
                                                 2: ServerError se )

    void setDirectionVector( 1: i16 x
                             2: i16 y ) throws ( 1: InvalidRequest ire,
                                                 2: ServerError se )

    void setLedsState( 1: i16 state ) throws ( 1: InvalidRequest ire,
                                                 2: ServerError se )
}
