// HTTPLib.h : main header file for the HTTPLib DLL
//

#pragma once

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols


// CHTTPLibApp
// See HTTPLib.cpp for the implementation of this class
//

class CHTTPLibApp : public CWinApp
{
public:
	CHTTPLibApp();

// Overrides
public:
	virtual BOOL InitInstance();

	DECLARE_MESSAGE_MAP()
};
