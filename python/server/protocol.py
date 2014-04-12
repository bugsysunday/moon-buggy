__author__ = 'okn'


class Protocol(object):
    def __init__(self, connection, packet_factory):
        self._connection = connection
        self._packet_factory = packet_factory

    def communicate(self, packet):
        self._connection.send(packet.to_dto())
        dto = self._connection.receive()
        return self._packet_factory.create(dto)