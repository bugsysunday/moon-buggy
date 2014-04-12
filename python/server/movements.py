
from server.packet_factory import MovePacket, StopPacket, ServoAnglePacket, SetMotorParamsPacket, SetDirectionVector

__author__ = 'okn'


class Movements(object):
    def __init__(self, protocol, sensors):
        self._protocol = protocol
        self._sensors = sensors

    def move(self, direction, speed):
        request = MovePacket.create(direction, speed)
        self._protocol.communicate(request)

    def stop(self, ):
        request = StopPacket.create()
        self._protocol.communicate(request)

    def setCameraAngle(self, angle):
        request = ServoAnglePacket.create(angle)
        self._protocol.communicate(request)

    def setMotorParams(self, direction, speed, motor):
        request = SetMotorParamsPacket.create(direction, speed, motor)
        self._protocol.communicate(request)

    def setDirectionVector(self, x, y):
        request = SetDirectionVector.create(x, y)
        self._protocol.communicate(request)
