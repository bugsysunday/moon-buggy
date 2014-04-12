#!/bin/sh

rm -rf build 

mkdir -p build/moon-buggy-interface-py
mkdir -p build/moon-buggy-interface-java

thrift --gen py   -out  build/moon-buggy-interface-py   moon-buggy-interface.thrift
thrift --gen java -out  build/moon-buggy-interface-java moon-buggy-interface.thrift
