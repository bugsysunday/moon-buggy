#!/bin/bash
rm -rf build
mkdir build

zip -r build/moon-buggy-sevice server thrift moonbuggy __main__.py
