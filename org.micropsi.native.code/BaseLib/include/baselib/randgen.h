#pragma once

#ifndef RANDGEN_H_INCLUDED
#define RANDGEN_H_INCLUDED

// RANDROT-B-Algorithmus
class TRanrotBGenerator 
{
private:

    enum constants 
    {                                       // define parameters
        KK = 17, 
        JJ = 10, 
        R1 = 13, 
        R2 =  9
    };

protected:

    int p1, p2;                             // indexes into buffer
    unsigned long randbuffer[KK];                   // history buffer
    unsigned long randbufcopy[KK * 2];              // used for self-test

    enum TArch 
    {
        LITTLEENDIAN, 
        BIGENDIAN, 
        NONIEEE
    };
    TArch Architecture;                     // conversion to float depends on computer architecture

public:

    void RandomInit(unsigned long seed);            // initialization
    int IRandom(int min, int max);          // get integer random number in desired interval
    double Random();                        // get floating point random number
    TRanrotBGenerator(unsigned long seed);          // constructor
};

#endif // RANDGEN_H_INCLUDED
