CC=g++
CFLAGS=-Wall -g
LDFLAGS=
SOURCES=subgrep.c
OBJECTS=$(SOURCES:.c=.o)
EXECUTABLE=subgrep

all: $(SOURCES) $(EXECUTABLE)

$(EXECUTABLE): $(OBJECTS)
	$(CC) $(OBJECTS) -o $@ $(LDFLAGS)

.c.o:
	$(CC) $(CFLAGS) -c $<

clean:
	rm -rf $(OBJECTS) $(EXECUTABLE)