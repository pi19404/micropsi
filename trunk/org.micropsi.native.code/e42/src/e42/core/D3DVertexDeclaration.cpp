#include "stdafx.h"

#include "e42/core/D3DVertexDeclaration.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexDeclaration::CD3DVertexDeclaration()
{
	m_axVertexElements[0] = CD3DVertexElement9::D3DDECL_END;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexDeclaration::~CD3DVertexDeclaration()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexDeclaration::CD3DVertexDeclaration(DWORD dwFVF)
{
	HRESULT hr = D3DXDeclaratorFromFVF(dwFVF, m_axVertexElements);
	assert(SUCCEEDED(hr));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexDeclaration::CD3DVertexDeclaration(const CD3DVertexDeclaration& rxVD)
{
	operator=(rxVD);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexDeclaration::CD3DVertexDeclaration(const CD3DVertexElement9* pxVertexElements)
{
	operator=(pxVertexElements);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexDeclaration::CD3DVertexDeclaration(const D3DVERTEXELEMENT9* pxVertexElements)
{
	operator=(pxVertexElements);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexDeclaration&
CD3DVertexDeclaration::operator=(const CD3DVertexDeclaration& rxVD)
{
	return operator=(rxVD.m_axVertexElements);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexDeclaration& 
CD3DVertexDeclaration::operator=(const CD3DVertexElement9* pxVertexElements)
{
	for (int i = 0; i < MAX_FVF_DECL_SIZE; i++)
	{
		m_axVertexElements[i] = pxVertexElements[i];

		if (m_axVertexElements[i] == CD3DVertexElement9::D3DDECL_END)
		{
			break;
		}
	}

	return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexDeclaration& 
CD3DVertexDeclaration::operator=(const D3DVERTEXELEMENT9* pxVertexElements)
{
	return operator=((CD3DVertexElement9*)pxVertexElements);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool 
CD3DVertexDeclaration::operator==(const CD3DVertexDeclaration& rxVD) const
{
	for (int i = 0; i < MAX_FVF_DECL_SIZE; i++)
	{
		if (m_axVertexElements[i] != rxVD.m_axVertexElements[i])
		{
			return false;
		}

		if (m_axVertexElements[i] == CD3DVertexElement9::D3DDECL_END)
		{
			break;
		}
	}

	return true;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool 
CD3DVertexDeclaration::operator!=(const CD3DVertexDeclaration& rxVD) const
{
	return !operator==(rxVD);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int 
CD3DVertexDeclaration::CalcVertexSize(int iStream) const
{
	return D3DXGetDeclVertexSize(m_axVertexElements, iStream);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int 
CD3DVertexDeclaration::CalcNumElements() const
{
	return D3DXGetDeclLength(m_axVertexElements);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int 
CD3DVertexDeclaration::FindElementIdx(D3DDECLUSAGE eUsage, int iUsageIdx) const
{
	int iElementIdx = 0;

	while (m_axVertexElements[iElementIdx] != CD3DVertexElement9::D3DDECL_END)
	{
		if (m_axVertexElements[iElementIdx].Usage == eUsage &&
			m_axVertexElements[iElementIdx].UsageIndex == iUsageIdx)
		{
			return iElementIdx;
		}

		iElementIdx++;
	}

	return -1;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DVertexElement9*
CD3DVertexDeclaration::FindElement(D3DDECLUSAGE eUsage, int iUsageIdx)
{
	int iElementIdx = FindElementIdx(eUsage, iUsageIdx);

	if (iElementIdx == -1)
	{
		return NULL;
	}
	else
	{
		return m_axVertexElements + iElementIdx;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const CD3DVertexElement9*	
CD3DVertexDeclaration::FindElement(D3DDECLUSAGE eUsage, int iUsageIdx) const
{
	int iElementIdx = FindElementIdx(eUsage, iUsageIdx);

	if (iElementIdx == -1)
	{
		return NULL;
	}
	else
	{
		return m_axVertexElements + iElementIdx;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int
CD3DVertexDeclaration::AddVertexElement(const CD3DVertexElement9& rxVertexElement)
{
	int iCurrentEnd = CalcNumElements();
	int iVertexSize = CalcVertexSize(rxVertexElement.Stream);

	assert(m_axVertexElements[iCurrentEnd] == CD3DVertexElement9::D3DDECL_END);
	assert((rxVertexElement.Offset == -1 || 
			rxVertexElement.Offset == iVertexSize) && "error: invalid vertex element offset");

	m_axVertexElements[iCurrentEnd] = rxVertexElement;
	m_axVertexElements[iCurrentEnd].Offset = iVertexSize;

	iCurrentEnd++;

	assert(iCurrentEnd < MAX_FVF_DECL_SIZE);

	m_axVertexElements[iCurrentEnd] = CD3DVertexElement9::D3DDECL_END;

	return iCurrentEnd - 1;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
