/*******************************************************************************
 Vertices.h enthält alle Structs für die gebräuchlichsten Vertextypen;
    kann erweitert werden, allerdings müssen alle Datenstrukturen synchron
    erweitert werden
*******************************************************************************/


#pragma once

#ifndef VERTICES_H_INCLUDED
#define VERTICES_H_INCLUDED

#include "e42/stdinc.h"

#include <D3D9.h>
#include "baselib/geometry/CVector.h"

//-------------------------------------------------------------------------------------------------------------------------------------------

enum VertexType
{
	// 1,2,3,4	= Anzahl der Dimensionen (X,Y,Z,RHW)
	// D		= Diffuse Color
	// S		= Specular Color
	// Tn		= Texturkoordinaten n = Dimension der Koordinaten (2 = UV; 3 = UVW)
    // tn       = Tangent n = Dimension der Tangente
    // Bn       = Blendweights n = Anzahl der Weights


	VT_4,
	VT_4T2,
	VT_4T2T2,
	VT_4NT2,
	VT_4D,
	VT_4DS,
	VT_4DST2,

	VT_3,
	VT_3N,
	VT_3T2,
	VT_3T2B3,
	VT_3T2T2,
	VT_3NT2,
	VT_3NT2T2,
	VT_3NDT2T2,
	VT_3D,
	VT_3DS,
    VT_3DSB3,
	VT_3DST2,
	VT_3DT2B2,

	VT_3ND,
	VT_3NDT2,

    VT_3B1N,
	VT_B2,

    VT_3B1t1t1,

    VT_COUNT,

    VT_UNDEFINED
};

//-------------------------------------------------------------------------------------------------------------------------------------------
enum VertexFVF
{
	D3DFVF_VT4 =		D3DFVF_XYZRHW,
	D3DFVF_VT4T2 =	    D3DFVF_XYZRHW	                                                        | D3DFVF_TEX1               | D3DFVF_TEXCOORDSIZE2(0),
	D3DFVF_VT4T2T2 =	D3DFVF_XYZRHW	                                                        | D3DFVF_TEX2               | D3DFVF_TEXCOORDSIZE2(0)   | D3DFVF_TEXCOORDSIZE2(1),
	D3DFVF_VT4NT2 =		D3DFVF_XYZRHW   | D3DFVF_NORMAL                                         | D3DFVF_TEX1               | D3DFVF_TEXCOORDSIZE2(0),
	D3DFVF_VT4D =		D3DFVF_XYZRHW	| D3DFVF_DIFFUSE,
	D3DFVF_VT4DS =		D3DFVF_XYZRHW	| D3DFVF_DIFFUSE	        | D3DFVF_SPECULAR,
	D3DFVF_VT4DST2 =	D3DFVF_XYZRHW	| D3DFVF_DIFFUSE	        | D3DFVF_SPECULAR	        | D3DFVF_TEX1               | D3DFVF_TEXCOORDSIZE2(0),

	D3DFVF_VT3 =		D3DFVF_XYZ,
	D3DFVF_VT3N =		D3DFVF_XYZ      | D3DFVF_NORMAL,
	D3DFVF_VT3T2 =		D3DFVF_XYZ                                                              | D3DFVF_TEX1               | D3DFVF_TEXCOORDSIZE2(0),
	D3DFVF_VT3T2B3 =	D3DFVF_XYZ                                                              | D3DFVF_TEX1               | D3DFVF_TEXCOORDSIZE2(0)   | D3DFVF_XYZB3,
	D3DFVF_VT3T2T2 =	D3DFVF_XYZ                                                              | D3DFVF_TEX2               | D3DFVF_TEXCOORDSIZE2(0)   | D3DFVF_TEXCOORDSIZE2(1),
	D3DFVF_VT3NT2 =		D3DFVF_XYZ      | D3DFVF_NORMAL                                         | D3DFVF_TEX1               | D3DFVF_TEXCOORDSIZE2(0),
	D3DFVF_VT3NT2T2 =	D3DFVF_XYZ      | D3DFVF_NORMAL                                         | D3DFVF_TEX2               | D3DFVF_TEXCOORDSIZE2(0)	| D3DFVF_TEXCOORDSIZE2(1),
	D3DFVF_VT3NDT2T2 =	D3DFVF_XYZ      | D3DFVF_NORMAL				| D3DFVF_DIFFUSE            | D3DFVF_TEX2               | D3DFVF_TEXCOORDSIZE2(0)	| D3DFVF_TEXCOORDSIZE2(1),
	D3DFVF_VT3D =		D3DFVF_XYZ		| D3DFVF_DIFFUSE,
	D3DFVF_VT3DS =		D3DFVF_XYZ		| D3DFVF_DIFFUSE	        | D3DFVF_SPECULAR,
	D3DFVF_VT3DSB3 =	D3DFVF_XYZ		| D3DFVF_DIFFUSE	        | D3DFVF_SPECULAR           | D3DFVF_XYZB3,
	D3DFVF_VT3DST2 =	D3DFVF_XYZ		| D3DFVF_DIFFUSE	        | D3DFVF_SPECULAR	        | D3DFVF_TEX1               | D3DFVF_TEXCOORDSIZE2(0),
	D3DFVF_VT3DT2B2 =	D3DFVF_XYZ		| D3DFVF_DIFFUSE	        | D3DFVF_TEX1               | D3DFVF_TEXCOORDSIZE2(0)   | D3DFVF_XYZB2,

	D3DFVF_VT3ND =	    D3DFVF_XYZ		| D3DFVF_NORMAL		        | D3DFVF_DIFFUSE,
	D3DFVF_VT3NDT2 =	D3DFVF_XYZ		| D3DFVF_NORMAL		        | D3DFVF_DIFFUSE	        | D3DFVF_TEX1               | D3DFVF_TEXCOORDSIZE2(0),

	D3DFVF_VT3B1N =		D3DFVF_XYZ		| D3DFVF_XYZB1		        | D3DFVF_NORMAL,
	D3DFVF_VTB2 =		D3DFVF_XYZB2,

    D3DFVF_VT3B1t1t1 =  0,      // NON_FVF-Format!
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex4
{
public:
    CVec4 p;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex4T2
{
public:
    CVec4 p;
    CVec2 t0;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex4T2T2
{
public:
    CVec4 p;
    CVec2 t0;
    CVec2 t1;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex4NT2
{
public:
    CVec4 p;
    CVec3 n;
    CVec2 t1;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex4D
{
public:
    CVec4 p;
    DWORD diffuse;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex4DS
{
public:
	CVec4 p;
    DWORD diffuse;
	DWORD specular;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex4DST2
{
public:
    CVec4 p;
    DWORD diffuse;
    DWORD specular;
    CVec2 t0;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3
{
public:
    CVec3 p;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3N
{
public:
    CVec3 p;
    CVec3 n;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3T2
{
public:
    CVec3 p;
    CVec2 t0;
};
//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3T2B3
{
public:
    CVec3 p;
    CVec2 t0;
    float f0;
    float f1;
    float f2;
};
//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3T2T2
{
public:
    CVec3 p;
    CVec2 t0;
    CVec2 t1;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3NT2
{
public:
    CVec3 p;
    CVec3 n;
    CVec2 t0;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3NT2T2
{
public:
    CVec3 p;
    CVec3 n;
    CVec2 t0;
    CVec2 t1;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3NDT2T2
{
public:
    CVec3 p;
    CVec3 n;
	DWORD diffuse;
    CVec2 t0;
    CVec2 t1;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3D
{
public:
    CVec3 p;
    DWORD diffuse;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3DS
{
public:
    CVec3 p;
    DWORD diffuse;
    DWORD specular;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3DSB3
{
public:
    CVec3 p;
    DWORD diffuse;
    DWORD specular;
    float fWeight0;
    float fWeight1;
    float fWeight3;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3DST2
{
public:
	CVec3 p;
    DWORD diffuse;
	DWORD specular;
	CVec2 t0;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3DT2B2
{
public:
	CVec3 p;
    DWORD diffuse;
	CVec2 t0;
    float fWeight0;
    float fWeight1;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3ND
{
public:
	CVec3 p;
	CVec3 n;
	DWORD diffuse;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3NDT2
{
public:
	CVec3 p;
	CVec3 n;
	DWORD diffuse;
	CVec2 t0;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3B1N
{
public:
	CVec3 p;
	float fWeight0;
	CVec3 n;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class VertexB2
{
public:
	float fWeight0;
	float fWeight1;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertex3B1t1t1
{
public:
    CVec3 p;
	float fWeight0;
    float fTangent0;
    float fTangent1;
};

//-------------------------------------------------------------------------------------------------------------------------------------------
class Vertices
{
public:
   static const D3DVERTEXELEMENT9   g_VEDTable[][8];        ///< Tabelle VertexTypes->VertexElementdefinitions
   static const VertexFVF           g_FVFTable[];           ///< Tabelle VertexTypes->VertexFVFe
   static const int                 g_VSizeTable[];         ///< Tabelle mit Größen

   static VertexType FVF2VT(unsigned int uiFVF);            ///< VertexType zu FVF finden
};
//-------------------------------------------------------------------------------------------------------------------------------------------
#include "baselib/array.h"

typedef CArray<Vertex3NT2>      Vertex3NT2Array;
typedef CArray<Vertex3NDT2>     Vertex3NDT2Array;
typedef CArray<CVec3>           CVec3Array;
typedef CArray<CVec2>           CVec2Array;
typedef CArray<CVec3, true>     CVec3ArrayM;
typedef CArray<CVec2, true>     CVec2ArrayM;
//-------------------------------------------------------------------------------------------------------------------------------------------


#endif // VERTICES_H_INCLUDED