#include "stdafx.h"
#include "uilib/core/outputdevice.h"
#include "uilib/core/bitmap.h"
#include "uilib/core/bitmapfont.h"
#include "uilib/core/paintcontext.h"

class CTestPaintContext : public UILib::CPaintContext
{
public:
	CTestPaintContext(UILib::COutputDevice* p_pxDevice) : UILib::CPaintContext(p_pxDevice) {};
	~CTestPaintContext() {};
	
	void SetOffset(CPnt p_pntOffset)	{ __super::SetOffset(p_pntOffset); }
	void SetClip(CRct p_xClipRect)		{ __super::SetClip(p_xClipRect); }
};


enum TestWhat
{
	T_SetPixel	= 1 << 0,
	T_DrawLine	= 1 << 1,
	T_DrawRect  = 1 << 2,
	T_FillRect  = 1 << 3,
	T_TTFont	= 1 << 4,
	T_BMPFont	= 1 << 5,
	T_Blit		= 1 << 6,
};


static UILib::CFontHandle		g_hFont;				// uilib font
static UILib::CBitmap*			g_pxBitmap;			
static UILib::CFontHandle		g_hBmpFont;	
static CTestPaintContext*		g_pxPaintCtx;
static int						g_iTests;



void TestClipping_OnInit(UILib::COutputDevice* p_pxUIDevice)
{
	g_hFont = p_pxUIDevice->CreateFont(35, "Times New Roman");
	g_pxBitmap = new UILib::CBitmap("uilibtest.png");

	g_hBmpFont = UILib::CBitmapFont::Create("bitmapfont.png", 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜßabcdefghijklmnopqrstuvwxyzäöü+-=<>^§$&%{}[]().,;:?!_#*~\\/´'|\"@1234567890", 4, 17);

	g_pxPaintCtx = new CTestPaintContext(p_pxUIDevice);

	g_pxPaintCtx->SetClip(CRct(200, 200, 600, 400));
	g_pxPaintCtx->SetOffset(CPnt(50, 50));

	g_iTests = 0xFFFF;
}


void TestClipping_OnPaint(UILib::COutputDevice* p_pxUIDevice)
{
	p_pxUIDevice->BeginPaint();

	CRct r = g_pxPaintCtx->GetAbsClipRect();
	p_pxUIDevice->DrawRect(r.left - 1, r.top-1, r.right+1, r.bottom+1, CColor(255, 255, 255, 255));

	int iX = rand() % 800;
	int iY = rand() % 600;
	int iH = rand() % (800 - iX);
	int iW = rand() % (600 - iY);
	int iR = rand() % 255;
	int iG = rand() % 255;
	int iB = rand() % 255;

	g_pxPaintCtx->DrawRect(CRct(iX, iY, iX+iW, iY+iH), CColor(iR, iG, iB));
	g_pxPaintCtx->FillRect(iX, iY, iX+iW, iY+iH, CColor(iR, iG, iB));
	g_pxPaintCtx->SetPixel(iX, iY, CColor(iR, iG, iB));
	g_pxPaintCtx->DrawLine(iX, iY, iX+iW, iY+iH, CColor(iR, iG, iB));
	g_pxPaintCtx->Blit(CPnt(iX, iY), g_pxBitmap, true);
	g_pxPaintCtx->DrawText(g_hFont, CPnt(iX, iY), "Clipping Test!", CColor(iR, iG, iB));
	g_pxPaintCtx->DrawText(g_hBmpFont, CPnt(iX, iY), "Clipping Test!", CColor(iR, iG, iB));


	p_pxUIDevice->EndPaint();
}