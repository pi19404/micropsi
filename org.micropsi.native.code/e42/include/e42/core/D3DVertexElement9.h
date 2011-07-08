#ifndef E42_D3DVERTEXELEMENT9_H_INCLUDED
#define E42_D3DVERTEXELEMENT9_H_INCLUDED

#include "e42/stdinc.h"
#include <d3d9.h>
#include <d3d9types.h>

class CD3DVertexElement9 : public D3DVERTEXELEMENT9
{
public:
    CD3DVertexElement9();
    CD3DVertexElement9(const D3DVERTEXELEMENT9& ve);
    CD3DVertexElement9(WORD Stream, WORD Offset, BYTE Type, BYTE Method, BYTE Usage, BYTE UsageIndex);
    ~CD3DVertexElement9();

    CD3DVertexElement9& operator=(const CD3DVertexElement9& ve);
    bool operator==(const CD3DVertexElement9& ve) const;
    bool operator!=(const CD3DVertexElement9& ve) const;

    operator D3DVERTEXELEMENT9();


	static size_t GetSizeOfType(D3DDECLTYPE declType);
    static const D3DVERTEXELEMENT9 D3DDECL_END;
};

#endif // E42_D3DVERTEXELEMENT9_H_INCLUDED