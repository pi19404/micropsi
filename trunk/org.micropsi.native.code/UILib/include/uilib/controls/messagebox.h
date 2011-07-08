
#pragma once 
#ifndef UILIB_MESSAGEBOX_H_INCLUDED 
#define UILIB_MESSAGEBOX_H_INCLUDED 

#include "uilib/controls/dialogwindow.h"
#include "uilib/controls/label.h"
#include "uilib/controls/button.h"

namespace UILib
{

class CMessageBox : public CDialogWindow
{
public:

	enum MessageBoxProperties
	{
		// Icons
		MBI_ICONINFO			= 1 << 0,
		MBI_ICONEXCLAMATION		= 1 << 1,
		MBI_ICONWARNING			= 1 << 2,

		// Buttons
		MBB_OK					= 1 << 8,
		MBB_OKCANCEL			= 1 << 9,
		MBB_YESNO				= 1 << 10,
		MBB_RETRYCANCEL			= 1 << 11,
		MBB_RETRYIGNORECANCEL	= 1 << 12
	};

	enum MsgBoxResults
	{
		ID_ERROR,
		ID_OK,
		ID_CANCEL,
		ID_YES,
		ID_NO,
		ID_RETRY,
		ID_IGNORE
	};

	virtual void OnDecision(MsgBoxResults eResult);

	static CMessageBox* Create(const CStr& p_rsCaption, const CStr& p_rsText, 
		unsigned int p_iProperties = MBB_OK | MBI_ICONINFO);

	static CMessageBox* Create(const CStr& p_rsCaption, const CStr& p_rsText, 
		unsigned int p_iProperties, WHDL p_hMessageTargetWindow);

	static CMessageBox* Create(const CStr& p_rsCaption, const CStr& p_rsText, 
		unsigned int p_iProperties, CFunctionPointer1<MsgBoxResults> p_xCallback);

protected:

	CMessageBox(const CStr& p_rsCaption, const CStr& p_rsText, unsigned int p_iProps, 
				WHDL p_hMessageTargetWindow, CFunctionPointer1<MsgBoxResults> p_xCallback);
	virtual ~CMessageBox();

	virtual bool	HandleMsg(const CMessage& p_rxMessage);
	virtual bool	OnVisualizationChange();

	virtual CStr	GetDebugString() const;

private:
	static const char*	ms_pcText_OK;
	static const char*	ms_pcText_CANCEL;
	static const char*	ms_pcText_YES;
	static const char*	ms_pcText_NO;
	static const char*	ms_pcText_RETRY;
	static const char*	ms_pcText_IGNORE;

	static const unsigned int ms_iButtonMask;
	static const unsigned int ms_iIconMask;

	CLabel*				m_pxText;
	CLabel*				m_pxIcon;
	CButton*			m_pxButtonOKYESRETRY;
	CButton*			m_pxButtonNOCANCEL;
	CButton*			m_pxButtonIGNORE;

	unsigned int		m_iMBoxProps;

	// helper vars
	static const int					m_iSpacing;
	int									m_iButtonCount;
	WHDL								m_hMessageTargetWindow;		///< Zielfenster für Decision-Message (optional)
	CFunctionPointer1<MsgBoxResults>	m_xCallback;				///< Callbackfunktion für Entscheidung

	CMessageBox(const CMessageBox&) {}
	operator=(const CMessageBox&) {}
};

static const char* msgMessageBoxDecision = "MsgBoxDc";
class CMessageBoxDecisionMsg : public CMessage
{ public: CMessageBoxDecisionMsg(int p_iResult) : CMessage(msgMessageBoxDecision, false, true, p_iResult) {} };

} // namespace UILib

#endif   // ifdef UILIB_MESSAGEBOX_H_INCLUDED
