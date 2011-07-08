#ifndef E42_MACRO_H_INCLUDED
#define E42_MACRO_H_INCLUDED

#include <d3dx9shader.h>

class CD3DXMacro : private D3DXMACRO
{
public:
    CD3DXMacro();
    CD3DXMacro(const D3DXMACRO& rxD3DMacro);
    CD3DXMacro(const CD3DXMacro& rxD3DMacro);
    ~CD3DXMacro();
    CD3DXMacro& operator=(const D3DXMACRO& rxD3DMacro);
    CD3DXMacro& operator=(const CD3DXMacro& rxD3DMacro);

	void Clear();

	void SetName(const char* pcName);
	const char* GetName() const;

	void SetDefinition(const char* pcDefinition);
	const char* GetDefinition() const;


	D3DXMACRO& D3DXMacro();
    const D3DXMACRO& D3DXMacro() const;
};

#endif // E42_MACRO_H_INCLUDED