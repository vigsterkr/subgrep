#include <iostream>
#include <set>
#include <string>
#include <fstream>

#include <getopt.h>

typedef std::set<std::string> KeyWords;

static void
usage () {
  std::cout << "Not enough arguments!" << std::endl
            << ""
            << std::endl;
}

static void
parseKeywords (const char* file, KeyWords& kw) {
  std::fstream fin;
  fin.open (file, std::ios_base::in);
  std::string line;
  
  if (!fin.is_open ()) {
    std::cout << "Could not open " << file << " keywords file." << std::endl;
    exit (1);
  }
  
  while (fin.good ()) {
    getline (fin, line);
    kw.insert (line);
  }
  
  fin.close ();
}

int
main (int argc, char **argv) {
  KeyWords kw;
  int c;
  char *fname = NULL, *out = NULL;
  std::ostream* os = NULL;
  std::filebuf fb;
  
  if (argc < 3) {
    usage ();
    exit (1);
  } 
  
  while ((c = getopt (argc, argv, "k:o:")) != -1) {
    switch (c) {
      case 'k':
        fname = optarg;
        break;
      case 'o':
        out = optarg;
        break;
      case '?':
      default:
        usage();
      
    }
  }
  
  argc -= optind;
  argv += optind;
  
  if (!fname) {
    std::cout << "No keywords file has been given!" << std::endl;
    exit (1); 
  }
  
  if (out) {
    fb.open (out, std::ios::out);
    if (!fb.is_open ()) {
      std::cout << "Error opening output file: " << out << std::endl;
      exit (1);
    }
    os = new std::ostream (&fb);
  } else {
    os = new std::ostream (std::cout.rdbuf ());
  }
  
  if (!os->good ()) {
    std::cout << "Error creating output stream" << std::endl;
    exit (1);
  }
  
  parseKeywords (fname, kw);
  
  for (KeyWords::const_iterator it = kw.begin (); it != kw.end (); it++) {
    std::cout << *it << std::endl;
  }
  
  os->flush ();
  delete os;
  
  return 0;
}