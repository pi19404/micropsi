#ifndef E42_D3DXSEMANTIC_H_INCLUDED
#define E42_D3DXSEMANTIC_H_INCLUDED

#include <d3dx9shader.h>

class CD3DXSemantic : public D3DXSEMANTIC
{
public:
    CD3DXSemantic();
    ~CD3DXSemantic();
    CD3DXSemantic(const D3DXSEMANTIC& s);
    CD3DXSemantic(D3DDECLUSAGE eUsage, DWORD dwUsageIndex);

    CD3DXSemantic& operator=(const D3DXSEMANTIC& s);
    bool operator==(const D3DXSEMANTIC& s) const;
    bool operator!=(const D3DXSEMANTIC& s) const;

    operator D3DXSEMANTIC();
};

#endif // E42_D3DXSEMANTIC_H_INCLUDED