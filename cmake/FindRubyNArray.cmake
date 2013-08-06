FIND_PACKAGE(Ruby REQUIRED)

EXECUTE_PROCESS(COMMAND ${RUBY_EXECUTABLE} -r narray -e "print 'true'"
	OUTPUT_VARIABLE RUBY_HAS_NARRAY
	ERROR_QUIET)

# find vendor arch dir
EXECUTE_PROCESS(COMMAND ${RUBY_EXECUTABLE} -r rbconfig -e "print RbConfig::CONFIG['vendorarchdir']".
    OUTPUT_VARIABLE RUBY_VENDOR_ARCH_DIR
    ERROR_QUIET)

find_program(GEM_EXECUTABLE NAMES gem )

if (GEM_EXECUTABLE)
	EXECUTE_PROCESS(COMMAND ${GEM_EXECUTABLE} which narray
		OUTPUT_VARIABLE RUBY_NARRAY_PATH
		ERROR_QUIET)

	if(RUBY_NARRAY_PATH)
		STRING(REGEX REPLACE "(.*)/narray.*" "\\1" RUBY_NARRAY_PATH ${RUBY_NARRAY_PATH})

		FIND_PATH(RUBY_NARRAY_INCLUDE_DIR narray.h
			HINTS ${RUBY_NARRAY_PATH})

		FIND_FILE(RUBY_NARRAY_LIBRARY NAMES narray.bundle narray.so
			HINTS ${RUBY_NARRAY_PATH})
	endif()
endif()

if (NOT RUBY_NARRAY_INCLUDE_DIR)
	FIND_PATH(RUBY_NARRAY_INCLUDE_DIR narray.h
		HINTS ${RUBY_VENDOR_ARCH_DIR} ${RUBY_LIBRARY} ${RUBY_POSSIBLE_LIB_DIR})
endif()

if (NOT RUBY_NARRAY_LIBRARY)
	FIND_FILE(RUBY_NARRAY_LIBRARY NAMES narray.bundle narray.so
		HINTS ${RUBY_VENDOR_ARCH_DIR} ${RUBY_LIBRARY} ${RUBY_POSSIBLE_LIB_DIR})
endif()

INCLUDE (FindPackageHandleStandardArgs)
FIND_PACKAGE_HANDLE_STANDARD_ARGS(RubyNArray DEFAULT_MSG 
	RUBY_HAS_NARRAY RUBY_NARRAY_INCLUDE_DIR RUBY_NARRAY_LIBRARY)

MARK_AS_ADVANCED(RUBY_NARRAY_INCLUDE_DIR RUBY_NARRAY_LIBRARY)
