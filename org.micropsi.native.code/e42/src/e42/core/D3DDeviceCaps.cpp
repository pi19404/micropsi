#include "stdafx.h"

#include "e42/core/D3DDeviceCaps.h"

//-------------------------------------------------------------------------------------------------------------------------------------------
CD3DDeviceCaps::CD3DDeviceCaps()
{
	ZeroMemory(&m_xDeviceCaps, sizeof(m_xDeviceCaps));
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CD3DDeviceCaps::~CD3DDeviceCaps()
{
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CD3DDeviceCaps::Read(IDirect3DDevice9* pd3dDevice)
{
	HRESULT hr = pd3dDevice->GetDeviceCaps(&m_xDeviceCaps);
	assert(SUCCEEDED(hr));
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CD3DDeviceCaps::Read(IDirect3D9* pD3D, UINT uiAdapter, D3DDEVTYPE xDevType)
{
	HRESULT hr = pD3D->GetDeviceCaps(uiAdapter, xDevType, &m_xDeviceCaps);
	assert(SUCCEEDED(hr));
}
//-------------------------------------------------------------------------------------------------------------------------------------------
bool
CD3DDeviceCaps::Supports24BitZBuffer()
{
	assert(false);	// TODO
	return true;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
int
CD3DDeviceCaps::GetMaxMultiSampleQuality(bool bFullscreen)
{
	assert(false);	// TODO
	return 0;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
