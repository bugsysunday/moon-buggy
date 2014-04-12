package controller;

import moonbuggy.Direction;
import moonbuggy.MoonBuggyService;
import moonbuggy.Point3d;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import ui.DistanceMeasureService;

import java.net.SocketException;


public class MainFrameController implements DistanceMeasureService {
    private MoonBuggyService.Client moonBuggyService;
    private int speed = 0;


    public static interface LogListener{
        void appendLog(String text);
    }

    private LogListener logListener = new LogListener() {
        @Override
        public void appendLog(String text) {

        }
    };

    public void setLogListener( LogListener logListener){
        this.logListener = logListener;
    }

    public MainFrameController() {
        TTransport nullTransport = new TTransport() {
            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public void open() throws TTransportException {

            }

            @Override
            public void close() {

            }

            @Override
            public int read(byte[] bytes, int i, int i2) throws TTransportException {
                return 0;
            }

            @Override
            public void write(byte[] bytes, int i, int i2) throws TTransportException {

            }
        };
        this.moonBuggyService = new MoonBuggyService.Client(new TBinaryProtocol(nullTransport));
    }

    public void connect(String endpoint)  {
        try {
            String address = endpoint;
            int port = 9090;

            if( endpoint.contains(":")){
                String[] addressPort = endpoint.split(":");
                address = addressPort[0];
                port = Integer.valueOf(addressPort[1]);
            }

            TSocket transport = new TSocket(address, port);
            transport.setTimeout(8000);
            try {
                transport.getSocket().setTcpNoDelay(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            TProtocol proto = new TBinaryProtocol(transport);
            transport.open();
            moonBuggyService = new MoonBuggyService.Client(proto);
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
        }
    }

    public Point3d measureAcceleration() {
        try {
            return moonBuggyService.measureAcceleration();
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
        }

        return new Point3d(0,0,0);
    }

    public short measureCompass(){
        try{
            return moonBuggyService.measureCompass();
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
    }

    return 0;
    }

    public static class UltrasonicSensor{
        public static final int FRONT_RIGHT = 1;
        public static final int BACK = 3;
        public static final int FRONT_CENTER = 0;
        public static final int FRONT_LEFT = 2;
    }

    public void forward() {
        try {
            moonBuggyService.move(Direction.FORWARD, (short) speed);
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
        }
    }

    public void stop() {
        try {
            moonBuggyService.stop();
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
        }
    }

    public void backward() {
        try {
            moonBuggyService.move(Direction.BACKWARD, (short) speed);
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
        }
    }

    public void turnLeft() {
        try {
            moonBuggyService.move(Direction.LEFT, (short) speed);
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
        }
    }

    public void turnRight()  {
        try {
            moonBuggyService.move(Direction.RIGHT, (short) speed);
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
        }
    }

    public void setAngle(int value) {
        try {
            moonBuggyService.setCameraAngle((short) value);
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
        }
    }

    public void setSpeed(int percents) {
        double value = percents * 2.54;
        speed = (int)value;
    }

    public int measureDistance(int sensor){
       try{
           return moonBuggyService.measureDistance((short)sensor);
       }catch (TException e){
           e.printStackTrace();
           logListener.appendLog(e.getMessage());
       }

       return 0;
    }


    public void setMotorParams(int motor, Direction direction, int speedPercents ){
        try{
            short motorSpeedValue = (short)((float)speedPercents * 2.54);
            moonBuggyService.setMotorParams(direction, motorSpeedValue, (short) motor);
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
        }
    }

    public void setDirectionVector(int xPercents, int yPercents) {
        try{
            moonBuggyService.setDirectionVector((short) xPercents, (short) yPercents);
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
        }
    }

    public void turnOnLights(){
        try {
            moonBuggyService.setLedsState((short) 1);
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
        }
    }

    public void turnOfLights(){
        try {
            moonBuggyService.setLedsState((short) 0);
        }catch (TException e){
            e.printStackTrace();
            logListener.appendLog(e.getMessage());
        }
    }
}
