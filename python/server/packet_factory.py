import struct
from moonbuggy.ttypes import Point3d

__author__ = 'okn'


class Packet(object):
    def __init__(self):
        self._header = None
        self._payload = None

    def set_header(self, header):
        self._header = header

    def set_payload(self, payload):
        self._payload = payload

    def to_dto(self):
        return struct.pack("B", self._header) + self._payload


class MovePacket(Packet):
    header = 0x1

    @staticmethod
    def create(direction, speed):
        packet = MovePacket()
        packet.set_header(MovePacket.header)
        packet.set_payload(struct.pack("B", direction) + struct.pack("B", speed))
        return packet

    @staticmethod
    def create_from_payload(payload):
        return MovePacket()


class StopPacket(Packet):
    header = 0x2

    @staticmethod
    def create():
        packet = StopPacket()
        packet.set_header(StopPacket.header)
        packet.set_payload("")
        return packet

    @staticmethod
    def create_from_payload(payload):
        return StopPacket()


class ServoAnglePacket(Packet):
    header = 0x3

    @staticmethod
    def create(angle):
        packet = ServoAnglePacket()
        packet.set_header(ServoAnglePacket.header)
        packet.set_payload(struct.pack("B", angle))
        return packet

    @staticmethod
    def create_from_payload(payload):
        return ServoAnglePacket()


class DistanceMeasurePacket(Packet):
    header = 0x4

    def __init__(self):
        Packet.__init__(self)
        self.distance = 0

    @staticmethod
    def create(sensor_number):
        packet = DistanceMeasurePacket()
        packet.set_header(DistanceMeasurePacket.header)
        packet.set_payload(struct.pack("B", sensor_number))
        return packet

    @staticmethod
    def create_from_payload(payload):
        distance = struct.unpack(">H", payload[0:2])[0]
        packet = DistanceMeasurePacket()
        packet.distance = distance
        return packet


def point3d_from_payload(payload):
    x = struct.unpack("<h", payload[0:2])[0]
    y = struct.unpack("<h", payload[2:4])[0]
    z = struct.unpack("<h", payload[4:6])[0]

    return Point3d(x, y, z)


class AccelerationMeasurePacket(Packet):
    header = 0x5

    def __init__(self):
        Packet.__init__(self)
        self.point3d = Point3d()

    @staticmethod
    def create():
        packet = AccelerationMeasurePacket()
        packet.set_header(AccelerationMeasurePacket.header)
        packet.set_payload("")
        return packet

    @staticmethod
    def create_from_payload(payload):
        point3d = point3d_from_payload(payload)
        packet = AccelerationMeasurePacket()
        packet.point3d = point3d
        return packet


class CompassMeasurePacket(Packet):
    header = 0x6

    def __init__(self):
        Packet.__init__(self)
        self.point3d = Point3d()

    @staticmethod
    def create():
        packet = CompassMeasurePacket()
        packet.set_header(CompassMeasurePacket.header)
        packet.set_payload("")
        return packet

    @staticmethod
    def create_from_payload(payload):
        point3d = point3d_from_payload(payload)
        packet = CompassMeasurePacket()
        packet.point3d = point3d
        return packet


class SetMotorParamsPacket(Packet):
    header = 0x7

    @staticmethod
    def create(direction, speed, motor):
        packet = SetMotorParamsPacket()
        packet.set_header(SetMotorParamsPacket.header)
        packet.set_payload(struct.pack("B", direction) + struct.pack("B", speed) + struct.pack("B", motor))
        return packet

    @staticmethod
    def create_from_payload(payload):
        return SetMotorParamsPacket()


class SetDirectionVector(Packet):
    header = 0x8

    @staticmethod
    def create(x, y):
        packet = SetDirectionVector()
        packet.set_header(SetDirectionVector.header)
        packet.set_payload(struct.pack("<h", x) + struct.pack("<h", y))
        return packet

    @staticmethod
    def create_from_payload(payload):
        return SetDirectionVector()


class SetLedsState(Packet):
    header = 0x9

    @staticmethod
    def create(state):
        packet = SetLedsState()
        packet.set_header(SetLedsState.header)
        packet.set_payload(struct.pack("B", state))
        return packet

    @staticmethod
    def create_from_payload(payload):
        return SetLedsState()




class PacketFactory(object):

    def __init__(self):
        self.clones = {MovePacket.header: MovePacket,
                       StopPacket.header: StopPacket,
                       ServoAnglePacket.header: ServoAnglePacket,
                       DistanceMeasurePacket.header: DistanceMeasurePacket,
                       AccelerationMeasurePacket.header: AccelerationMeasurePacket,
                       CompassMeasurePacket.header: CompassMeasurePacket,
                       SetMotorParamsPacket.header: SetMotorParamsPacket,
                       SetDirectionVector.header: SetDirectionVector,
                       SetLedsState.header: SetLedsState}

    def create(self, dto):
        header = struct.unpack("B", dto[0])[0]
        return self.clones[header].create_from_payload(dto[1:])