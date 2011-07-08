#include "stdafx.h"
#include "uilib/core/outputdevice.h"
#include "uilib/core/bitmap.h"
#include "uilib/core/bitmapfont.h"

static UILib::CFontHandle		g_hFont;				// uilib font
static UILib::CBitmap*			g_pxBitmap[5];			
static UILib::CFontHandle		g_hBmpFont;	


void TestContext_OnInit(UILib::COutputDevice* p_pxUIDevice)
{
	g_hFont = p_pxUIDevice->CreateFont(70, "Times New Roman");
	//g_pxBitmap[0] = new UILib::CBitmap("red.png");
	//g_pxBitmap[1] = new UILib::CBitmap("green.png");
	//g_pxBitmap[2] = new UILib::CBitmap("blue.png");
	//g_pxBitmap[3] = new UILib::CBitmap("alpha.png");
	g_pxBitmap[4] = new UILib::CBitmap("uilibtest.png");

	g_hBmpFont = UILib::CBitmapFont::Create("bitmapfont.png", 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜßabcdefghijklmnopqrstuvwxyzäöü+-=<>^§$&%{}[]().,;:?!_#*~\\/´'|\"@1234567890", 4, 17);
}


void TestContext_OnPaint(UILib::COutputDevice* p_pxUIDevice)
{
	p_pxUIDevice->BeginPaint();

	int iX = rand() % 150;
	int iY = rand() % 200;
	int iH = rand() % 100;
	int iW = rand() % 50;
	int iR = rand() % 255;
	int iG = rand() % 255;
	int iB = rand() % 255;

	p_pxUIDevice->DrawRect(200+iX, iY, 200+iX+iW, iY+iH, CColor(iR, iG, iB));
	p_pxUIDevice->FillRect(iX, iY, iX+iW, iY+iH, CColor(iR, iG, iB));

	int iX1 = rand() % 200;
	int iY1 = rand() % 300;
	int iX2 = rand() % 200;
	int iY2 = rand() % 300;

	p_pxUIDevice->SetPixel(iX1, 300+iY1, CColor(iR, iG, iB));
	p_pxUIDevice->DrawLine(200+iX1, 300+iY1, 200+iX2, 300+iY2, CColor(iR, iG, iB));


/*
	p_pxUIDevice->FillRect(10, 10, 100, 500, CColor(0, 255, 0));
	p_pxUIDevice->FillRect(400, 10, 490, 500, CColor(0, 0, 255));

	p_pxUIDevice->FillRect(10, 10, 550, 100, CColor(255, 0, 0));
	p_pxUIDevice->DrawLine(10, 10, 700, 200, CColor(255, 255, 255));
*/
	p_pxUIDevice->DrawText(g_hFont, 420, 100, "Hello UILib!", CColor(255, 255, 0));
	p_pxUIDevice->DrawText(g_hBmpFont, 420, 150, "Hello UILib!", CColor(255, 255, 0));

	for(int i=0; i<4; ++i)
	{
//		p_pxUIDevice->Blit(CPnt(320 + 20*i, 400+20*i), g_pxBitmap[i], false);
	}
	p_pxUIDevice->FillRect(580, 490, 580 + g_pxBitmap[4]->GetWidth(), 490 + g_pxBitmap[4]->GetHeight(), CColor(0, 0, 0)); 
	p_pxUIDevice->Blit(CPnt(580, 490), g_pxBitmap[4], true);

	p_pxUIDevice->Blit(CPnt(580, 370), g_pxBitmap[4], false);

	p_pxUIDevice->EndPaint();
}