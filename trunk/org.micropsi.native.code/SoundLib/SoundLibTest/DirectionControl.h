#pragma once

// CDirectionControl

// format notification
#define FN_DIRECTIONCHANGED 0x01000

class CDirectionControl : public CWnd
{
	DECLARE_DYNAMIC(CDirectionControl)

public:
	CDirectionControl();
	virtual ~CDirectionControl();

	static BOOL RegisterWindowClass();
	BOOL Create(CWnd* pParentWnd, const RECT& rect, UINT nID, DWORD dwStyle = WS_VISIBLE);

	void	SetVector(float fX, float fY); 

	// 0 = 6 o'clock, CCW 
	float	GetAngle();

	// liefert 2D-Vector
	void	GetVector(float& fX, float& fY);

protected:

	void SetFromPoint(CPoint point);

	afx_msg void OnPaint();
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
	afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
	afx_msg void OnLButtonDown(UINT nFlags, CPoint point);

	float m_fAngleInRadiant;
	bool  m_bDragging;

protected:
	DECLARE_MESSAGE_MAP()
};


