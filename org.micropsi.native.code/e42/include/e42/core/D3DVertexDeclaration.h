#ifndef E42_D3DVERTEXDECLARATION_H_INCLUDED
#define E42_D3DVERTEXDECLARATION_H_INCLUDED

#include <d3dx9mesh.h>
#include "e42/core/D3DVertexElement9.h"

class CD3DVertexDeclaration
{
public:
	CD3DVertexDeclaration();
	CD3DVertexDeclaration(DWORD dwFVF);
	CD3DVertexDeclaration(const CD3DVertexDeclaration& rxVD);
	CD3DVertexDeclaration(const CD3DVertexElement9* pxVertexElements);
	CD3DVertexDeclaration(const D3DVERTEXELEMENT9* pxVertexElements);
	~CD3DVertexDeclaration();

	CD3DVertexDeclaration& operator=(const CD3DVertexDeclaration& rxVD);
	CD3DVertexDeclaration& operator=(const CD3DVertexElement9* pxVertexElements);
	CD3DVertexDeclaration& operator=(const D3DVERTEXELEMENT9* pxVertexElements);

	bool operator==(const CD3DVertexDeclaration& rxVD) const;
	bool operator!=(const CD3DVertexDeclaration& rxVD) const;


	CD3DVertexElement9	m_axVertexElements[MAX_FVF_DECL_SIZE];		// 65*8=520 Byte

	int CalcNumElements() const;
	int CalcVertexSize(int iStream = 0) const;

	int FindElementIdx(D3DDECLUSAGE eUsage, int iUsageIdx = 0) const;
	CD3DVertexElement9*	FindElement(D3DDECLUSAGE eUsage, int iUsageIdx = 0);
	const CD3DVertexElement9* FindElement(D3DDECLUSAGE eUsage, int iUsageIdx = 0) const;

	int AddVertexElement(const CD3DVertexElement9& rxVertexElement = CD3DVertexElement9());
};

#endif //E42_D3DVERTEXDECLARATION_H_INCLUDED