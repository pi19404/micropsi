// SoundLibTest.h : Hauptheaderdatei f�r die SoundLibTest-Anwendung
//

#pragma once

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// Hauptsymbole


// CSoundLibTestApp:
// Siehe SoundLibTest.cpp f�r die Implementierung dieser Klasse
//

class CSoundLibTestApp : public CWinApp
{
public:
	CSoundLibTestApp();

// �berschreibungen
	public:
	virtual BOOL InitInstance();

// Implementierung

	DECLARE_MESSAGE_MAP()
};

extern CSoundLibTestApp theApp;
