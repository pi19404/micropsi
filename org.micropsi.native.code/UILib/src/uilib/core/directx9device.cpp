// supeDXGContext.cpp

#include "stdafx.h"
#include <d3d9.h>
#include <d3dx9.h>
#include <math.h>

#include "uilib/core/directx9device.h"
#include "uilib/core/timer.h"
#include "baselib/debugprint.h"


namespace UILib
{


//---------------------------------------------------------------------------------------------------------------------
///	Default Konstruktor
CDirectX9Device::CDirectX9Device(IDirect3DDevice9* p_pxD3DDevice, 
								 int p_iWidth, int p_iHeight, 
								 int p_iScreenWidth, int p_iScreenHeight, 
								 int p_iTileWidth, int p_iTileHeight)
{
	m_iWidth	  = p_iWidth;
	m_iHeight	  = p_iHeight;
    
	m_iScreenWidth  = p_iScreenWidth  != 0 ? p_iScreenWidth  : m_iWidth;
	m_iScreenHeight = p_iScreenHeight != 0 ? p_iScreenHeight : m_iHeight;

	m_iHTileSize = p_iTileWidth;
	m_iVTileSize = p_iTileHeight;

	m_rctSize	  = CRct(0, 0, m_iWidth, m_iHeight);
	m_pxD3DDevice = p_pxD3DDevice;

    m_pxBlendVertexShader = NULL;
    m_pxVertexDeclaration = NULL;
    m_iGlobalInterfaceAlpha = 255;
    m_iBrightness = 255;
	m_fTranslationX = 0.0f;
	m_fTranslationY = 0.0f;
	m_fScaleX = 1.0f;
	m_fScaleY = 1.0f;
	m_fPivotX = m_iWidth / 2.0f;
	m_fPivotY = m_iHeight / 2.0f;
	m_fRotationAngle = 0.0f;

	m_pxInterpolatorX = 0;
	m_pxInterpolatorY = 0;
	m_pxInterpolatorSX = 0;
	m_pxInterpolatorSY = 0;
	m_pxInterpolatorAlpha = 0;
	m_pxInterpolatorBrightness = 0;
	m_pxInterpolatorRotationAngle = 0;

    InitDevice();
}


//---------------------------------------------------------------------------------------------------------------------
/// Default Destruktor
CDirectX9Device::~CDirectX9Device()
{
	if (m_pxInterpolatorX)			{ delete m_pxInterpolatorX; }
	if (m_pxInterpolatorY)			{ delete m_pxInterpolatorY; }
	if (m_pxInterpolatorSX)			{ delete m_pxInterpolatorSX; }
	if (m_pxInterpolatorSY)			{ delete m_pxInterpolatorSY; }
	if (m_pxInterpolatorBrightness)	{ delete m_pxInterpolatorBrightness; }
	if (m_pxInterpolatorAlpha)		{ delete m_pxInterpolatorAlpha; }
	if (m_pxInterpolatorRotationAngle) { delete m_pxInterpolatorRotationAngle; }

	ShutDevice();
}



//---------------------------------------------------------------------------------------------------------------------
/// Initialisiert das Device; erstellt Geometrie usw; wird vom Konstruktor gerufen
void CDirectX9Device::InitDevice()
{
	HRESULT hr;

	// Schritt 1: große Textur anlegen, in die gezeichnet wird. Zum Rendern wird diese Textur in viele kleine Texturen 
	// (Kacheln) kopiert; das passiert jeweils nur, wenn sich ein Teil der Textur geändert hat, dann werden die Kacheln
	// aktualisiert
		
	hr = D3DXCreateTexture(m_pxD3DDevice, m_iWidth, m_iHeight, 1, 0, D3DFMT_A8R8G8B8, D3DPOOL_SYSTEMMEM, &m_pxBackBuffer);
	assert(SUCCEEDED(hr));
	assert(m_pxBackBuffer);

	m_iHTiles = (m_iWidth - 1) / m_iHTileSize + 1;
	m_iVTiles = (m_iHeight - 1) / m_iVTileSize + 1;
	m_apxTiles.SetSize(m_iHTiles * m_iVTiles);

   	// Schritt 2: Vertex Buffer anlegen. Das Device benutzt einen großen Vertex Buffer

	hr = m_pxD3DDevice->CreateVertexBuffer(	sizeof(TVertexFormat) * m_iHTiles * m_iVTiles * 4, 
											D3DUSAGE_WRITEONLY, 
											D3DFVF_XYZRHW|D3DFVF_TEX1, 
								  			D3DPOOL_DEFAULT,
											&m_pxVertexBuffer,
											0);
	assert(SUCCEEDED(hr));

	TVertexFormat* pxVD;
	hr = m_pxVertexBuffer->Lock(0, 0, (void**) &pxVD, 0);
	assert(SUCCEEDED(hr));

	// Schritt 3: Index Buffer anlegen. Das Device benutzt einen großen Index Buffer

	hr = m_pxD3DDevice->CreateIndexBuffer(	m_iHTiles * m_iVTiles * 6 * 2, 
											D3DUSAGE_WRITEONLY, 
											D3DFMT_INDEX16, 
											D3DPOOL_DEFAULT, 
											&(m_pxIndexBuffer),
											0);
	assert(SUCCEEDED(hr));

	unsigned short* pxID; 
	m_pxIndexBuffer->Lock(0, 0, (void**) &pxID, 0);
	assert(SUCCEEDED(hr));
	
	//Schritt 4: Textur zum clearen erzeugen

	IDirect3DTexture9* pxClearTexture;
    hr = D3DXCreateTexture(m_pxD3DDevice, m_iHTileSize, m_iVTileSize, 1, 0, D3DFMT_A8R8G8B8, D3DPOOL_SYSTEMMEM, &pxClearTexture);
	assert(SUCCEEDED(hr));
	ClearMemTexture(pxClearTexture);

	IDirect3DSurface9* pClearSurface;
	hr = pxClearTexture->GetSurfaceLevel(0,&pClearSurface);
	assert(SUCCEEDED(hr));

	//Schritt 5: Quads erzeugen

    for (int y = 0; y < m_iVTiles; ++y)
    {
        for (int x = 0; x < m_iHTiles; ++x)
        {
            int iTileIdx = y * m_iHTiles + x;
            
            // 1.) Quad initialisieren
            TQuad& rxCurTile = m_apxTiles[iTileIdx];
            
            rxCurTile.m_iPosX = x * m_iHTileSize;
            rxCurTile.m_iPosY = y * m_iVTileSize;
            
            rxCurTile.m_iWidth = min(m_iHTileSize, m_iWidth - rxCurTile.m_iPosX);
            rxCurTile.m_iHeight = min(m_iVTileSize, m_iHeight - rxCurTile.m_iPosY);
            
            rxCurTile.m_rRect = CRct(
                rxCurTile.m_iPosX,
                rxCurTile.m_iPosY,
                rxCurTile.m_iPosX + rxCurTile.m_iWidth,
                rxCurTile.m_iPosY + rxCurTile.m_iHeight);
            
            rxCurTile.m_bDirty = true;
            rxCurTile.m_eState = TQuad::S_OPAQUE;     // [dma] warum nicht UNKNOWN?
            
            


            // 2.a) Textur anlegen
            hr = D3DXCreateTexture(
                m_pxD3DDevice, m_iHTileSize, m_iVTileSize, 
                1, 0, D3DFMT_A8R8G8B8, D3DPOOL_DEFAULT, 
                &rxCurTile.m_pxTexture);
            assert(SUCCEEDED(hr));
            
            
			// 2.b) Textur clearen

			IDirect3DSurface9* pTxtSurface;
			rxCurTile.m_pxTexture->GetSurfaceLevel(0, &pTxtSurface);
	
			RECT r;
			r.top    = 0;
			r.left   = 0;
			r.bottom = m_iHTileSize;
			r.right  = m_iVTileSize; 

			POINT p; p.x = p.y = 0;
			HRESULT hr = m_pxD3DDevice->UpdateSurface(pClearSurface, &r, pTxtSurface, &p);
			assert(SUCCEEDED(hr));

			pTxtSurface->Release();


            // 3.a) Vertexpositionen schreiben
            //        0---1
            //        | / |
            //        2---3
			float screeSpaceOffset = 0.45f;

            int iV = iTileIdx * 4;
            pxVD[iV + 0].m_fX = (float)rxCurTile.m_rRect.left - screeSpaceOffset;          pxVD[iV + 0].m_fY = (float)rxCurTile.m_rRect.top - screeSpaceOffset;
            pxVD[iV + 1].m_fX = (float)rxCurTile.m_rRect.right - screeSpaceOffset;         pxVD[iV + 1].m_fY = (float)rxCurTile.m_rRect.top - screeSpaceOffset;
            pxVD[iV + 2].m_fX = (float)rxCurTile.m_rRect.left - screeSpaceOffset;          pxVD[iV + 2].m_fY = (float)rxCurTile.m_rRect.bottom - screeSpaceOffset;
            pxVD[iV + 3].m_fX = (float)rxCurTile.m_rRect.right - screeSpaceOffset;         pxVD[iV + 3].m_fY = (float)rxCurTile.m_rRect.bottom - screeSpaceOffset;
            
            pxVD[iV + 0].m_fZ =
			pxVD[iV + 1].m_fZ = 
            pxVD[iV + 2].m_fZ =
			pxVD[iV + 3].m_fZ = 0.0f;

			pxVD[iV + 0].m_fRHW =
			pxVD[iV + 1].m_fRHW =
			pxVD[iV + 2].m_fRHW =
			pxVD[iV + 3].m_fRHW = 1.0f;

            
            
            // 3.b) Texturkoordinaten schreiben
            float fXUsage = (float)rxCurTile.m_iWidth / (float)m_iHTileSize;
            float fYUsage = (float)rxCurTile.m_iHeight / (float)m_iVTileSize;
            
            float fTextureOffsetU = 0.5f / (float)m_iHTileSize;
            float fTextureOffsetV = 0.5f / (float)m_iVTileSize;
            
            pxVD[iV + 0].m_UV[0].fU = fTextureOffsetU + 0;           pxVD[iV + 0].m_UV[0].fV = fTextureOffsetV + 0;
            pxVD[iV + 1].m_UV[0].fU = fTextureOffsetU + fXUsage;     pxVD[iV + 1].m_UV[0].fV = fTextureOffsetV + 0;		
            pxVD[iV + 2].m_UV[0].fU = fTextureOffsetU + 0;           pxVD[iV + 2].m_UV[0].fV = fTextureOffsetV + fYUsage;
            pxVD[iV + 3].m_UV[0].fU = fTextureOffsetU + fXUsage;     pxVD[iV + 3].m_UV[0].fV = fTextureOffsetV + fYUsage;
            
            
            // 4.) Indizes schreiben
            int iW = iTileIdx * 6;
            pxID[iW + 0] = iV + 0;      pxID[iW + 1] = iV + 1;      pxID[iW + 2] = iV + 2; 
            pxID[iW + 3] = iV + 2;      pxID[iW + 4] = iV + 1;      pxID[iW + 5] = iV + 3; 
            
            m_apxTiles[iTileIdx].m_iIndexBufferOffset = iW;
        }
    }

	// 6. Schritt: alles fertig, unlock

	pxClearTexture->Release();
	pClearSurface->Release();

	hr = m_pxIndexBuffer->Unlock();
	assert(SUCCEEDED(hr));

	hr = m_pxVertexBuffer->Unlock();
	assert(SUCCEEDED(hr));

	ClearMemTexture(m_pxBackBuffer);
}

//---------------------------------------------------------------------------------------------------------------------
bool						
CDirectX9Device::ClearMemTexture(IDirect3DTexture9* p_pxTexture)
{
	D3DLOCKED_RECT r;
	HRESULT hr = p_pxTexture->LockRect(0, &r, 0, 0);

    assert(SUCCEEDED(hr));
	if (!SUCCEEDED(hr))
	{
		return false;
	}

	int iPitch = r.Pitch / 4;
	unsigned long* pdwPixels = (unsigned long*) r.pBits;

	D3DSURFACE_DESC xSurfaceDesc; 
	p_pxTexture->GetLevelDesc(0, &xSurfaceDesc);

	memset(pdwPixels, 0, iPitch * xSurfaceDesc.Height * sizeof(int));

	hr = p_pxTexture->UnlockRect(0);
	assert(SUCCEEDED(hr));

	return true;
}

//---------------------------------------------------------------------------------------------------------------------
/// Schließt das Device; wird vom Destruktor gerufen
void 
CDirectX9Device::ShutDevice()
{
    if (m_pxBlendVertexShader)
    {
        m_pxBlendVertexShader->Release();
        m_pxVertexDeclaration->Release();
    }

	if (m_pxBackBuffer)
	{
		m_pxBackBuffer->Release();
		m_pxVertexBuffer->Release();
		m_pxIndexBuffer->Release();
	}

	for (unsigned long i = 0; i<m_apxTiles.Size(); ++i)
	{
		TQuad& rxTQuad=m_apxTiles[i];
		rxTQuad.m_pxTexture->Release();
		rxTQuad.m_pxTexture = 0;
	}
}



//---------------------------------------------------------------------------------------------------------------------
/**
	Bevor irgendwelche Zeichenoperationen ausgeführt werden können, muss
	BeginPaint() aufgerufen werden. Nachdem alle Zeichenoperationen durchgeführt 
	sind, muss EndPaint() aufgerufen werden.
*/
bool CDirectX9Device::BeginPaint()
{
	D3DLOCKED_RECT r;
	HRESULT hr = m_pxBackBuffer->LockRect(0, &r, 0, 0);

    assert(SUCCEEDED(hr));
	if (!SUCCEEDED(hr))
	{
		return false;
	}

	m_iPitch = r.Pitch / 4;
	m_pdwPixels = (unsigned long*) r.pBits;

	// cool debug code: mark invalid regions
	// if working correctly, no red areas should ever be visible
/*
	int i;
	m_xInvalidRegions.StartIterate(i);
	CRct xRect;
	while(m_xInvalidRegions.Iterate(i, xRect))
	{
		FillRect(xRect.left, xRect.top, xRect.right, xRect.bottom, CColor(255,0,0,255));
	}
*/
	return true; 
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Bevor irgendwelche Zeichenoperationen ausgeführt werden können, muss
	BeginPaint() aufgerufen werden. Nachdem alle Zeichenoperationen durchgeführt 
	sind, muss EndPaint() aufgerufen werden.
*/
void CDirectX9Device::EndPaint(bool p_bValidateAll)
{
//	// check all quads with unknown state...
//	for (unsigned int i = 0; i<m_apxTiles.Size(); ++i)
//	{
//		if (m_apxTiles[i].m_eState == TQuad::S_UNKNOWN)
//		{
//			if (m_xTransparentParts.Contains(m_apxTiles[i].m_rRect))
//			{
//				m_apxTiles[i].m_eState = TQuad::S_TRANSPARENT;
////				m_xTransparentParts.Sub(m_apxTiles[i].m_rRect);
////				m_xTransparentParts.Push(m_apxTiles[i].m_rRect);
//			}
//			else
//			{
//				m_apxTiles[i].m_eState = TQuad::S_OPAQUE;
//			}
//		}
//	}
//	m_xTransparentParts.Compact();


	//unsigned int j;
	//m_xTransparentParts.StartIterate(j);
	//CRct xRect;
	//while(m_xTransparentParts.Iterate(j, xRect))
	//{
	//	DrawLineNoDirty(xRect.left, xRect.top, xRect.right-1, xRect.top, CColor(255,0,0,255));
	//	DrawLineNoDirty(xRect.left, xRect.bottom-1, xRect.right-1, xRect.bottom-1, CColor(255,0,0,255));
	//	DrawLineNoDirty(xRect.left, xRect.top, xRect.left, xRect.bottom-1, CColor(255,0,0,255));
	//	DrawLineNoDirty(xRect.right-1, xRect.top, xRect.right-1, xRect.bottom-1, CColor(255,0,0,255));
	//}

	HRESULT hr = m_pxBackBuffer->UnlockRect(0);
	assert(SUCCEEDED(hr));

	m_pdwPixels = 0;
    
	if (p_bValidateAll)
	{
		m_xInvalidRegions.Clear();
	}
}




//---------------------------------------------------------------------------------------------------------------------
/// erklärt die gesamte Zeichenfläche des Devices für ungültig
void CDirectX9Device::Invalidate()
{	
	CRct rect;
	rect.top    = 0;
	rect.left   = 0;
	rect.bottom	= m_iHeight;
	rect.right  = m_iWidth;
	COutputDevice::Invalidate(rect);
}



//---------------------------------------------------------------------------------------------------------------------
/// liefert die Farbe eines einzelnen Pixels
CColor CDirectX9Device::GetPixel(int p_iX, int p_iY)
{
	assert(m_pdwPixels != 0);

	if (p_iX >= m_iWidth  ||  p_iY >= m_iHeight  ||  p_iX < 0  ||  p_iY < 0)
	{
		return CColor(0);
	}
	CColor xRetCol=CColor(m_pdwPixels[p_iX + p_iY * m_iPitch]);
	return xRetCol;
}


//---------------------------------------------------------------------------------------------------------------------
/// setzt einen einzelnen Pixel
void CDirectX9Device::SetPixel(int p_iX, int p_iY, const CColor& p_xColor)
{
	assert(m_pdwPixels != 0);

	if (p_iX >= m_iWidth  ||  p_iY >= m_iHeight  ||  p_iX < 0  ||  p_iY < 0) 
	{
		return;
	}
	m_pdwPixels[p_iY * m_iPitch + p_iX] = p_xColor.m_dwColor; 
	MarkDirty(p_iX, p_iY, p_xColor);
}


//---------------------------------------------------------------------------------------------------------------------
/// zeichnet eine Linie
void CDirectX9Device::DrawLine(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor)
{
	assert(m_pdwPixels != 0);

	if ((p_iX1 == p_iX2) && (p_iY1 == p_iY2))
	{
		SetPixel(p_iX1, p_iY1, p_xColor);
		return;
	}

	if (p_iX1 == p_iX2)
	{
		if (p_iX1 < 0  ||  p_iX1 >= m_iWidth) { return; }

		int iHeight = m_iHeight-1;
		p_iY1 = clamp(p_iY1,0,iHeight);
		p_iY2 = clamp(p_iY2,0,iHeight);

		if (p_iY1==p_iY2)
		{
			SetPixel(p_iX1, p_iY1, p_xColor);
			return;
		}
		if (p_iY1 > p_iY2){Swap(p_iY1,p_iY2);}

		MarkDirty(p_iX1, p_iY1, p_iX1+1, p_iY2+1, p_xColor);
		int iOffset = p_iY1 * m_iPitch + p_iX1;
		for (;p_iY1<=p_iY2;p_iY1++){m_pdwPixels[iOffset] = p_xColor.m_dwColor; iOffset+=m_iPitch;}
		return;
	}
	if (p_iY1 == p_iY2)
	{
		if (p_iY1 < 0  ||  p_iY1 >= m_iHeight) { return; }

		int iWidth = m_iWidth-1;
		p_iX1 = clamp(p_iX1,0,iWidth);
		p_iX2 = clamp(p_iX2,0,iWidth);

		if (p_iX1==p_iX2)
		{
			SetPixel(p_iX1, p_iY1, p_xColor);
			return;
		}
		if (p_iX1 > p_iX2){Swap(p_iX1,p_iX2);}

		MarkDirty(p_iX1, p_iY1, p_iX2+1, p_iY1+1, p_xColor);
		int iOffset = p_iY1 * m_iPitch + p_iX1;
		for (;p_iX1<=p_iX2;p_iX1++){m_pdwPixels[iOffset++] = p_xColor.m_dwColor; }
		return;
	}

	MarkDirty(min(p_iX1, p_iX2), min(p_iY1, p_iY2), max(p_iX1, p_iX2)+1, max(p_iY1, p_iY2)+1, p_xColor);
	float fX1 = (float) p_iX1;
	float fX2 = (float) p_iX2;
	float fY1 = (float) p_iY1;
	float fY2 = (float) p_iY2;

	float fDX,fDY;
	fDX=(float)(fX2-fX1);
	fDY=(float)(fY2-fY1);
	float fSteps;
	if (fabs(fDX)>fabs(fDY))
	{
		fSteps=(float)fabs(fDX);
	}
	else
	{
		fSteps=(float)fabs(fDY);
	}
	fDX/=fSteps;fDY/=fSteps;
	float fX=(float)fX1;
	float fY=(float)fY1;
	while(fSteps>=0.0f)
	{
		m_pdwPixels[(int) fY * m_iPitch + (int) fX] = p_xColor.m_dwColor; 
		fX+=fDX;
		fY+=fDY;
		fSteps--;
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// zeichnet eine Linie
void CDirectX9Device::DrawLineNoDirty(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor)
{
	assert(m_pdwPixels != 0);

	if ((p_iX1 == p_iX2) && (p_iY1 == p_iY2))
	{
		SetPixel(p_iX1, p_iY1, p_xColor);
		return;
	}

	if (p_iX1 == p_iX2)
	{
		if (p_iX1 < 0  ||  p_iX1 >= m_iWidth) { return; }

		int iHeight = m_iHeight-1;
		p_iY1 = clamp(p_iY1,0,iHeight);
		p_iY2 = clamp(p_iY2,0,iHeight);

		if (p_iY1==p_iY2)
		{
			SetPixel(p_iX1, p_iY1, p_xColor);
			return;
		}
		if (p_iY1 > p_iY2){Swap(p_iY1,p_iY2);}

		int iOffset = p_iY1 * m_iPitch + p_iX1;
		for (;p_iY1<=p_iY2;p_iY1++){m_pdwPixels[iOffset] = p_xColor.m_dwColor; iOffset+=m_iPitch;}
		return;
	}
	if (p_iY1 == p_iY2)
	{
		if (p_iY1 < 0  ||  p_iY1 >= m_iHeight) { return; }

		int iWidth = m_iWidth-1;
		p_iX1 = clamp(p_iX1,0,iWidth);
		p_iX2 = clamp(p_iX2,0,iWidth);

		if (p_iX1==p_iX2)
		{
			SetPixel(p_iX1, p_iY1, p_xColor);
			return;
		}
		if (p_iX1 > p_iX2){Swap(p_iX1,p_iX2);}

		int iOffset = p_iY1 * m_iPitch + p_iX1;
		for (;p_iX1<=p_iX2;p_iX1++){m_pdwPixels[iOffset++] = p_xColor.m_dwColor; }
		return;
	}

	float fX1 = (float) p_iX1;
	float fX2 = (float) p_iX2;
	float fY1 = (float) p_iY1;
	float fY2 = (float) p_iY2;

	float fDX,fDY;
	fDX=(float)(fX2-fX1);
	fDY=(float)(fY2-fY1);
	float fSteps;
	if (fabs(fDX)>fabs(fDY))
	{
		fSteps=(float)fabs(fDX);
	}
	else
	{
		fSteps=(float)fabs(fDY);
	}
	fDX/=fSteps;fDY/=fSteps;
	float fX=(float)fX1;
	float fY=(float)fY1;
	while(fSteps>=0.0f)
	{
		m_pdwPixels[(int) fY * m_iPitch + (int) fX] = p_xColor.m_dwColor; 
		fX+=fDX;
		fY+=fDY;
		fSteps--;
	}
}



//---------------------------------------------------------------------------------------------------------------------
/**
	Zeichnet ein nicht ausgefülltes Rechteck (also nur den Umriss)
	Wichtig: der zweite Punkt ist der erste Punkt, der *nicht* mehr im Rechteck
	enthalten ist. (10, 10, 20, 20) zeichnet ein Rechteck mit Höhen und Breite 10 
	(= 20 - 10) und der linken oberen Ecke bei (0, 0)
	Dies ist kompatibel zu Microsoft Windows Auffassungen über Rechtecke.
*/
void CDirectX9Device::DrawRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor)
{
	assert(m_pdwPixels != 0);

	if (p_iX1 >= p_iX2  ||  p_iY1 >= p_iY2)		
	{ 
		return; 
	}

//	MarkDirty(p_iX1, p_iY1, p_iX2, p_iY2, p_xColor);
	COutputDevice::DrawRect(p_iX1, p_iY1, p_iX2, p_iY2, p_xColor);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Zeichnet ein ausgefülltes Rechteck
	Wichtig: der zweite Punkt ist der erste Punkt, der *nicht* mehr im Rechteck
	enthalten ist. (10, 10, 20, 20) zeichnet ein Rechteck mit Höhen und Breite 10 
	(= 20 - 10) und der linken oberen Ecke bei (0, 0)
	Dies ist kompatibel zu Microsoft Windows Auffassungen über Rechtecke.
*/
void CDirectX9Device::FillRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor)
{
	assert(m_pdwPixels != 0);

	p_iX1 = max(0, p_iX1);
	p_iX2 = min(m_iWidth, p_iX2);
	p_iY1 = max(0, p_iY1);
	p_iY2 = min(m_iHeight, p_iY2);

	if (p_iX1 >= p_iX2  ||  p_iY1 >= p_iY2)		
	{ 
		return; 
	}

	MarkDirty(p_iX1, p_iY1, p_iX2, p_iY2, p_xColor);

	// transparentes Rechteck: für Optimierungzwecke vermerken!
	if (p_xColor.m_cAlpha == 0x00)
	{
//		m_xTransparentParts.Add(CRct(p_iX1, p_iY1, p_iX2, p_iY2));

        int iTileX1 = (p_iX1+m_iHTileSize-1) / m_iHTileSize;
        int iTileY1 = (p_iY1+m_iVTileSize-1) / m_iVTileSize;
        int iTileX2 = (p_iX2 < m_iWidth) ? p_iX2 / m_iHTileSize : m_iHTiles;
        int iTileY2 = (p_iY2 < m_iHeight) ? p_iY2 / m_iHTileSize : m_iVTiles;

		int i, j;
		for (j=iTileY1; j<iTileY2; ++j)
		{
		    for (i=iTileX1; i<iTileX2; ++i)
		    {
				m_apxTiles[j*m_iHTiles + i].m_eState = TQuad::S_TRANSPARENT;
			}
		}
	}


	// Zeichnen
	/*
	int iOffset = p_iY1 * m_iPitch + p_iX1;
	int iRowWidth = p_iX2 - p_iX1;
	int iRestWidth = m_iPitch - iRowWidth; 
	int y;
	for (y=p_iY1; y<p_iY2; ++y)
	{
		int iEndRowOffset = iOffset + iRowWidth;
		while (iOffset < iEndRowOffset)
		{
			m_pdwPixels[iOffset++] = p_xColor.m_dwColor; 
		}
		iOffset += iRestWidth;
	}
	*/

	const DWORD dwColor = p_xColor.m_dwColor;

	const int iRowWidth = p_iX2 - p_iX1;
	for (int y = p_iY1; y < p_iY2; ++y)
	{
		DWORD* pdwWritePos = m_pdwPixels + y * m_iPitch + p_iX1;
		DWORD* pdwEndOfLine = pdwWritePos + iRowWidth;

		do 
		{
			*(pdwWritePos++) = dwColor;
		}
		while (pdwWritePos != pdwEndOfLine);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// interne Funktion: zeichnet ein einzelnes Textzeichen
void CDirectX9Device::DrawCharacter(CAlphaBitmapFontCharacter* p_pChar, int p_iX, int p_iY, CColor p_xColor, const CRct& p_xClipRect)
{
	assert(m_pdwPixels != 0);

	if (p_pChar->m_iWidth==0 || p_pChar->m_iHeight==0){return;}

	if (!p_xClipRect.Intersects(CRct(p_iX, p_iY, p_iX + p_pChar->m_iWidth, p_iY + p_pChar->m_iHeight))){return;}

	int iReadIdx		= 0;
	int iReadPitch		= 0;
	int iEndReadIdx		= p_pChar->m_iWidth * p_pChar->m_iHeight;
	int iWriteIdx		= p_iY * m_iPitch + p_iX;
	int iWriteLineEnd	= iWriteIdx + p_pChar->m_iWidth;
	int iWritePitch		= m_iPitch - p_pChar->m_iWidth;

	// clip left

	int iGap = p_xClipRect.left - p_iX;			
	if (iGap > 0)
	{
		iReadPitch		+= iGap;
		iReadIdx		+= iGap;
		iWriteIdx		+= iGap;
		iWritePitch		+= iGap;
	}

	// clip right

	iGap = p_iX + p_pChar->m_iWidth - p_xClipRect.right;		
	if (iGap > 0)
	{
		iReadPitch     += iGap;
		iWriteLineEnd  -= iGap;
		iWritePitch    += iGap;
	}

	// clip top

	iGap = p_xClipRect.top - p_iY;		
	if (iGap > 0)
	{
		iReadIdx		+= iGap * p_pChar->m_iWidth;
		iWriteIdx		+= iGap * m_iPitch;
		iWriteLineEnd	+= iGap * m_iPitch;
	}

	// clip bottom

	iGap = p_iY + p_pChar->m_iHeight - p_xClipRect.bottom;		
	if (iGap > 0)
	{
		iEndReadIdx -= iGap * p_pChar->m_iWidth;
	}

	CColor xAlphaColor = p_xColor;		// in dieser Kopie ändern wir den Alpha, wie wir lustig sind
	while (iReadIdx < iEndReadIdx)
	{
		int iAlpha = p_pChar->m_pBuffer[iReadIdx++];
		if (iAlpha != 0)
		{
			if (iAlpha < 255)
			{
				CColor col = m_pdwPixels[iWriteIdx];
				xAlphaColor.m_cAlpha = (char) iAlpha;
				col *= xAlphaColor;
				m_pdwPixels[iWriteIdx] = col.m_dwColor;
			}
			else
			{
				m_pdwPixels[iWriteIdx] = p_xColor.m_dwColor;
			}
		}

		iWriteIdx++;
		if (iWriteIdx >= iWriteLineEnd)
		{
			iWriteLineEnd += m_iPitch;
			iWriteIdx += iWritePitch;
			iReadIdx  += iReadPitch;
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// zeichnet eine Bitmap
void CDirectX9Device::Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, bool p_bAlpha)
{
	return this->Blit(p_xPnt, p_pxBitmap, m_rctSize, p_bAlpha);
}


//---------------------------------------------------------------------------------------------------------------------
/// zeichnet eine Bitmap
void CDirectX9Device::Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, const CRct& p_xClipRect, bool p_bAlpha)
{
	assert(m_pdwPixels != 0);

	CRct rctBitmap = CRct(p_xPnt.x, p_xPnt.y, p_xPnt.x + p_pxBitmap->GetSize().cx, p_xPnt.y + p_pxBitmap->GetSize().cy);
	CRct xClipRect = p_xClipRect.Clip(m_rctSize);
	if (!xClipRect.Intersects(rctBitmap))
	{
		return;
	}

	int iReadIdx		= 0;
	int iReadPitch		= 0;
	int iEndReadIdx		= p_pxBitmap->GetNumPixels();
	int iWriteIdx		= p_xPnt.y * m_iPitch + p_xPnt.x;
	int iWriteLineEnd	= iWriteIdx + p_pxBitmap->GetSize().cx;
	int iWritePitch		= m_iPitch - p_pxBitmap->GetSize().cx;

	// clip left

	int iGap = xClipRect.left - p_xPnt.x;			
	if (iGap > 0)
	{
		iReadPitch		+= iGap;
		iReadIdx		+= iGap;
		iWriteIdx		+= iGap;
		iWritePitch		+= iGap;
	}

	// clip right

	iGap = p_xPnt.x + p_pxBitmap->GetSize().cx - xClipRect.right;		
	if (iGap > 0)
	{
		iReadPitch     += iGap;
		iWriteLineEnd  -= iGap;
		iWritePitch    += iGap;
	}

	// clip top

	iGap = xClipRect.top - p_xPnt.y;		
	if (iGap > 0)
	{
		iReadIdx		+= iGap * p_pxBitmap->GetSize().cx;
		iWriteIdx		+= iGap * m_iPitch;
		iWriteLineEnd	+= iGap * m_iPitch;
	}

	// clip bottom

	iGap = p_xPnt.y + p_pxBitmap->GetSize().cy - xClipRect.bottom;		
	if (iGap > 0)
	{
		iEndReadIdx -= iGap * p_pxBitmap->GetSize().cx;
	}

	// blitting loop

	const unsigned long* kpuiBitmap=p_pxBitmap->GetRawData();
	if (!p_bAlpha)
	{
		// Version ohne alpha
		
		while (iReadIdx < iEndReadIdx)
		{
			m_pdwPixels[iWriteIdx] = kpuiBitmap[iReadIdx++];

			iWriteIdx++;
			if (iWriteIdx >= iWriteLineEnd)
			{
				iWriteLineEnd += m_iPitch;
				iWriteIdx += iWritePitch;
				iReadIdx  += iReadPitch;
			}
		}
	}
	else
	{
		// Version mit Alpha

		while (iReadIdx < iEndReadIdx)
		{
				
			unsigned long iAlpha = (kpuiBitmap[iReadIdx] & 0xFF000000) >> 24;
			if (iAlpha != 0)
			{
				if (iAlpha < 255)
				{
					CColor col = CColor(m_pdwPixels[iWriteIdx]);
					col *= CColor(kpuiBitmap[iReadIdx]);
					m_pdwPixels[iWriteIdx] = col.m_dwColor;
				}
				else
				{
					m_pdwPixels[iWriteIdx] = kpuiBitmap[iReadIdx];
				}
			}
			iReadIdx++;
			iWriteIdx++;
			if (iWriteIdx >= iWriteLineEnd)
			{
				iWriteLineEnd += m_iPitch;
				iWriteIdx += iWritePitch;
				iReadIdx  += iReadPitch;
			}
		}
	}

	// markiere Textur als 'dirty'

	rctBitmap = rctBitmap.Clip(xClipRect);
	if (!rctBitmap.IsEmpty())
	{
//		DrawRect(rctBitmap.left, rctBitmap.top, rctBitmap.right, rctBitmap.bottom, CColor(0, 255, 0));
		MarkDirty(rctBitmap.left, rctBitmap.top, rctBitmap.right, rctBitmap.bottom, CColor(255, 255, 255, 255));
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// schreibt ungültige ('dirty') Texturen neu
void CDirectX9Device::UpdateDirtyTextures()
{
	for (unsigned int i = 0; i<m_apxTiles.Size(); ++i)
	{
		if (m_apxTiles[i].m_bDirty)
		{
//			DebugPrint("Surace %d was dirty", i);
			IDirect3DSurface9* pTxtSurface;
			IDirect3DSurface9* pSrcSurface;
			m_pxBackBuffer->GetSurfaceLevel(0,&pSrcSurface);
			m_apxTiles[i].m_pxTexture->GetSurfaceLevel(0, &pTxtSurface);
	
			RECT r;
			r.top    = m_apxTiles[i].m_iPosY;
			r.left   = m_apxTiles[i].m_iPosX;
			r.bottom = r.top  + m_apxTiles[i].m_iHeight;
			r.right  = r.left + m_apxTiles[i].m_iWidth; 

			POINT p; p.x = p.y = 0;
			HRESULT hr = m_pxD3DDevice->UpdateSurface(pSrcSurface, &r, pTxtSurface, &p);
			assert(SUCCEEDED(hr));

			pSrcSurface->Release();
			pTxtSurface->Release();
			m_apxTiles[i].m_bDirty = false;
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CDirectX9Device::UpdateAnimation()
{
	if (m_pxInterpolatorSX || m_pxInterpolatorSY || m_pxInterpolatorX || m_pxInterpolatorY || 
		m_pxInterpolatorAlpha || m_pxInterpolatorBrightness || m_pxInterpolatorRotationAngle)
	{
		float fTime = CTimer::GetSystemTimeInS();
		if (m_pxInterpolatorX)	
		{ 
			if (m_pxInterpolatorX->Update(fTime)) { delete m_pxInterpolatorX; m_pxInterpolatorX = 0; }
		}
		if (m_pxInterpolatorY)
		{ 
			if (m_pxInterpolatorY->Update(fTime)) { delete m_pxInterpolatorY; m_pxInterpolatorY = 0; }
		}
		if (m_pxInterpolatorSX)
		{ 
			if (m_pxInterpolatorSX->Update(fTime)) { delete m_pxInterpolatorSX; m_pxInterpolatorSX = 0; }
		}
		if (m_pxInterpolatorSY)
		{ 
			if (m_pxInterpolatorSY->Update(fTime)) { delete m_pxInterpolatorSY; m_pxInterpolatorSY = 0; } 
		}
		if (m_pxInterpolatorAlpha)
		{ 
			if (m_pxInterpolatorAlpha->Update(fTime)) { delete m_pxInterpolatorAlpha; m_pxInterpolatorAlpha = 0; }
		}
		if (m_pxInterpolatorBrightness)
		{ 
			if (m_pxInterpolatorBrightness->Update(fTime)) { delete m_pxInterpolatorBrightness; m_pxInterpolatorBrightness = 0; } 
		}
		if (m_pxInterpolatorRotationAngle)
		{
			if (m_pxInterpolatorRotationAngle->Update(fTime)) { delete m_pxInterpolatorRotationAngle; m_pxInterpolatorRotationAngle = 0; } 
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
/// rendert dieses Device über DirectX
void 
CDirectX9Device::Render2D()
{
	UpdateAnimation();

	if (m_pxBlendVertexShader)
	{
		if (m_iBrightness == 0)
		{
			// falls Helligkeit = 0, kann auch gecleart werden
			m_pxD3DDevice->Clear(0, 0, D3DCLEAR_TARGET, 0, 0, 0);
			return;
		}

		if ((m_iBrightness == 255) &&
			(m_iGlobalInterfaceAlpha == 0))
		{
			// falls Helligkeit = 100% und Transparenz = 100%, muss nichts gerendert werden 
			return;
		}
		// <- Achtung: können diese Optimierungen zu Performance-Stalls führen, wenn später das komplette Interface
		// geupdatet werden muss, weil alle Kacheln dirty sind? (die Optimierungen hier skippen ja auch das Updaten der Texturen)
	}

	UpdateDirtyTextures();
	HRESULT hr;


	m_pxD3DDevice->SetRenderState(D3DRS_ZENABLE, FALSE);
	m_pxD3DDevice->SetRenderState(D3DRS_STENCILENABLE, FALSE);
	m_pxD3DDevice->SetRenderState(D3DRS_CULLMODE, D3DCULL_NONE);
	m_pxD3DDevice->SetRenderState(D3DRS_FILLMODE, D3DFILL_SOLID);
	m_pxD3DDevice->SetRenderState(D3DRS_MULTISAMPLEANTIALIAS, FALSE);
	m_pxD3DDevice->SetRenderState(D3DRS_COLORWRITEENABLE, D3DCOLORWRITEENABLE_RED | D3DCOLORWRITEENABLE_GREEN | D3DCOLORWRITEENABLE_BLUE | D3DCOLORWRITEENABLE_ALPHA);

	m_pxD3DDevice->SetSamplerState(0, D3DSAMP_MINFILTER, D3DTEXF_POINT);
	m_pxD3DDevice->SetSamplerState(0, D3DSAMP_MAGFILTER, D3DTEXF_POINT);
	m_pxD3DDevice->SetSamplerState(0, D3DSAMP_MIPFILTER, D3DTEXF_NONE);
	m_pxD3DDevice->SetSamplerState(0, D3DSAMP_ADDRESSU, D3DTADDRESS_CLAMP);
	m_pxD3DDevice->SetSamplerState(0, D3DSAMP_ADDRESSV, D3DTADDRESS_CLAMP);

	D3DXMATRIX matIdentity;
	D3DXMatrixIdentity(&matIdentity);
	m_pxD3DDevice->SetTransform(D3DTS_TEXTURE0, &matIdentity);
	m_pxD3DDevice->SetTransform(D3DTS_WORLD, &matIdentity);
	m_pxD3DDevice->SetTransform(D3DTS_VIEW, &matIdentity);
	m_pxD3DDevice->SetTransform(D3DTS_PROJECTION, &matIdentity);


	hr = m_pxD3DDevice->SetIndices(m_pxIndexBuffer);
	assert(SUCCEEDED(hr));
	hr = m_pxD3DDevice->SetStreamSource(0, m_pxVertexBuffer, 0, sizeof(TVertexFormat));
	assert(SUCCEEDED(hr));

	hr = m_pxD3DDevice->SetFVF( D3DFVF_XYZRHW | D3DFVF_TEX1 );
	assert(SUCCEEDED(hr));


	if ((m_iBrightness != 255 ||  
		 m_fScaleX != 1.0f || m_fScaleY != 1.0f || 
		 m_fTranslationX != 0.0f || m_fTranslationY != 0.0f) &&
		m_pxBlendVertexShader)
	{
		SetupBlendShaderRenderStates();

		hr = m_pxD3DDevice->SetVertexDeclaration(m_pxVertexDeclaration);
		assert(SUCCEEDED(hr));

		hr = m_pxD3DDevice->SetVertexShader(m_pxBlendVertexShader);
		assert(SUCCEEDED(hr));
		hr = m_pxD3DDevice->SetPixelShader(NULL);
		assert(SUCCEEDED(hr));
	}
	else
	{
		SetupFixedFunctionRenderStates();
	}

	int x, y;
	int i = 0;

	int iTilesRendered = 0;
	for (y = 0; y < m_iVTiles; ++y)
	{
		for (x = 0; x < m_iHTiles; ++x)
		{
			if (m_iBrightness != 255 || m_apxTiles[i].m_eState != TQuad::S_TRANSPARENT)
			{
				hr = m_pxD3DDevice->SetTexture(0, m_apxTiles[i].m_pxTexture);
				assert(SUCCEEDED(hr));

				int iStart = m_apxTiles[i].m_iIndexBufferOffset;

				hr = m_pxD3DDevice->DrawIndexedPrimitive(D3DPT_TRIANGLELIST, 0, (y*m_iHTiles + x) * 4, 4, iStart, 2);
				assert(SUCCEEDED(hr));
				iTilesRendered++;
			}
			++i;
		}
	}
//	DebugPrint("tiles rendered: %d, TranspartPartsList Size %d", iTilesRendered, m_xTransparentParts.Size());
}

//---------------------------------------------------------------------------------------------------------------------
/// stellt das DirectXDevice gemäß globalem InterfaceAlpha und Brightness ein
void
CDirectX9Device::SetupBlendShaderRenderStates()
{
	// Formel für ErgebnisPixelFarbe:
	//      C = F * (Ai * At * Ct + (1 - Ai * At) * BG)
	//           ->umformen->
	//      C = F * Ai * At * Ct  +  F * (1 - Ai * At) * BG
	//
	// Variablensemantik:
	//      C:  Ergebnispixel
	//      F:  FadeFaktor == Brightness
	//      Ai: globaler InterfaceAlpha
	//      At: lokales InterfaceAlpha (aus Textur)
	//      Ct: Interfacefarbe (aus Textur)
	//      BG: Hintergrundfarbe
	//
	// Implementation
	//      Die Summe wird mittels AlphaBlending gebildet (1). Der Faktor vor BG ist das 
	//      Ergbis der Alpha-Pipe (2), der linke Summand ist das Ergebnis der Color-Pipe (3). 
	//      Ai wird in TFACTOR.a bereitgestellt (4); F wird in DIFFUSE.a abgelegt (VSH);
	//      TFACTOR.rgb = 0 (4); F * Ai wird vom Vertexshader in DIFFUSE.rgb bereitgestellt;
	//      At und Ct werden der Interface-Textur entnommen.
	//
	// alles klar?


	// (1) Alpha-Blending
	m_pxD3DDevice->SetRenderState(D3DRS_ALPHABLENDENABLE, TRUE);
	m_pxD3DDevice->SetRenderState(D3DRS_SRCBLEND, D3DBLEND_ONE);
	m_pxD3DDevice->SetRenderState(D3DRS_DESTBLEND, D3DBLEND_SRCALPHA);


	m_pxD3DDevice->SetRenderState(D3DRS_ALPHATESTENABLE, FALSE);

	// (2) Alpha-Pipe:
	m_pxD3DDevice->SetTextureStageState(0, D3DTSS_ALPHAARG1, D3DTA_TEXTURE);
	m_pxD3DDevice->SetTextureStageState(0, D3DTSS_ALPHAARG2, D3DTA_TFACTOR);
	m_pxD3DDevice->SetTextureStageState(0, D3DTSS_ALPHAOP, D3DTOP_MODULATE);

	m_pxD3DDevice->SetTextureStageState(1, D3DTSS_ALPHAARG1, D3DTA_CURRENT | D3DTA_COMPLEMENT);
	m_pxD3DDevice->SetTextureStageState(1, D3DTSS_ALPHAARG2, D3DTA_DIFFUSE);
	m_pxD3DDevice->SetTextureStageState(1, D3DTSS_ALPHAOP, D3DTOP_MODULATE);

	m_pxD3DDevice->SetTextureStageState(2, D3DTSS_ALPHAOP, D3DTOP_DISABLE);


	// (3) Color-Pipe:
	m_pxD3DDevice->SetTextureStageState(0, D3DTSS_COLORARG1, D3DTA_TEXTURE);
	m_pxD3DDevice->SetTextureStageState(0, D3DTSS_COLORARG2, D3DTA_TFACTOR);
	m_pxD3DDevice->SetTextureStageState(0, D3DTSS_COLOROP, D3DTOP_BLENDTEXTUREALPHA);

	m_pxD3DDevice->SetTextureStageState(1, D3DTSS_COLORARG1, D3DTA_DIFFUSE);
	m_pxD3DDevice->SetTextureStageState(1, D3DTSS_COLORARG2, D3DTA_CURRENT);
	m_pxD3DDevice->SetTextureStageState(1, D3DTSS_COLOROP, D3DTOP_MODULATE);

	m_pxD3DDevice->SetTextureStageState(2, D3DTSS_COLOROP, D3DTOP_DISABLE);

	//m_pxD3DDevice->SetTextureStageState(1, D3DTSS_COLORARG1, D3DTA_TEXTURE); 
	// <- alternative Lösung für rage 128, auf der D3DTSS_COLORARG1 Textur sein muss 
	// (Dafür muss dann UV1 == Diffuse && Textur = Grauverlauf 0->1 sein + clampU)


	// (4) TFactor:
	m_pxD3DDevice->SetRenderState(D3DRS_TEXTUREFACTOR, 
		m_iGlobalInterfaceAlpha << 24);

	// Shaderkonstanten
	float afConstants[] = {
		m_iGlobalInterfaceAlpha / 255.0f, // c0.x
		m_iBrightness / 255.0f,           // c0.y
		0, 1,                             // c0.zw

		(2.0f) / m_iScreenWidth,			// c1.x
		(-2.0f)/ m_iScreenHeight,			// c1.y
		0, 0,								// c1.zw
	};

	D3DXMATRIX matRotationCenter, matScaling, matRotation, matRotationCenterInv, matTranslation;
	D3DXMatrixScaling(&matScaling, m_fScaleX, m_fScaleY, 1);
	D3DXMatrixRotationZ(&matRotation, m_fRotationAngle);
	D3DXMatrixTranslation(&matRotationCenter, -m_fPivotX, -m_fPivotY, 0);
	D3DXMatrixTranslation(&matRotationCenterInv, m_fPivotX, m_fPivotY, 0);
	D3DXMatrixTranslation(&matTranslation, m_fTranslationX, m_fTranslationY, 0);

	D3DXMATRIX matTransform;
	D3DXMatrixMultiply(&matTransform, &matRotationCenter, &matScaling);
	D3DXMatrixMultiply(&matTransform, &matTransform, &matRotation);
	D3DXMatrixMultiply(&matTransform, &matTransform, &matRotationCenterInv);
	D3DXMatrixMultiply(&matTransform, &matTransform, &matTranslation);

	D3DXMatrixTranspose(&matTransform, &matTransform);

	m_pxD3DDevice->SetVertexShaderConstantF(0, afConstants, 2);
	m_pxD3DDevice->SetVertexShaderConstantF(2, (const float*) &matTransform, 4);
}
//---------------------------------------------------------------------------------------------------------------------
void
CDirectX9Device::SetupFixedFunctionRenderStates()
{
	m_pxD3DDevice->SetVertexShader(NULL);
	m_pxD3DDevice->SetPixelShader(NULL);

	m_pxD3DDevice->SetRenderState(D3DRS_ALPHATESTENABLE, TRUE);
	m_pxD3DDevice->SetRenderState(D3DRS_ALPHAFUNC, D3DCMP_GREATER);
	m_pxD3DDevice->SetRenderState(D3DRS_FOGENABLE, FALSE);
	m_pxD3DDevice->SetRenderState(D3DRS_ALPHAREF, 0);

	m_pxD3DDevice->SetRenderState(D3DRS_ALPHABLENDENABLE, TRUE);
	m_pxD3DDevice->SetRenderState(D3DRS_SRCBLEND, D3DBLEND_SRCALPHA);
	m_pxD3DDevice->SetRenderState(D3DRS_DESTBLEND, D3DBLEND_INVSRCALPHA);

	m_pxD3DDevice->SetTextureStageState(0, D3DTSS_ALPHAARG1, D3DTA_TEXTURE);
	m_pxD3DDevice->SetTextureStageState(0, D3DTSS_ALPHAARG2, D3DTA_TFACTOR);
	m_pxD3DDevice->SetTextureStageState(0, D3DTSS_ALPHAOP, D3DTOP_MODULATE);
	m_pxD3DDevice->SetTextureStageState(1, D3DTSS_ALPHAOP, D3DTOP_DISABLE);

	m_pxD3DDevice->SetTextureStageState(0, D3DTSS_COLORARG1, D3DTA_TEXTURE);
	m_pxD3DDevice->SetTextureStageState(0, D3DTSS_COLOROP, D3DTOP_SELECTARG1);
	m_pxD3DDevice->SetTextureStageState(1, D3DTSS_COLOROP, D3DTOP_DISABLE);

	m_pxD3DDevice->SetRenderState(D3DRS_TEXTUREFACTOR, m_iGlobalInterfaceAlpha << 24);
}
//---------------------------------------------------------------------------------------------------------------------
/// lädt den Vertexshader, der für die Blend-Effekte verwendet wird;
/// daran, dass m_pxBlendVertexShader != 0 ist erkennt das Rendering,
/// dass spezielle Renderstates gesetzt werden müssen ->SetBlendShaderState()
void 
CDirectX9Device::EnableBlendShader(const CStr& p_rsFilename)
{
	assert(m_pxD3DDevice);

	// 1.) vertexshader erzeugen
	LPD3DXBUFFER pCode = NULL;
	LPD3DXBUFFER pErrorMsgs = NULL;

	HRESULT hr;
	    
	if (p_rsFilename.IsEmpty())
	{
		// falls kein Dateinamen übergeben, den default-shader erzeugen
		const char* pcShader = 
			"vs_1_1\n"
			"dcl_position		v0                          \n"
			"dcl_texcoord		v1                          \n"
			"def    c6,			-1.0f, 1.0f, 0.0f, 1.0f     \n"
			"mov    r6,			c6                          \n"
			"m4x3	r1.xyz,		v0,		c2					\n"	
			"mad    oPos.xyz,	r1,		c1,     r6          \n"		// output (oPos) = r0 * c1 (scaling) + r4 (verschiebung 0/0 ins zentrum)
			"mov	oPos.w,		c6.w						\n"
			"mov    oT0,		v1                          \n"
			"mov    r1,			c0.xxxw                     \n"
			"mul    oD0,		c0.y,   r1                  \n";


		hr = D3DXAssembleShader(pcShader, (UINT)strlen(pcShader), NULL, NULL,
				0, &pCode, &pErrorMsgs);
	}
	else
	{
		// Shader aus Datei erzeugen - FIXME: SearchPath wird noch nicht verwendet
		hr = D3DXAssembleShaderFromFile(p_rsFilename.c_str(), NULL, NULL, 0, &pCode, &pErrorMsgs);
	}

	assert(SUCCEEDED(hr));

	if (pErrorMsgs)
	{
		MessageBox(NULL, (LPCSTR)pErrorMsgs->GetBufferPointer(), "vsh - error", MB_ICONERROR | MB_OK);
		pErrorMsgs->Release();
		return;
	}

	assert(pCode);

	hr = m_pxD3DDevice->CreateVertexShader((DWORD*)pCode->GetBufferPointer(), &m_pxBlendVertexShader);
	assert(SUCCEEDED(hr));
	pCode->Release();


	// 2.) vertexdeclaration anlegen (wird benötigt um Vertexshader zu erzeugen)
	D3DVERTEXELEMENT9 axVE[] = {
		{ 0, 0,		D3DDECLTYPE_FLOAT4,		D3DDECLMETHOD_DEFAULT,	D3DDECLUSAGE_POSITION,	0 },				// VT_4T2
		{ 0, 16,    D3DDECLTYPE_FLOAT2,		D3DDECLMETHOD_DEFAULT,	D3DDECLUSAGE_TEXCOORD,	0 },
		D3DDECL_END() };

	hr = m_pxD3DDevice->CreateVertexDeclaration(axVE, &m_pxVertexDeclaration);
	assert(SUCCEEDED(hr));
}
//---------------------------------------------------------------------------------------------------------------------
void 
CDirectX9Device::SetGlobalInterfaceAlpha(int iGlobalInterfaceAlpha)
{
    m_iGlobalInterfaceAlpha = iGlobalInterfaceAlpha;
	if (m_pxInterpolatorAlpha)	{ delete m_pxInterpolatorAlpha; m_pxInterpolatorAlpha = 0; }
}
//---------------------------------------------------------------------------------------------------------------------
int
CDirectX9Device::GetGlobalInterfaceAlpha()
{
    return m_iGlobalInterfaceAlpha;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CDirectX9Device::SetBrightness(int iBrightness)
{
    m_iBrightness = iBrightness;
	if (m_pxInterpolatorBrightness)	{ delete m_pxInterpolatorBrightness; m_pxInterpolatorBrightness = 0; }
}
//---------------------------------------------------------------------------------------------------------------------
int
CDirectX9Device::GetBrightness()
{
    return m_iBrightness;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CDirectX9Device::SetDeviceTranslation(float p_fDeltaX, float p_fDeltaY)
{
	m_fTranslationX = p_fDeltaX;
	m_fTranslationY = p_fDeltaY;
	if (m_pxInterpolatorX )	{ delete m_pxInterpolatorX; m_pxInterpolatorX = 0; }
	if (m_pxInterpolatorY )	{ delete m_pxInterpolatorY; m_pxInterpolatorY = 0; }
}

//---------------------------------------------------------------------------------------------------------------------
void						
CDirectX9Device::SetDeviceScaling(float p_fScaleX, float p_fScaleY)
{
	m_fScaleX = p_fScaleX;
	m_fScaleY = p_fScaleY;
	if (m_pxInterpolatorSX )	{ delete m_pxInterpolatorSX; m_pxInterpolatorSX = 0; }
	if (m_pxInterpolatorSY )	{ delete m_pxInterpolatorSY; m_pxInterpolatorSY = 0; }
}

//---------------------------------------------------------------------------------------------------------------------
void
CDirectX9Device::SetDeviceRotation(float p_fAngle)
{
	m_fRotationAngle = p_fAngle;
	if (m_pxInterpolatorRotationAngle )	{ delete m_pxInterpolatorRotationAngle; m_pxInterpolatorRotationAngle = 0; }
}

//---------------------------------------------------------------------------------------------------------------------
void
CDirectX9Device::SetDevicePivot(float p_fX, float p_fY)
{
	m_fPivotX = p_fX;
	m_fPivotY = p_fY;
}

//---------------------------------------------------------------------------------------------------------------------
/// markiert einen Bereich als 'dirty' (Texturen müssen neu geschrieben werden)
void			
CDirectX9Device::MarkDirty(int p_iX, int p_iY, const CColor& p_xColor)
{
    TQuad& rxQuad = m_apxTiles[(p_iY / m_iVTileSize)*m_iHTiles + (p_iX / m_iHTileSize)];
    
    rxQuad.m_bDirty = true;

	if (p_xColor.m_cAlpha != 0x00)
	{
//		m_xTransparentParts.Sub(CRct(p_iX, p_iY, p_iX+1, p_iY+1));
        rxQuad.m_eState = TQuad::S_OPAQUE;
	}
    else
    {
		if (rxQuad.m_eState == TQuad::S_OPAQUE)
		{
	    	rxQuad.m_eState = TQuad::S_UNKNOWN;
		}
    }
}


//---------------------------------------------------------------------------------------------------------------------
/// markiert einen Bereich als 'dirty' (Texturen müssen neu geschrieben werden)
void 
CDirectX9Device::MarkDirty(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor)
{
	assert(p_iX2 >= p_iX1  &&  p_iY2 >= p_iY1);
	
	p_iX1 = max(p_iX1, 0);
	p_iY1 = max(p_iY1, 0);
	p_iX2 = min(p_iX2, m_iWidth);
	p_iY2 = min(p_iY2, m_iHeight);

	if (p_iX1 == p_iX2  ||  p_iY1 == p_iY2) { return; }

    TQuad::State eState = TQuad::S_UNKNOWN;
	if (p_xColor.m_cAlpha != 0x00)
	{
//		m_xTransparentParts.Sub(CRct(p_iX1, p_iY1, p_iX2, p_iY2));
		eState = TQuad::S_OPAQUE;
	}

    int i, j;
	for (j=(p_iY1 / m_iVTileSize); j<=((p_iY2-1) / m_iVTileSize); ++j)
	{
	    for (i=(p_iX1 / m_iHTileSize); i<=((p_iX2-1) / m_iHTileSize); ++i)
	    {
			m_apxTiles[j*m_iHTiles + i].m_bDirty = true;
			m_apxTiles[j*m_iHTiles + i].m_eState = eState;
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void						
CDirectX9Device::DrawTextAlphaBitmapFont(CFontHandle p_xFontHandle, int p_iX, int p_iY, CStr p_sText, CColor p_xColor, const CRct& p_xClipRect)
{
	CRct xClipRect = p_xClipRect.Clip(m_rctSize);

	int i, iKerning;
	int x = p_iX;
	int iMaxOverBaseLine  = 0;
	int iMaxUnderBaseLine = 0;
	unsigned long iChar, iOldChar;

	i = 0; iOldChar = 0;
	while(p_sText.c_str()[i] != 0)
	{
		int iBytes = 1;
		iChar = (unsigned char) p_sText.GetAt(i);

		if (i>0)
		{
			iKerning = ((CAlphaBitmapFont*) (p_xFontHandle.m_pxFont))->GetKerning(iOldChar, iChar);
		}
		else
		{
			iKerning=0;
		}
		CAlphaBitmapFontCharacter* pChar = ((CAlphaBitmapFont*) (p_xFontHandle.m_pxFont))->GetChar(iChar);
		x += pChar->m_iSpacingBefore + iKerning;
		DrawCharacter(pChar, x, p_iY-pChar->m_iHeightOverBaseLine, p_xColor, xClipRect);
		x += pChar->m_iWidth + pChar->m_iSpacingAfter;
	
		if (x >= xClipRect.right) { break; }

		iMaxOverBaseLine = max(iMaxOverBaseLine, pChar->m_iHeightOverBaseLine);
		iMaxUnderBaseLine = max(iMaxUnderBaseLine, pChar->m_iHeight - pChar->m_iHeightOverBaseLine);

		iOldChar = iChar;
		i+= iBytes;
	}

	CRct r = CRct(p_iX, p_iY-iMaxOverBaseLine, x, p_iY+iMaxUnderBaseLine);
	r = r.Clip(p_xClipRect);
	if (!r.IsEmpty())
	{
	//	DrawRect(r.left, r.top, r.right, r.bottom, KColor(0, 255, 0));
		MarkDirty(r.left, r.top, r.right, r.bottom, p_xColor);
	}
}


//---------------------------------------------------------------------------------------------------------------------
void						
CDirectX9Device::MoveTo(float p_fTargetX, float p_fTargetY, float p_fTimeInSeconds)
{
	if (m_pxInterpolatorX)	{ delete m_pxInterpolatorX; }
	if (m_pxInterpolatorY)	{ delete m_pxInterpolatorY; }

	float fTime = CTimer::GetSystemTimeInS();
	m_pxInterpolatorX = new CLinearInterpolator(&m_fTranslationX, p_fTargetX, fTime, p_fTimeInSeconds);
	m_pxInterpolatorY = new CLinearInterpolator(&m_fTranslationY, p_fTargetY, fTime, p_fTimeInSeconds);
}


//---------------------------------------------------------------------------------------------------------------------
void						
CDirectX9Device::ScaleTo(float p_fTargetXScale, float p_fTargetYScale, float p_fTimeInSeconds)
{
	if (m_pxInterpolatorSX)	{ delete m_pxInterpolatorSX; }
	if (m_pxInterpolatorSY)	{ delete m_pxInterpolatorSY; }

	float fTime = CTimer::GetSystemTimeInS();
	m_pxInterpolatorSX = new CLinearInterpolator(&m_fScaleX, p_fTargetXScale, fTime, p_fTimeInSeconds);
	m_pxInterpolatorSY = new CLinearInterpolator(&m_fScaleY, p_fTargetYScale, fTime, p_fTimeInSeconds);
}

//---------------------------------------------------------------------------------------------------------------------
void
CDirectX9Device::RotateTo(float p_fTargetAngle, float p_fTimeInSeconds)
{
	if (m_pxInterpolatorRotationAngle)		{ delete m_pxInterpolatorRotationAngle; }

	float fTime = CTimer::GetSystemTimeInS();
	m_pxInterpolatorRotationAngle = new CLinearInterpolator(&m_fRotationAngle, p_fTargetAngle, fTime, p_fTimeInSeconds);
}

//---------------------------------------------------------------------------------------------------------------------
void						
CDirectX9Device::FadeAlphaTo(int p_iTargetAlpha, float p_fTimeInSeconds)
{
	if (m_pxInterpolatorAlpha)	{ delete m_pxInterpolatorAlpha; }

	float fTime = CTimer::GetSystemTimeInS();
	m_pxInterpolatorAlpha = new CLinearInterpolator(&m_iGlobalInterfaceAlpha, p_iTargetAlpha, fTime, p_fTimeInSeconds);
}

//---------------------------------------------------------------------------------------------------------------------
void						
CDirectX9Device::FadeBrightnessTo(int p_iTargetBrightness, float p_fTimeInSeconds)
{
	if (m_pxInterpolatorBrightness)	{ delete m_pxInterpolatorBrightness; }

	float fTime = CTimer::GetSystemTimeInS();
	m_pxInterpolatorBrightness = new CLinearInterpolator(&m_iBrightness, p_iTargetBrightness, fTime, p_fTimeInSeconds);
}
//---------------------------------------------------------------------------------------------------------------------
bool
CDirectX9Device::AnimationRunning()
{
	UpdateAnimation();
	return  m_pxInterpolatorSX || m_pxInterpolatorSY || m_pxInterpolatorX || m_pxInterpolatorY || 
			m_pxInterpolatorAlpha || m_pxInterpolatorBrightness || m_pxInterpolatorRotationAngle;
}					
//---------------------------------------------------------------------------------------------------------------------
IDirect3DTexture9*
CDirectX9Device::GetTexture(int p_iTileX, int p_iTileY)
{
	return m_apxTiles[p_iTileY * m_iHTiles + p_iTileX].m_pxTexture;
}
//---------------------------------------------------------------------------------------------------------------------

} // namespace UILib
