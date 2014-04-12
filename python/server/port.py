__author__ = 'okn'

from serial import Serial, STOPBITS_TWO


class Port(object):
    def __init__(self, tty, baudrate=115200):
        self.__serial = Serial(port=tty,
                               baudrate=19200,
                               timeout=1,
                               writeTimeout=1)
        self.__serial._timeout = 1

    def write(self, buf):
        self.__serial.write(buf)

    def read(self, size=1):
        result = self.__serial.read(size)
        if result is None:
            raise RuntimeError("read timeout")

        return result