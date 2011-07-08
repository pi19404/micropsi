
#include "stdafx.h"
#include "uilib/core/bitmap.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"
#include "uilib/controls/messagebox.h"

namespace UILib
{

const char* CMessageBox::ms_pcText_OK		= "OK";
const char* CMessageBox::ms_pcText_CANCEL	= "Cancel";
const char* CMessageBox::ms_pcText_YES		= "Yes";
const char* CMessageBox::ms_pcText_NO		= "No";
const char* CMessageBox::ms_pcText_RETRY	= "Retry";
const char* CMessageBox::ms_pcText_IGNORE	= "Ignore";

const unsigned int CMessageBox::ms_iButtonMask	= MBB_OK | MBB_OKCANCEL	| MBB_YESNO	| MBB_RETRYCANCEL | MBB_RETRYIGNORECANCEL;
const unsigned int CMessageBox::ms_iIconMask	= MBI_ICONINFO | MBI_ICONEXCLAMATION | MBI_ICONWARNING;

const int CMessageBox::m_iSpacing		= 10;

//---------------------------------------------------------------------------------------------------------------------
CMessageBox::CMessageBox(const CStr& p_rsCaption, const CStr& p_rsText, unsigned int p_iProperties, 
						WHDL p_hMessageTargetWindow, CFunctionPointer1<MsgBoxResults> p_xCallback)
{
	CWindowMgr::Get().SetModal(this);

	m_iButtonCount = 0;
	m_iMBoxProps = p_iProperties;
	m_hMessageTargetWindow = p_hMessageTargetWindow;
	m_xCallback = p_xCallback;

	m_pxText = CLabel::Create();
	m_pxText->SetText(p_rsText);

	m_pxIcon = 0;
	m_pxButtonOKYESRETRY = 0;
	m_pxButtonNOCANCEL = 0;
	m_pxButtonIGNORE = 0;

	switch(m_iMBoxProps & ms_iButtonMask)
	{
	case MBB_OKCANCEL:
		m_pxButtonOKYESRETRY = CButton::Create(); 
		m_pxButtonOKYESRETRY->SetText(ms_pcText_OK);	
		m_iButtonCount++;
		m_pxButtonNOCANCEL = CButton::Create(); 
		m_pxButtonNOCANCEL->SetText(ms_pcText_CANCEL);	
		m_iButtonCount = 2;
		break;

	case MBB_YESNO:
		m_pxButtonOKYESRETRY = CButton::Create(); 
		m_pxButtonOKYESRETRY->SetText(ms_pcText_YES);	
		m_pxButtonNOCANCEL = CButton::Create(); 
		m_pxButtonNOCANCEL->SetText(ms_pcText_NO);		
		m_iButtonCount = 2;
		break;

	case MBB_RETRYCANCEL:
		m_pxButtonOKYESRETRY = CButton::Create(); 
		m_pxButtonOKYESRETRY->SetText(ms_pcText_RETRY);	
		m_pxButtonNOCANCEL = CButton::Create(); 
		m_pxButtonNOCANCEL->SetText(ms_pcText_CANCEL);
		m_iButtonCount = 2;
		break;

	case MBB_RETRYIGNORECANCEL:
		m_pxButtonOKYESRETRY = CButton::Create(); 
		m_pxButtonOKYESRETRY->SetText(ms_pcText_RETRY);
		m_pxButtonIGNORE = CButton::Create(); 
		m_pxButtonIGNORE->SetText(ms_pcText_IGNORE);	
		m_pxButtonNOCANCEL = CButton::Create();
		m_pxButtonNOCANCEL->SetText(ms_pcText_CANCEL);
		m_iButtonCount = 3;
		break;

	case MBB_OK:
	default:
		m_pxButtonOKYESRETRY = CButton::Create(); 
		m_pxButtonOKYESRETRY->SetText(ms_pcText_OK);	
		m_iButtonCount = 1;
		break;
	}

	switch (m_iMBoxProps & ms_iIconMask)
	{
	case MBI_ICONEXCLAMATION:
		m_pxIcon = CLabel::Create();
		m_pxIcon->SetBitmap("std_iconexclamation.png");
		break;

	case MBI_ICONWARNING:
		m_pxIcon = CLabel::Create();
		m_pxIcon->SetBitmap("std_iconwarning.png");
		break;

	default:
	case MBI_ICONINFO:
		m_pxIcon = CLabel::Create();
		m_pxIcon->SetBitmap("std_iconinfo.png");
		break;
	}

	m_pxIcon->SetTransparent();

	AddChild(m_pxText->GetWHDL());
	AddChild(m_pxButtonOKYESRETRY->GetWHDL());
	if (m_pxButtonNOCANCEL)
	{
		AddChild(m_pxButtonNOCANCEL->GetWHDL());
	}
	if (m_pxButtonIGNORE)
	{
		AddChild(m_pxButtonIGNORE->GetWHDL());
	}
	if (m_pxIcon)
	{
		AddChild(m_pxIcon->GetWHDL());
	}

	WHDL WHDL = GetWHDL();
	CWindowMgr& wm = CWindowMgr::Get();
	wm.AddTopLevelWindow(WHDL);
	wm.BringWindowToTop(WHDL);

	SetCaption(p_rsCaption);
}

//---------------------------------------------------------------------------------------------------------------------
CMessageBox::~CMessageBox()
{
	CWindowMgr::Get().ReleaseModal(this);
}

//---------------------------------------------------------------------------------------------------------------------
CMessageBox*
CMessageBox::Create(const CStr& p_rsCaption, const CStr& p_rsText, const unsigned int p_iProperties)
{
	return new CMessageBox(p_rsCaption, p_rsText, p_iProperties, 0, CFunctionPointer1<MsgBoxResults>());
}

//---------------------------------------------------------------------------------------------------------------------
CMessageBox* 
CMessageBox::Create(const CStr& p_rsCaption, const CStr& p_rsText, unsigned int p_iProperties, WHDL p_hMessageTargetWindow)
{
	return new CMessageBox(p_rsCaption, p_rsText, p_iProperties, p_hMessageTargetWindow, CFunctionPointer1<MsgBoxResults>());
}

//---------------------------------------------------------------------------------------------------------------------
CMessageBox* 
CMessageBox::Create(const CStr& p_rsCaption, const CStr& p_rsText, unsigned int p_iProperties, CFunctionPointer1<MsgBoxResults> p_xCallback)
{
	return new CMessageBox(p_rsCaption, p_rsText, p_iProperties, 0, p_xCallback);
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CMessageBox::HandleMsg(const CMessage& p_rxMessage)
{
	MsgBoxResults iResult = ID_ERROR;

	if(p_rxMessage == msgButtonClicked)
	{
		switch(m_iMBoxProps & ms_iButtonMask)
		{
		case MBB_OK:
			if(p_rxMessage.GetWindow() == m_pxButtonOKYESRETRY->GetWHDL())
			{
				iResult = ID_OK;
			}
			break;

		case MBB_OKCANCEL:
			if(p_rxMessage.GetWindow() == m_pxButtonOKYESRETRY->GetWHDL())
			{
				iResult = ID_OK;
			}
			else if(p_rxMessage.GetWindow() == m_pxButtonNOCANCEL->GetWHDL())
			{
				iResult = ID_CANCEL;
			}
			break;

		case MBB_YESNO:
			if(p_rxMessage.GetWindow() == m_pxButtonOKYESRETRY->GetWHDL())
			{
				iResult = ID_YES;
			}
			else if(p_rxMessage.GetWindow() == m_pxButtonNOCANCEL->GetWHDL())
			{
				iResult = ID_NO;
			}
			break;

		case MBB_RETRYCANCEL:
			if(p_rxMessage.GetWindow() == m_pxButtonOKYESRETRY->GetWHDL())
			{
				iResult = ID_RETRY;
			}
			else if(p_rxMessage.GetWindow() == m_pxButtonNOCANCEL->GetWHDL())
			{
				iResult = ID_CANCEL;
			}
			break;

		case MBB_RETRYIGNORECANCEL:
			if(p_rxMessage.GetWindow() == m_pxButtonOKYESRETRY->GetWHDL())
			{
				iResult = ID_RETRY;
			}
			else if(p_rxMessage.GetWindow() == m_pxButtonNOCANCEL->GetWHDL())
			{
				iResult = ID_CANCEL;
			}
			else if(p_rxMessage.GetWindow() == m_pxButtonIGNORE->GetWHDL())
			{
				iResult = ID_IGNORE;
			}
			break;

		default:
			iResult = ID_ERROR;
			break;
		}

		OnDecision(iResult);
		Destroy();	
	}

	return __super::HandleMsg(p_rxMessage);
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CMessageBox::OnVisualizationChange()
{
	int iTitleBarHeight = 0;
	CSize xButSize = CSize(50,20);
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v = CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		iTitleBarHeight = v->GetMetrics()->m_iTitleBarHeight;
		xButSize		= v->GetMetrics()->m_xStdButtonSize;
	}

	CSize xRootWndSize = GetRootWindow()->GetSize();

	m_pxIcon->AutoSize();
	m_pxText->AutoSize();

	m_pxButtonOKYESRETRY->SetSize(xButSize.cx,xButSize.cy);
	if (m_pxButtonNOCANCEL)
	{
		m_pxButtonNOCANCEL->SetSize(xButSize.cx,xButSize.cy);
	}
	if (m_pxButtonIGNORE)
	{
		m_pxButtonIGNORE->SetSize(xButSize.cx,xButSize.cy);
	}

	CSize xWndSize, xIconSize = m_pxIcon->GetSize();
	xWndSize.cx = m_iSpacing + xIconSize.cx + m_iSpacing + m_pxText->GetSize().cx + 2*m_iSpacing;
	xWndSize.cy = iTitleBarHeight + m_iSpacing + max(xIconSize.cy,m_pxText->GetSize().cy) + m_iSpacing + xButSize.cy + m_iSpacing;

    SetSize(xWndSize);
	AssureMinSize(CSize(3*xButSize.cx + 4*m_iSpacing, xWndSize.cy));
	AssureMaxSize(CSize(xRootWndSize.cx*3/4, xWndSize.cy));

	xWndSize = GetSize();

	m_pxIcon->SetPos(m_iSpacing, m_iSpacing);
	m_pxText->AssureMinSize(CSize(xWndSize.cx - xIconSize.cx - 4*m_iSpacing - (xIconSize.cx+2*m_iSpacing)/2, xIconSize.cy));
	m_pxText->SetPos(m_iSpacing + xIconSize.cx + m_iSpacing, m_iSpacing);

	int iPosY = (xWndSize.cy - iTitleBarHeight) - m_iSpacing - xButSize.cy;
	switch(m_iButtonCount)
	{
	case 1:
		m_pxButtonOKYESRETRY->SetPos(xWndSize.cx/2 - xButSize.cx/2, iPosY);
		break;
	case 2:
		m_pxButtonOKYESRETRY->SetPos(xWndSize.cx/2 - xButSize.cx - m_iSpacing/2, iPosY);
		m_pxButtonNOCANCEL->SetPos(xWndSize.cx/2 + m_iSpacing/2, iPosY);
		break;
	case 3:
		m_pxButtonOKYESRETRY->SetPos(xWndSize.cx/2 - xButSize.cx*3/2 - m_iSpacing, iPosY);
		m_pxButtonIGNORE->SetPos(xWndSize.cx/2 - xButSize.cx/2, iPosY);
		m_pxButtonNOCANCEL->SetPos(xWndSize.cx/2 + xButSize.cx/2 + m_iSpacing, iPosY);
		break;
	default:
		break;
	}

	SetPos(xRootWndSize.cx/2 - xWndSize.cx/2, xRootWndSize.cy/2 - xWndSize.cy/2);

	return __super::OnVisualizationChange();
}

//---------------------------------------------------------------------------------------------------------------------
CStr
CMessageBox::GetDebugString() const		
{ 
	return "CMessageBox"; 
}

//---------------------------------------------------------------------------------------------------------------------
void
CMessageBox::OnDecision(MsgBoxResults eResult)
{
	if(m_hMessageTargetWindow != WHDL::InvalidHandle())
	{
		CWindowMgr::Get().PostMsg(CMessageBoxDecisionMsg((int) eResult), m_hMessageTargetWindow);
	}
	if(m_xCallback)
	{
		m_xCallback(eResult);
	}
}
//---------------------------------------------------------------------------------------------------------------------

} // namespace UILib

