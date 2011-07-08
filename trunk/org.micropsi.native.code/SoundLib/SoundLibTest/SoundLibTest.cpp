// SoundLibTest.cpp : Definiert das Klassenverhalten für die Anwendung.
//

#include "stdafx.h"
#include "SoundLibTest.h"
#include "SoundLibTestDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CSoundLibTestApp

BEGIN_MESSAGE_MAP(CSoundLibTestApp, CWinApp)
	ON_COMMAND(ID_HELP, CWinApp::OnHelp)
END_MESSAGE_MAP()


// CSoundLibTestApp-Erstellung

CSoundLibTestApp::CSoundLibTestApp()
{
	// TODO: Hier Code zur Konstruktion einfügen
	// Alle wichtigen Initialisierungen in InitInstance positionieren
}


// Das einzige CSoundLibTestApp-Objekt

CSoundLibTestApp theApp;


// CSoundLibTestApp Initialisierung

BOOL CSoundLibTestApp::InitInstance()
{
	// InitCommonControls() ist für Windows XP erforderlich, wenn ein Anwendungsmanifest
	// die Verwendung von ComCtl32.dll Version 6 oder höher zum Aktivieren
	// von visuellen Stilen angibt. Ansonsten treten beim Erstellen von Fenstern Fehler auf.
	InitCommonControls();

	CWinApp::InitInstance();
	AfxEnableControlContainer( );
	CSoundSpaceView::RegisterWindowClass();
	CDirectionControl::RegisterWindowClass();

	CSoundLibTestDlg dlg;
	m_pMainWnd = &dlg;
	INT_PTR nResponse = dlg.DoModal();
	if (nResponse == IDOK)
	{
		// TODO: Fügen Sie hier Code ein, um das Schließen des
		//  Dialogfelds über OK zu steuern
	}
	else if (nResponse == IDCANCEL)
	{
		// TODO: Fügen Sie hier Code ein, um das Schließen des
		//  Dialogfelds über "Abbrechen" zu steuern
	}

	// Da das Dialogfeld geschlossen wurde, FALSE zurückliefern, so dass wir die
	//  Anwendung verlassen, anstatt das Nachrichtensystem der Anwendung zu starten.
	return FALSE;
}
