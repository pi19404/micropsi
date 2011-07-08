#pragma once

#ifndef E42_STDINC_H_INCLUDED
#define E42_STDINC_H_INCLUDED

#define WIN32_LEAN_AND_MEAN		// Exclude rarely-used stuff from Windows headers

//-----------------------------------------------------------------------------------------------------------------------------------------
#ifdef _DEBUG

    #ifndef D3D_DEBUG_INFO
        #define  D3D_DEBUG_INFO
    #endif

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

//-----------------------------------------------------------------------------------------------------------------------------------------
//#include "e42/core/stdtypes.h"

typedef unsigned long   DWORD;
typedef unsigned short  WORD;
typedef unsigned char   BYTE;

typedef unsigned int    UINT;
typedef unsigned short  USHORT;

//typedef unsigned short  WCHAR;			// kollidiert mit w32-definition!!!!

typedef long LONG;
typedef int  INT;
typedef int  BOOL;

#ifndef D3DCOLOR_DEFINED
typedef DWORD D3DCOLOR;
#define D3DCOLOR_DEFINED
#endif

#ifndef DIRECTINPUT_VERSION
#define DIRECTINPUT_VERSION         0x0800
#endif

//-----------------------------------------------------------------------------------------------------------------------------------------
#ifndef NULL
#define NULL 0
#endif // NULL

//Delete an Array safely
#ifndef SAFE_DELETE_ARRAY
	#define SAFE_DELETE_ARRAY(p) { if(p) { delete[] (p); (p)=NULL; } }
#endif

//Delete an object pointer
#ifndef SAFE_DELETE
	#define SAFE_DELETE(p) { if(p) { delete (p); (p)=NULL; } }
#endif

//Release an object pointer
#ifndef SAFE_RELEASE
	#define SAFE_RELEASE(p) { if(p) { (p)->Release(); (p)=NULL; } }
#endif
//-----------------------------------------------------------------------------------------------------------------------------------------


#endif // STDINC_H_INCLUDED
