#include "stdafx.h"
#include "windows.h"
#include "uilib/core/mousecursor.h"

namespace UILib
{ 

const char* CMouseCursor::ms_pcCurrentCursor = "???";

void CMouseCursor::SetCursor(CMouseCursor::CursorType p_eType)
{
	LPCTSTR sCursorType;
	switch(p_eType)
	{
		case CT_None:			return;
		case CT_Arrow:			sCursorType=IDC_ARROW;			ms_pcCurrentCursor="CT_Arrow";		break;
		case CT_SizeNS:			sCursorType=IDC_SIZENS;			ms_pcCurrentCursor="CT_SizeNS";		break;
		case CT_SizeWE:			sCursorType=IDC_SIZEWE;			ms_pcCurrentCursor="CT_SizeWE";		break;
		case CT_SizeNWSE:		sCursorType=IDC_SIZENWSE;		ms_pcCurrentCursor="CT_SizeNWSE";	break;
		case CT_SizeNESW:		sCursorType=IDC_SIZENESW;		ms_pcCurrentCursor="CT_SizeNESW";	break;
		case CT_Wait:			sCursorType=IDC_WAIT;			ms_pcCurrentCursor="CT_Wait";		break;
		case CT_Cross:			sCursorType=IDC_ARROW;			ms_pcCurrentCursor="CT_Cross";		break;
		case CT_No:				sCursorType=IDC_NO;				ms_pcCurrentCursor="CT_No";			break;
		case CT_IBeam:			sCursorType=IDC_IBEAM;			ms_pcCurrentCursor="CT_IBeam";		break;
		case CT_AppStarting:	sCursorType=IDC_APPSTARTING;	ms_pcCurrentCursor="CT_AppStarting";	break;
		case CT_Hand:			sCursorType=IDC_HAND;			ms_pcCurrentCursor="CT_Hand";		break;
		case CT_Help:			sCursorType=IDC_HELP;			ms_pcCurrentCursor="CT_Help";		break;
		case CT_UpArrow:		sCursorType=IDC_UPARROW;		ms_pcCurrentCursor="CT_UpArrow";		break;
		default: return;
	};

	HCURSOR hCursor = ::LoadCursor(0, sCursorType);
	::SetCursor(hCursor);	
}


//----------------------------------------------------------------------------------------------------------------------

void CMouseCursor::SetStandardCursor()
{
	HCURSOR hCursor = ::LoadCursor(0, IDC_ARROW);
	::SetCursor(hCursor);
}

} // namespace UILib

