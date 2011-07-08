#ifndef E42_D3DDEVICECAPS_H_INCLUDED
#define E42_D3DDEVICECAPS_H_INCLUDED

#include <d3d9.h>

class CD3DDeviceCaps
{
public:
	CD3DDeviceCaps();
	~CD3DDeviceCaps();

	D3DCAPS9	m_xDeviceCaps;

	void		Read(IDirect3DDevice9* pd3dDevice);
	void		Read(IDirect3D9* pD3D, UINT uiAdapter, D3DDEVTYPE xDevType = D3DDEVTYPE_HAL);

	bool		Supports24BitZBuffer();
	int			GetMaxMultiSampleQuality(bool bFullscreen);
};

#endif // E42_D3DDEVICECAPS_H_INCLUDED