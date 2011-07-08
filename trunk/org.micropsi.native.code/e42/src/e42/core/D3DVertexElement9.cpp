#include "stdafx.h"

#include "e42/core/D3DVertexElement9.h"

const D3DVERTEXELEMENT9 CD3DVertexElement9::D3DDECL_END = {0xFF, 0, D3DDECLTYPE_UNUSED, 0, 0, 0};
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexElement9::CD3DVertexElement9()
{
	assert(sizeof(CD3DVertexElement9) == sizeof(D3DVERTEXELEMENT9));
    Offset = -1;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexElement9::CD3DVertexElement9(const D3DVERTEXELEMENT9& ve)
{
    Stream = ve.Stream;
    Offset = ve.Offset;
    Type = ve.Type;
    Method = ve.Method;
    Usage = ve.Usage;
    UsageIndex = ve.UsageIndex;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexElement9::CD3DVertexElement9(WORD Stream, WORD Offset, BYTE Type, BYTE Method, BYTE Usage, BYTE UsageIndex)
{
    this->Stream = Stream;
    this->Offset = Offset;
    this->Type = Type;
    this->Method = Method;
    this->Usage = Usage;
    this->UsageIndex = UsageIndex;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexElement9::~CD3DVertexElement9()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexElement9& 
CD3DVertexElement9::operator=(const CD3DVertexElement9& ve)
{
    Stream = ve.Stream;
    Offset = ve.Offset;
    Type = ve.Type;
    Method = ve.Method;
    Usage = ve.Usage;
    UsageIndex = ve.UsageIndex;

    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool 
CD3DVertexElement9::operator==(const CD3DVertexElement9& ve) const
{
    return 
        Stream == ve.Stream &&
        Offset == ve.Offset &&
        Type == ve.Type &&
        Method == ve.Method &&
        Usage == ve.Usage &&
        UsageIndex == ve.UsageIndex;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool 
CD3DVertexElement9::operator!=(const CD3DVertexElement9& ve) const
{
    return !operator==(ve);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexElement9::operator D3DVERTEXELEMENT9()
{
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
size_t
CD3DVertexElement9::GetSizeOfType(D3DDECLTYPE declType)
{
	switch (declType)
	{
    case D3DDECLTYPE_FLOAT1 :	
		return 4;

	case D3DDECLTYPE_FLOAT2 :	
		return 8;

	case D3DDECLTYPE_FLOAT3 :	
		return 12;

	case D3DDECLTYPE_FLOAT4 :	
		return 16;

    case D3DDECLTYPE_D3DCOLOR :	
		return 4;

    case D3DDECLTYPE_UBYTE4 :
    case D3DDECLTYPE_UBYTE4N :	
		return 4;

    case D3DDECLTYPE_SHORT2 :
    case D3DDECLTYPE_SHORT2N :
    case D3DDECLTYPE_USHORT2N :
		return 4;

    case D3DDECLTYPE_SHORT4 :
    case D3DDECLTYPE_SHORT4N :
    case D3DDECLTYPE_USHORT4N :
		return 8;

    case D3DDECLTYPE_UDEC3 :
    case D3DDECLTYPE_DEC3N :
		return 4;

    case D3DDECLTYPE_FLOAT16_2 :
		return 4;

    case D3DDECLTYPE_FLOAT16_4 :
		return 8;

    case D3DDECLTYPE_UNUSED :	
	default:
		assert(false);
		return 0;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
