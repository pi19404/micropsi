#ifndef UILIB_DIRECTX9CONTEXT_H_INCLUDED
#define UILIB_DIRECTX9CONTEXT_H_INCLUDED

#include "outputdevice.h"
#include "linearinterpolator.h"

// Direct3D-Interfaces als forward deklarieren, damit wir nicht hier schon die Header includen müssen

struct IDirect3DTexture9;
struct IDirect3DVertexBuffer9;
struct IDirect3DIndexBuffer9;
struct IDirect3DDevice9;
struct IDirect3DVertexShader9;
struct IDirect3DVertexDeclaration9;

namespace UILib 
{

class CDirectX9Device : public COutputDevice
{
public:

	CDirectX9Device(IDirect3DDevice9* p_pxD3DDevice, 
					int p_iWidth, int p_iHeight, 
					int p_iScreenWidth = 0, int p_iScreenHeight = 0, 
					int p_iTileWidth = 128, int p_iTileHeight = 128);

	virtual ~CDirectX9Device();

	virtual CFourCC				GetType() const		{ return CFourCC("DX90"); }

	virtual CSize				GetSize() const		{ return CSize(m_iWidth, m_iHeight); }

	virtual bool				BeginPaint();
	virtual void				EndPaint(bool p_bValidateAll = true);

	virtual void				Invalidate();

	virtual CColor				GetPixel(int p_iX, int p_iY);
	virtual void				SetPixel(int p_iX, int p_iY, const CColor& p_xColor);

	virtual void				DrawLine(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor);
	virtual void				DrawLineNoDirty(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor);

	virtual void				DrawRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor);

	virtual void				FillRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor);

	virtual void				Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, bool p_bAlpha = false);
	virtual void				Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, const CRct& p_xClipRect, bool p_bAlpha = false);

	void						Render2D();

    void                        EnableBlendShader(const CStr& p_rsFilename = "");
    void                        SetGlobalInterfaceAlpha(int iGlobalInterfaceAlpha);
    int                         GetGlobalInterfaceAlpha();
    void                        SetBrightness(int iBrightness);
    int                         GetBrightness();
	void						SetDeviceTranslation(float p_fDeltaX, float p_fDeltaY);
	void						SetDeviceScaling(float p_fScaleX, float p_fScaleY);
	void						SetDeviceRotation(float p_fAngle);
	void						SetDevicePivot(float p_fX, float p_fY);

	void						MoveTo(float p_fTargetX, float p_fTargetY, float p_fTimeInSeconds);
	void						ScaleTo(float p_fTargetXScale, float p_fTargetYScale, float p_fTimeInSeconds);
	void						RotateTo(float p_fTargetAngle, float p_fTimeInSeconds);
	void						FadeAlphaTo(int p_iTargetAlpha, float p_fTimeInSeconds);
	void						FadeBrightnessTo(int p_iTargetBrightness, float p_fTimeInSeconds);

	bool						AnimationRunning();	

	IDirect3DTexture9*			GetTexture(int p_iTileX = 0, int p_iTileY = 0);

	/// wird eigentlich von Render() automatisch gerufen, aber wenn man die Textur anders rendert, muss man es selbst machen
	void						UpdateDirtyTextures();

protected: 

	void						UpdateAnimation();
    
	bool						ClearMemTexture(IDirect3DTexture9* p_pxTexture);

    void                        SetupBlendShaderRenderStates();
    void                        SetupFixedFunctionRenderStates();

    virtual void				DrawCharacter(CAlphaBitmapFontCharacter* p_pChar, int p_iX, int p_iY, CColor p_xColor, const CRct& p_xClipRect);
	virtual void				DrawTextAlphaBitmapFont(CFontHandle p_xFontHandle, int p_iX, int p_iY, CStr p_sText, CColor p_xColor, const CRct& p_xClipRect);

	struct TQuad
	{
		enum State
		{
			S_OPAQUE,
			S_TRANSPARENT,
			S_UNKNOWN
		};

		IDirect3DTexture9*		m_pxTexture;			///< Textur, wird aus dem Backbuffer aktualisiert
		bool 					m_bDirty;				///< dirty-bit; gesetzt, wenn Textur aktualisiert werden muss.
		int						m_iWidth;				///< tatsächlich verwendete Breite in Pixeln, evtl. < Texturbreite
		int						m_iHeight;				///< tatsächlich verwendete Höhe in Pixeln, evtl. < Texturbreite
		int						m_iPosX;				///< X-Koordinate auf der Surface
		int						m_iPosY;				///< Y-Koordinate auf der Surface
		CRct					m_rRect;				///< Rechteck (im Prinzip redundante Info)
		int						m_iIndexBufferOffset;	///< Start des Quads im Indexbuffer
		State					m_eState;
	};

	struct TVertexFormat
	{
		float   m_fX;
		float   m_fY;
		float   m_fZ;
		float	m_fRHW;
		struct UV
		{
			float fU;
			float fV;
		} m_UV[1];
	};

    int                         m_iGlobalInterfaceAlpha; ///< globale Transparenz des Interface
    int                         m_iBrightness;          ///< Helligkeit nach Zeichnen des Overlays


	int							m_iWidth;				///< Breite der Zeichenfläche
	int							m_iHeight;				///< Höhe der Zeichenfläche
	CRct						m_rctSize;				///< Clipping-Rechteck; (0, 0, Devicebreite, Devicehöhe)
	int							m_iScreenWidth;			///< Größe des Bildschirms in Pixeln
	int							m_iScreenHeight;		///< Größe des Bildschirms in Pixeln

	float						m_fTranslationX;		///< Translation in X-Richtung in Pixeln
	float						m_fTranslationY;		///< Translation in Y-Richtung in Pixeln
	float						m_fScaleX;				///< Scaling der X-Achse (Faktor)
	float						m_fScaleY;				///< Scaling der Y-Achse (Faktor)
	float						m_fPivotX;				///< Zentrum der Rotation/Skalierung (Pixel-Koordinaten)
	float						m_fPivotY;				///< Zentrum der Rotation/Skalierung (Pixel-Koordinaten)
	float						m_fRotationAngle;		///< Rotationswinkel (0 = normal)

	CLinearInterpolator*		m_pxInterpolatorX;
	CLinearInterpolator*		m_pxInterpolatorY;
	CLinearInterpolator*		m_pxInterpolatorSX;
	CLinearInterpolator*		m_pxInterpolatorSY;
	CLinearInterpolator*		m_pxInterpolatorAlpha;
	CLinearInterpolator*		m_pxInterpolatorBrightness;
	CLinearInterpolator*		m_pxInterpolatorRotationAngle;

	int							m_iPitch;				///< Pitch (= tatsächliche Breite) der gelockten Surface in Pixeln
	unsigned long*				m_pdwPixels;			///< Zeiger auf die Pixel (solange die Surface gelockt ist)

	IDirect3DDevice9*			m_pxD3DDevice;			///< Direct3D Device
	IDirect3DTexture9*			m_pxBackBuffer;			///< große Textur, Device malt darauf und kopiert dann in kleinere
	IDirect3DVertexBuffer9*		m_pxVertexBuffer;		///< Vertex buffer 
	IDirect3DIndexBuffer9*		m_pxIndexBuffer;		///< Index buffer 
    IDirect3DVertexShader9*     m_pxBlendVertexShader;  ///< Vertex shader
    IDirect3DVertexDeclaration9* m_pxVertexDeclaration; ///< Vertex declaration

	CDynArray<TQuad>			m_apxTiles;				///< Array mit texturierten Quads, die zusammen das Device ergeben

	int							m_iHTileSize;			///< Breite eines Quads in Pixeln
	int							m_iVTileSize;			///< Höhe eines Quads in Pixeln
	int							m_iHTiles;				///< Anzahl Quads pro Zeile
	int							m_iVTiles;				///< Anzahl Quads pro Spalte

	CRctList					m_xTransparentParts;	///< RectList mit Tranparenten Teilen des Devices

	void						InitDevice();
	void						ShutDevice();

	// Markiert ein Quad als "dirty", d.h. die Textur muss neu geschrieben werden
	void						MarkDirty(int p_iX, int p_iY, const CColor& p_xColor);

	// Markiert ein Quad als "dirty", d.h. die Textur muss neu geschrieben werden
	void						MarkDirty(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor);
}; 

} // namespace UILib

#endif // ifndef UILIB_DIRECTX9CONTEXT_H_INCLUDED
