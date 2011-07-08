#pragma once

#ifndef STDINC_H_INCLUDED
#define STDINC_H_INCLUDED

#define WIN32_LEAN_AND_MEAN		// Exclude rarely-used stuff from Windows headers
#define DIRECTINPUT_VERSION  0x0800

#ifdef _DEBUG

    #include <crtdbg.h>

    // die folgenden Header werden an dieser Stelle included, 
    // weil sie nicht mit dem new-Makro zusammenarbeiten (FIXME)
    #include <map>
    #include <string>
    #include <d3dx9math.h>

    #define new   new( _CLIENT_BLOCK, __FILE__, __LINE__)

#endif // _DEBUG

#include <assert.h>
#include "baselib/debugprint.h"

#endif // STDINC_H_INCLUDED
