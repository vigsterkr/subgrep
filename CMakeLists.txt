project(subgrep)
cmake_minimum_required(VERSION 2.8)

FIND_PACKAGE(PythonInterp REQUIRED)
set(PythonLibs_FIND_VERSION ${PYTHON_VERSION_STRING})
FIND_PACKAGE(PythonLibs REQUIRED)
