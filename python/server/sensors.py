import math
from server.packet_factory import DistanceMeasurePacket, AccelerationMeasurePacket, CompassMeasurePacket, SetLedsState

__author__ = 'okn'


class Sensors(object):
    def __init__(self, protocol):
        self._protocol = protocol

    def measureDistance(self, sensorNumber):
        request = DistanceMeasurePacket.create(sensorNumber)
        response = self._protocol.communicate(request)
        return response.distance

    def measureAcceleration(self, ):
        request = AccelerationMeasurePacket.create()
        response = self._protocol.communicate(request)
        return response.point3d

    def measureCompass(self, ):
        request = CompassMeasurePacket.create()
        response = self._protocol.communicate(request)
        acceleration = response.point3d

        heading = math.atan2(acceleration.y, acceleration.x)
        if heading < 0:
            heading += 2*math.pi

        if heading > 2*math.pi:
            heading -= 2*math.pi

        heading = heading * 180 / math.pi

        return int(heading)

    def setLedsState(self, state):
        request = SetLedsState.create(state)
        self._protocol.communicate(request)