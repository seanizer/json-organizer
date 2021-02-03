# Take Home Assignment

Take a variable number of identically structured json records and de-duplicate the set.

An example file of records is given in the accompanying 'leads.json'. Output should be same format,
with dups reconciled according to the following rules:

1. The data from the newest date should be preferred

2. duplicate IDs count as dupes. Duplicate emails count as dups. Both must be unique in our dataset.
   Duplicate values elsewhere do not count as dups.

3. If the dates are identical the data from the record provided last in the list should be preferred

Simplifying assumption: the program can do everything in memory (don't worry about large files)

The application should also provide a log of changes including some representation of the source
record, the output record and the individual field changes (value from and value to) for each field.

Please implement as a command line Java program.

# Implementation Notes

I'm having a hard time understanding the requirement about logging changes. The way I read this,
records are never changed, just deduplicated. So instead of logging, I chose to output two separate
files, one in the original format, containing only non-duplicate records, and a separate file
containing duplicate records, along with the reason they are considered duplicates.

To run this application, you need a JDK installed, in version 1.8 or higher.

    ./json-organizer.sh 

Running this script will check whether the application jar is already present. If not, it will run a
Maven build of the application (downloading Maven first, if necessary).

The application runs with default parameters, but if you want to see the supported parameters, call

    ./json-organizer.sh --help
    
Note: this application requires a bash shell to run.
On Windows, you will probably need to install WSL to get it to run.
