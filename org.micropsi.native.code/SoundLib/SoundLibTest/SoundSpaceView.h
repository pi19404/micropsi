#pragma once

// CSoundSpaceView

class CSoundSpaceView : public CWnd
{
	DECLARE_DYNAMIC(CSoundSpaceView)

public:
	CSoundSpaceView();
	virtual ~CSoundSpaceView();

	static BOOL RegisterWindowClass();
	BOOL Create(CWnd* pParentWnd, const RECT& rect, UINT nID, DWORD dwStyle = WS_VISIBLE);

	afx_msg void OnPaint();
	afx_msg BOOL OnEraseBkgnd(CDC* pDC);

	int		m_iXUnits;
	int		m_iYUnits;

protected:

	POINT	ToDeviceCoordinates(float fX, float fY) const;
	void	DrawListener(CPaintDC& dc, float fX, float fY, float fDirX, float fDirY);
	void	DrawCoordinateCross(CPaintDC& dc);

	DECLARE_MESSAGE_MAP()
};


