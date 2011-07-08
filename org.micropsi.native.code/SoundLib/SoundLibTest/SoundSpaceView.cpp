#include "stdafx.h"
#include "SoundLibTest.h"

#include "SoundLib/SoundSystem.h"

#include "SoundSpaceView.h"

using namespace SoundLib;

// CSoundSpaceView

IMPLEMENT_DYNAMIC(CSoundSpaceView, CWnd)
#define SOUNDSPACEVIEW_CLASSNAME    _T("MFCSoundSpaceViewCtrl")  // Window class name

//---------------------------------------------------------------------------------------------------------------------
CSoundSpaceView::CSoundSpaceView()
{
	RegisterWindowClass();

	m_iXUnits = 100;
	m_iYUnits = 100;

}

//---------------------------------------------------------------------------------------------------------------------
CSoundSpaceView::~CSoundSpaceView()
{
}

//---------------------------------------------------------------------------------------------------------------------
BOOL 
CSoundSpaceView::Create(CWnd* pParentWnd, const RECT& rect, UINT nID, DWORD dwStyle /*=WS_VISIBLE*/)
{
	return CWnd::Create(SOUNDSPACEVIEW_CLASSNAME, _T(""), dwStyle, rect, pParentWnd, nID);
}

//---------------------------------------------------------------------------------------------------------------------
BOOL 
CSoundSpaceView::RegisterWindowClass()
{
    WNDCLASS wndcls;
    HINSTANCE hInst = AfxGetResourceHandle();

    if (!(::GetClassInfo(hInst, SOUNDSPACEVIEW_CLASSNAME, &wndcls)))
    {
        // otherwise we need to register a new class
        wndcls.style            = CS_DBLCLKS | CS_HREDRAW | CS_VREDRAW;
        wndcls.lpfnWndProc      = ::DefWindowProc;
        wndcls.cbClsExtra       = wndcls.cbWndExtra = 0;
        wndcls.hInstance        = hInst;
        wndcls.hIcon            = NULL;
        wndcls.hCursor          = AfxGetApp()->LoadStandardCursor(IDC_ARROW);
        wndcls.hbrBackground    = (HBRUSH) (COLOR_3DFACE + 1);
        wndcls.lpszMenuName     = NULL;
        wndcls.lpszClassName    = SOUNDSPACEVIEW_CLASSNAME;

        if (!AfxRegisterClass(&wndcls))
        {
            AfxThrowResourceException();
            return FALSE;
        }
    }

    return TRUE;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundSpaceView::OnPaint()
{
	CPaintDC dc(this); // device context for painting

//	CBrush xWhiteBrush(RGB(255, 255, 255));

	DrawCoordinateCross(dc);

	for(int i=0; i<CSoundSystem::Get().GetNumListeners(); ++i)
	{
		float fX, fY, fZ;
		CSoundSystem::Get().GetListenerPos(fX, fY, fZ);
		DrawListener(dc, fX, fY, 1.0f, 0.0f);
	}

}

//---------------------------------------------------------------------------------------------------------------------
void
CSoundSpaceView::DrawCoordinateCross(CPaintDC& dc)
{
	CRect xClientRect;
	GetClientRect(&xClientRect);

	CPen xGridPen(PS_SOLID, 1, RGB(0, 0, 200));
	dc.SelectObject(xGridPen);

	dc.SetBkMode(TRANSPARENT);
	dc.SetTextColor(RGB(0, 0, 200));


	for(int x = -m_iXUnits/2; x<= m_iXUnits/2; x += 10)
	{
		POINT p = ToDeviceCoordinates((float) x, 0);
		dc.MoveTo(p.x, xClientRect.top);
		dc.LineTo(p.x, xClientRect.bottom);

		CString s;
		s.Format("%d", x);
		dc.TextOut(p.x + 1, xClientRect.bottom - 20, s);
	}

	for(int y = -m_iYUnits/2; y<= m_iYUnits/2; y += 10)
	{
		POINT p = ToDeviceCoordinates(0, (float) y);
		dc.MoveTo(xClientRect.left, p.y);
		dc.LineTo(xClientRect.right, p.y);

		CString s;
		s.Format("%d", y);
		dc.TextOut(xClientRect.left +1, p.y + 1, s);
	}

	dc.SetTextColor(RGB(255, 255, 255));
	dc.TextOut(xClientRect.right - 20 , xClientRect.bottom - 20, "x");
	dc.TextOut(xClientRect.left + 20 , xClientRect.top + 1, "y");
}

//---------------------------------------------------------------------------------------------------------------------
void
CSoundSpaceView::DrawListener(CPaintDC& dc, float fX, float fY, float fDirX, float fDirY)
{
	CBrush xRedBrush(RGB(255, 0, 0));
	dc.SelectObject(xRedBrush);

	POINT p = ToDeviceCoordinates(fX, fY);
	dc.Ellipse(p.x-5, p.y-5, p.x+5, p.y+5);
}

//---------------------------------------------------------------------------------------------------------------------
POINT 
CSoundSpaceView::ToDeviceCoordinates(float fX, float fY) const
{
	CRect xClientRect;
	GetClientRect(&xClientRect);

	float fXUnitInPixels = (float) xClientRect.Width() / m_iXUnits;
	float fYUnitInPixels = (float) xClientRect.Height() / m_iYUnits;
	CPoint p = CPoint((int) (fX * fXUnitInPixels), (int) (fY * fYUnitInPixels));
	return p + xClientRect.CenterPoint();
}

//---------------------------------------------------------------------------------------------------------------------
BOOL
CSoundSpaceView::OnEraseBkgnd(CDC* pDC)
{
	CBrush xBrush(RGB(0, 0, 70));

	RECT r;
	GetClientRect(&r);

	pDC->FillRect(&r, &xBrush);

	return TRUE;
}

//---------------------------------------------------------------------------------------------------------------------

BEGIN_MESSAGE_MAP(CSoundSpaceView, CWnd)
	ON_WM_PAINT()
	ON_WM_ERASEBKGND()
END_MESSAGE_MAP()

//---------------------------------------------------------------------------------------------------------------------

