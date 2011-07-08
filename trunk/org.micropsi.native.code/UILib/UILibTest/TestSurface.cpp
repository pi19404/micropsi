#include "stdafx.h"
#include "uilib/core/outputdevice.h"


void TestSurface_OnInit(UILib::COutputDevice* p_pxUIDevice)
{
}


void TestSurface_OnPaint(UILib::COutputDevice* p_pxUIDevice)
{
	p_pxUIDevice->BeginPaint();

	int x, y;
	for(x=0; x<p_pxUIDevice->GetSize().cx; x += 20)
	{
		p_pxUIDevice->DrawLine(x, 0, x, p_pxUIDevice->GetSize().cy, CColor(255, 50, 50, 255)); 
	}
	for(y=0; y<p_pxUIDevice->GetSize().cy; y += 20)
	{
		p_pxUIDevice->DrawLine(0, y, p_pxUIDevice->GetSize().cx, y, CColor(50, 255, 50, 255)); 
	}


	p_pxUIDevice->EndPaint();
}