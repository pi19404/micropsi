#include "stdafx.h"
#include "SoundLibTest.h"
#include "DirectionControl.h"
#include "math.h"

#define PI 3.14159f 

// CDirectionControl

IMPLEMENT_DYNAMIC(CDirectionControl, CWnd)
#define DIRECTIONCONTROL_CLASSNAME    _T("MFCDirectionControl")  // Window class name

//---------------------------------------------------------------------------------------------------------------------
CDirectionControl::CDirectionControl()
{
	RegisterWindowClass();

	m_fAngleInRadiant = 0.0f;
	m_bDragging = false;
}

//---------------------------------------------------------------------------------------------------------------------
CDirectionControl::~CDirectionControl()
{
}

//---------------------------------------------------------------------------------------------------------------------
BOOL 
CDirectionControl::Create(CWnd* pParentWnd, const RECT& rect, UINT nID, DWORD dwStyle /*=WS_VISIBLE*/)
{
	return CWnd::Create(DIRECTIONCONTROL_CLASSNAME, _T(""), dwStyle, rect, pParentWnd, nID);
}

//---------------------------------------------------------------------------------------------------------------------
BOOL 
CDirectionControl::RegisterWindowClass()
{
    WNDCLASS wndcls;
    HINSTANCE hInst = AfxGetResourceHandle();

    if (!(::GetClassInfo(hInst, DIRECTIONCONTROL_CLASSNAME, &wndcls)))
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
        wndcls.lpszClassName    = DIRECTIONCONTROL_CLASSNAME;

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
CDirectionControl::OnPaint()
{
	CPaintDC dc(this); // device context for painting

	CRect xClientRect;
	GetClientRect(&xClientRect);

	int iRadius = min(xClientRect.Width(), xClientRect.Height()) / 2;
	CPoint xCenter(xClientRect.Width() /2 , xClientRect.Height() /2);

	CBrush xWhiteBrush(RGB(255, 255, 255));
	CBrush xGrayBrush(RGB(220, 220, 220));
	if(m_bDragging)
	{
		dc.SelectObject(&xGrayBrush);
	}
	else
	{
		dc.SelectObject(&xWhiteBrush);
	}
	CRect xCircleRect(xCenter.x - iRadius, xCenter.y -iRadius, xCenter.x + iRadius, xCenter.y +iRadius);
	dc.Ellipse(&xCircleRect);

	CBrush xRedBrush(RGB(255, 0, 0));
	dc.SelectObject(&xRedBrush);
	CRect xCenterCircle(xCenter.x - 2, xCenter.y -2, xCenter.x + 3, xCenter.y +3);
	dc.Ellipse(&xCenterCircle);

	int x = xCenter.x + (int) ((float) iRadius * sinf(m_fAngleInRadiant));
	int y = xCenter.y + (int) ((float) iRadius * cosf(m_fAngleInRadiant));
	dc.MoveTo(xCenter);
	dc.LineTo(x, y);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CDirectionControl::OnMouseMove(UINT nFlags, CPoint point)
{
	if(m_bDragging)
	{
		SetFromPoint(point);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CDirectionControl::OnLButtonDown(UINT nFlags, CPoint point)
{
	m_bDragging = true;
	SetCapture();
	SetFromPoint(point);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CDirectionControl::OnLButtonUp(UINT nFlags, CPoint point)
{
	if(m_bDragging)
	{
		ReleaseCapture();
		m_bDragging = false;
		Invalidate();
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CDirectionControl::SetFromPoint(CPoint point)
{
	CRect xClientRect;
	GetClientRect(&xClientRect);

	int iRadius = min(xClientRect.Width(), xClientRect.Height()) / 2;
	CPoint xCenter(xClientRect.Width() /2 , xClientRect.Height() /2);

	SetVector((float) (point.x - xCenter.x), (float) (point.y - xCenter.y));
}

//---------------------------------------------------------------------------------------------------------------------
// 0 = 6 o'clock, CCW 
float
CDirectionControl::GetAngle()
{
	return m_fAngleInRadiant;
}

//---------------------------------------------------------------------------------------------------------------------
// liefert 2D-Vector
void	
CDirectionControl::GetVector(float& fX, float& fY)
{
	fX = sinf(m_fAngleInRadiant);
	fY = cosf(m_fAngleInRadiant);
}

//---------------------------------------------------------------------------------------------------------------------
void
CDirectionControl::SetVector(float fX, float fY)
{
	if(fX == 0.0f  &&  fY == 0.0f)
	{
		return;
	}

	float fAngle = atanf(fX / fY);
	if(fY < 0)
	{
		fAngle += PI;
	}
	fAngle = fmodf(fAngle, PI * 2.0f);
	if(fAngle != m_fAngleInRadiant)
	{
		m_fAngleInRadiant = fAngle;

		NMHDR nmhdr;
		nmhdr.hwndFrom = m_hWnd;
		nmhdr.idFrom = GetDlgCtrlID();
		nmhdr.code = FN_DIRECTIONCHANGED;
		GetOwner()->SendMessage(WM_NOTIFY, GetDlgCtrlID(), (LPARAM) &nmhdr);

		Invalidate();
	}
}

//---------------------------------------------------------------------------------------------------------------------

BEGIN_MESSAGE_MAP(CDirectionControl, CWnd)
	ON_WM_PAINT()
	ON_WM_ERASEBKGND()
	ON_WM_MOUSEMOVE()
	ON_WM_LBUTTONDOWN()
	ON_WM_LBUTTONUP()
END_MESSAGE_MAP()

//---------------------------------------------------------------------------------------------------------------------

