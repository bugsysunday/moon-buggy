
from serial import SerialException
import socket
from moonbuggy import MoonBuggyService
from server.connection import Connection
from server.movements import Movements
from server.packet_factory import PacketFactory
from server.port import Port
from server.protocol import Protocol
from server.moon_buggy_service_impl import MoonBuggyServiceImpl
from server.sensors import Sensors
from thrift.server import TServer

from thrift.server.THttpServer import THttpServer
from thrift.server.TServer import TSimpleServer
from thrift.transport import TSocket, TTransport

__author__ = 'okn'

# Thrift facility
from thrift.protocol import TBinaryProtocol


def start_server():
    #port = Port('/dev/rfcomm0')
    port = Port(tty='/dev/ttyUSB0', baudrate=19200)
    connection = Connection(port)
    packet_factory = PacketFactory()
    protocol = Protocol(connection, packet_factory)
    sensors = Sensors(protocol)
    movements = Movements(protocol=protocol, sensors=sensors)
    # Set the main facility to operate with our Handlers
    processor = MoonBuggyService.Processor(MoonBuggyServiceImpl(sensors=sensors, movements=movements))
    #pfactory = TBinaryProtocol.TBinaryProtocolFactory()
    #server = THttpServer(processor, ("0.0.0.0", 80), pfactory)
    transport = TSocket.TServerSocket(host="0.0.0.0", port=9090)

    tfactory = TTransport.TBufferedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()
    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

    try:
        # Launch a Server
        print 'Starting the server...'
        server.serve()

    except (KeyboardInterrupt, SystemExit) as e:
        print "Quitting from keyboard interrupt"
        raise e

    except SerialException as e:
        raise e

    except Exception, e:
        print e


def main():
    while True:
        start_server()


if __name__ == "__main__":
    main()
