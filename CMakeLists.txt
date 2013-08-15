project(subgrep)
cmake_minimum_required(VERSION 2.8)
SET(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/cmake")

FIND_PACKAGE(PythonInterp REQUIRED)
set(Python_ADDITIONAL_VERSIONS ${PYTHON_VERSION_MAJOR}.${PYTHON_VERSION_MINOR})
FIND_PACKAGE(PythonLibs)
FIND_PACKAGE(NumPy)
if (NUMPY_FOUND)
  message(found numpy)
else()
  message(couldn't find numpy)
endif()

FIND_PACKAGE(Jinja2)

