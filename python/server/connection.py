import struct

__author__ = 'okn'


class Connection(object):
    def __init__(self, port):
        self._port = port

    def send(self, data):
        self._port.write("b00b" + struct.pack("B", len(data)) + data)

    def receive(self):
        self.__wait_for_preamble()
        size = int(struct.unpack("B", self._port.read())[0])
        if 0 == size:
            return ""
        return self._port.read(size)

    def __wait_for_preamble(self):
        buf = ""
        while not buf.endswith("b00b"):
            buf += self._port.read()