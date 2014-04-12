import threading
import traceback
import sys
from moonbuggy.MoonBuggyService import Iface
from moonbuggy.ttypes import ServerError

__author__ = 'okn'


class MoonBuggyServiceImpl(Iface):
    def __init__(self, sensors, movements):
        self.__sensors = sensors
        self.__movements = movements

    def move(self, direction, speed):
        try:
            self.__movements.move(direction, speed)
        except Exception as e:
            self.__movements.stop()
            raise ServerError(e.message, self.format_exception(e))

    def stop(self, ):
        try:
            self.__movements.stop()
        except Exception as e:
            raise ServerError(e.message, self.format_exception(e))

    def setCameraAngle(self, angle):
        try:
            return self.__movements.setCameraAngle(angle)
        except Exception as e:
            raise ServerError(e.message, self.format_exception(e))

    def measureDistance(self, sensorNumber):
        try:
            return self.__sensors.measureDistance(sensorNumber)
        except Exception as e:
            raise ServerError(e.message, self.format_exception(e))

    def measureAcceleration(self, ):
        try:
            return self.__sensors.measureAcceleration()
        except Exception as e:
            raise ServerError(e.message, self.format_exception(e))

    def measureCompass(self, ):
        try:
            return self.__sensors.measureCompass()
        except Exception as e:
            raise ServerError(e.message, self.format_exception(e))

    def setMotorParams(self, direction, speed, motor):
        try:
            self.__movements.setMotorParams(direction, speed, motor)
        except Exception as e:
            raise ServerError(e.message, self.format_exception(e))

    def setDirectionVector(self, x, y):
        try:
           self.__movements.setDirectionVector(x, y)
        except Exception as e:
            raise ServerError(e.message, self.format_exception(e))

    def setLedsState(self, state):
        try:
            return self.__sensors.setLedsState(state)
        except Exception as e:
            raise ServerError(e.message, self.format_exception(e))

    @staticmethod
    def format_exception(e):
        exception_list = traceback.format_stack()
        exception_list = exception_list[:-2]
        exception_list.extend(traceback.format_tb(sys.exc_info()[2]))
        exception_list.extend(traceback.format_exception_only(sys.exc_info()[0], sys.exc_info()[1]))

        exception_str = "Traceback (most recent call last):\n"
        exception_str += "".join(exception_list)
        # Removing the last \n
        exception_str = exception_str[:-1]

        return exception_str