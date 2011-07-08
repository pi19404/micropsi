// SoundLibTestDlg.cpp : Implementierungsdatei
//

#include "stdafx.h"
#include "SoundLibTest.h"
#include "SoundLibTestDlg.h"

#include "soundlib/soundsystem.h"
#include ".\soundlibtestdlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

using namespace SoundLib;

// CSoundLibTestDlg Dialogfeld



//---------------------------------------------------------------------------------------------------------------------
CSoundLibTestDlg::CSoundLibTestDlg(CWnd* pParent)
	: CDialog(CSoundLibTestDlg::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_VOLUMESLIDER, m_xVolumeSlider);
	DDX_Control(pDX, IDC_PITCHSLIDER, m_xPitchSlider);
	DDX_Control(pDX, IDC_SEEKSLIDER, m_xSeekSlider);
	DDX_Control(pDX, IDC_SOUNDXSLIDER, m_xSoundXSlider);
	DDX_Control(pDX, IDC_SOUNDYSLIDER, m_xSoundYSlider);
	DDX_Control(pDX, IDC_SOUNDZSLIDER, m_xSoundZSlider);
	DDX_Control(pDX, IDC_MINDISTSLIDER, m_xMinDistSlider);
	DDX_Control(pDX, IDC_MAXDISTSLIDER, m_xMaxDistSlider);
	DDX_Control(pDX, IDC_FILE, m_xFileComboBox);
	DDX_Control(pDX, IDC_3DSOUND, m_x3DSoundCheckBox);
	DDX_Control(pDX, IDC_STREAMING, m_xStreamingCheckBox);
	DDX_Control(pDX, IDC_LOOP, m_xLoopCheckBox);
	DDX_Control(pDX, IDC_VOLUMEEDIT, m_xVolumeEdit);
	DDX_Control(pDX, IDC_PITCHEDIT, m_xPitchEdit);
	DDX_Control(pDX, IDC_SOUNDXEDIT, m_xSoundXEdit);
	DDX_Control(pDX, IDC_SOUNDYEDIT, m_xSoundYEdit);
	DDX_Control(pDX, IDC_SOUNDZEDIT, m_xSoundZEdit);
	DDX_Control(pDX, IDC_MINDISTEDIT, m_xMinDistEdit);
	DDX_Control(pDX, IDC_MAXDISTEDIT, m_xMaxDistEdit);
	DDX_Control(pDX, IDC_CUSTOM2, m_Viewer);
	DDX_Control(pDX, IDC_CHANNELENVIRONMENT, m_ChannelEnvironment);
	DDX_Control(pDX, IDC_GLOBALENVIRONMENT, m_GlobalEnvironment);
	DDX_Control(pDX, IDC_LISTENERXZNORMAL, m_XZListenerNormal);
	DDX_Control(pDX, IDC_LISTENERXYNORMAL, m_XYListenerNormal);
	DDX_Control(pDX, IDC_LISTENERXNORMALEDIT, m_ListenerXNormalEdit);
	DDX_Control(pDX, IDC_LISTENERYNORMALEDIT, m_ListenerYNormalEdit);
	DDX_Control(pDX, IDC_LISTENERZNORMALEDIT, m_ListenerZNormalEdit);
	DDX_Control(pDX, IDC_HWVOICES, m_HWVoices);
	DDX_Control(pDX, IDC_HW3DVOICES, m_HW3DVoices);
	DDX_Control(pDX, IDC_EAXVERSION, m_EAXVersion);
	DDX_Control(pDX, IDC_NUMLISTENERS, m_xNumListeners);
}

//---------------------------------------------------------------------------------------------------------------------
BEGIN_MESSAGE_MAP(CSoundLibTestDlg, CDialog)
	ON_WM_PAINT()
	ON_WM_TIMER()
	ON_WM_DESTROY()
	ON_WM_QUERYDRAGICON()
	ON_WM_HSCROLL()
	//}}AFX_MSG_MAP
	ON_NOTIFY(NM_CUSTOMDRAW, IDC_VOLUMESLIDER, OnNMCustomdrawVolumeslider)
	ON_NOTIFY(NM_CUSTOMDRAW, IDC_MINDISTSLIDER, OnNMCustomdrawMindistslider)
	ON_NOTIFY(NM_CUSTOMDRAW, IDC_MAXDISTSLIDER, OnNMCustomdrawMaxdistslider)
	ON_NOTIFY(NM_CUSTOMDRAW, IDC_LISTENERXSLIDER, OnNMCustomdrawListenerxslider)
	ON_NOTIFY(NM_CUSTOMDRAW, IDC_LISTENERYSLIDER, OnNMCustomdrawListeneryslider)
	ON_NOTIFY(NM_CUSTOMDRAW, IDC_LISTENERZSLIDER, OnNMCustomdrawListenerzslider)
	ON_NOTIFY(NM_CUSTOMDRAW, IDC_SOUNDXSLIDER, OnNMCustomdrawSoundxslider)
	ON_NOTIFY(NM_CUSTOMDRAW, IDC_SOUNDYSLIDER, OnNMCustomdrawSoundyslider)
	ON_NOTIFY(NM_CUSTOMDRAW, IDC_SOUNDZSLIDER, OnNMCustomdrawSoundzslider)
	ON_NOTIFY(FN_DIRECTIONCHANGED, IDC_LISTENERXZNORMAL, OnNMListenerNormal)
	ON_NOTIFY(FN_DIRECTIONCHANGED, IDC_LISTENERXYNORMAL, OnNMListenerNormal)
	ON_BN_CLICKED(IDC_3DSOUND, OnBnClicked3dsound)
	ON_BN_CLICKED(IDC_BROWSEFILE, OnBnClickedBrowsefile)
	ON_BN_CLICKED(IDC_PLAY, OnBnClickedPlay)
	ON_BN_CLICKED(IDC_STOP, OnBnClickedStop)
	ON_BN_CLICKED(IDC_CHANNEL1, OnBnClickedChannel1)
	ON_BN_CLICKED(IDC_CHANNEL2, OnBnClickedChannel2)
	ON_BN_CLICKED(IDC_CHANNEL3, OnBnClickedChannel3)
	ON_BN_CLICKED(IDC_CHANNEL4, OnBnClickedChannel4)
	ON_BN_CLICKED(IDC_CHANNEL5, OnBnClickedChannel5)
	ON_BN_CLICKED(IDC_CHANNEL6, OnBnClickedChannel6)
	ON_BN_CLICKED(IDC_CHANNEL7, OnBnClickedChannel7)
	ON_BN_CLICKED(IDC_CHANNEL0, OnBnClickedChannel0)
	ON_CBN_SELCHANGE(IDC_FILE, OnCbnSelchangeFile)
	ON_BN_CLICKED(IDC_LOOP, OnBnClickedLoop)
	ON_NOTIFY(NM_CUSTOMDRAW, IDC_PITCHSLIDER, OnNMCustomdrawPitchslider)
	ON_BN_CLICKED(IDC_LISTENER0, OnBnClickedListener0)
	ON_BN_CLICKED(IDC_LISTENER1, OnBnClickedListener1)
	ON_BN_CLICKED(IDC_LISTENER2, OnBnClickedListener2)
	ON_BN_CLICKED(IDC_LISTENER3, OnBnClickedListener3)
	ON_CBN_SELCHANGE(IDC_NUMLISTENERS, OnCbnSelchangeNumlisteners)
	ON_CBN_SELCHANGE(IDC_GLOBALENVIRONMENT, OnCbnSelchangeGlobalenvironment)
END_MESSAGE_MAP()


//---------------------------------------------------------------------------------------------------------------------
BOOL 
CSoundLibTestDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Symbol für dieses Dialogfeld festlegen. Wird automatisch erledigt
	//  wenn das Hauptfenster der Anwendung kein Dialogfeld ist
	SetIcon(m_hIcon, TRUE);			// Großes Symbol verwenden
	SetIcon(m_hIcon, FALSE);		// Kleines Symbol verwenden

	CSoundSystem::Init(m_hWnd, true, true);
	
	m_bSeekSliderBeingTracked = false;
	
	m_xVolumeSlider.SetRange(0, 100, true);
	m_xPitchSlider.SetRange(20, 200, true);
	m_xMinDistSlider.SetRange(1, 100, true);
	m_xMaxDistSlider.SetRange(1, 10000, true);
	m_xSoundXSlider.SetRange(-100, 100, true);
	m_xSoundYSlider.SetRange(-100, 100, true);
	m_xSoundZSlider.SetRange(-100, 100, true);

	CSliderCtrl* pSlider;
	pSlider = (CSliderCtrl*) GetDlgItem(IDC_LISTENERXSLIDER);
	pSlider->SetRange(-100, 100, true);

	pSlider = (CSliderCtrl*) GetDlgItem(IDC_LISTENERYSLIDER);
	pSlider->SetRange(-100, 100, true);

	pSlider = (CSliderCtrl*) GetDlgItem(IDC_LISTENERZSLIDER);
	pSlider->SetRange(-100, 100, true);

	m_xSeekSlider.SetRange(0, 1000, true);

	m_hIconPlay		= AfxGetApp()->LoadIcon(IDI_PLAY);
	m_hIconPause	= AfxGetApp()->LoadIcon(IDI_PAUSE);
	m_hIconStop		= AfxGetApp()->LoadIcon(IDI_STOP);
	m_aiStatusControlIDs[0] = IDC_STATUS0;
	m_aiStatusControlIDs[1] = IDC_STATUS1;
	m_aiStatusControlIDs[2] = IDC_STATUS2;
	m_aiStatusControlIDs[3] = IDC_STATUS3;
	m_aiStatusControlIDs[4] = IDC_STATUS4;
	m_aiStatusControlIDs[5] = IDC_STATUS5;
	m_aiStatusControlIDs[6] = IDC_STATUS6;
	m_aiStatusControlIDs[7] = IDC_STATUS7;

	LoadFileHistory();

	m_iCurrentChannel = 0;
	CButton* pxChannelButton = (CButton*) GetDlgItem(IDC_CHANNEL0);
	pxChannelButton->SetCheck(1);
	ResetGUIDefaults();

	m_fSoundX = m_fSoundY = m_fSoundZ = 0.0f;
	m_fListenerX = m_fListenerY = m_fListenerZ = 0.0f;
	CSoundSystem::Get().SetListenerPos(m_fListenerX, m_fListenerY, m_fListenerZ, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);


	m_xNumListeners.SetCurSel(0);
	OnCbnSelchangeNumlisteners();

	const CSoundSystem::TCaps& xCaps = CSoundSystem::Get().GetCaps();
	CString s;
	s.Format("%d Voices with Hardware Mixing\n%d Static Voices with Hardware Mixing\n%d Streaming Voices with Hardware Mixing", 
		xCaps.m_iHWMixingAllBuffers, xCaps.m_iHWMixingStaticBuffers, xCaps.m_iHWMixingStreamingBuffers);
	m_HWVoices.SetWindowText(s);

	s.Format("%d Voices with 3D Hardware Mixing\n%d Static Voices with 3D Hardware Mixing\n%d Streaming Voices with 3D Hardware Mixing", 
		xCaps.m_iHW3DAllBuffers, xCaps.m_iHW3DStaticBuffers, xCaps.m_iHW3DStreamingBuffers);
	m_HW3DVoices.SetWindowText(s);

	int iEAXVersion = CSoundSystem::Get().GetEAXSupport();
	if(iEAXVersion <= 0)
	{
		m_EAXVersion.SetWindowText("EAX Support: none");
	}
	else if(iEAXVersion == 1)
	{
		m_EAXVersion.SetWindowText("EAX Support: EAX 1.0");
	}
	else if(iEAXVersion >= 2)
	{
		m_EAXVersion.SetWindowText("EAX Support: EAX 2.0 or better");
	}

	this->SetTimer(0, 1000 / 25, NULL);

	return TRUE;  // Geben Sie TRUE zurück, außer ein Steuerelement soll den Fokus erhalten
}



//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnDestroy()
{
	SaveFileHistory();
	CSoundSystem::Shut();
}


//---------------------------------------------------------------------------------------------------------------------
// Wenn Sie dem Dialogfeld eine Schaltfläche "Minimieren" hinzufügen, benötigen Sie 
//  den nachstehenden Code, um das Symbol zu zeichnen. Für MFC-Anwendungen, die das 
//  Dokument/Ansicht-Modell verwenden, wird dies automatisch ausgeführt.
void 
CSoundLibTestDlg::OnPaint() 
{
	if (IsIconic())
	{
		CPaintDC dc(this); // Gerätekontext zum Zeichnen

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// Symbol in Clientrechteck zentrieren
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Symbol zeichnen
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

//---------------------------------------------------------------------------------------------------------------------
// Die System ruft diese Funktion auf, um den Cursor abzufragen, der angezeigt wird, während der Benutzer
//  das minimierte Fenster mit der Maus zieht.
HCURSOR 
CSoundLibTestDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnTimer(UINT_PTR nIDEvent)
{
	CSoundSystem::Get().Tick();
	UpdateGUI();
	m_Viewer.Invalidate();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnNMCustomdrawVolumeslider(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMCUSTOMDRAW pNMCD = reinterpret_cast<LPNMCUSTOMDRAW>(pNMHDR);
	*pResult = 0;

	if(m_iCurrentChannel >= 0)
	{
		int iVol = m_xVolumeSlider.GetPos();
		CSoundSystem::Get().GetVoice(m_iCurrentChannel)->SetVolume((float) iVol);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnNMCustomdrawPitchslider(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMCUSTOMDRAW pNMCD = reinterpret_cast<LPNMCUSTOMDRAW>(pNMHDR);
	// TODO: Add your control notification handler code here
	*pResult = 0;

	CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iCurrentChannel);
	if(pxVoice->IsPlaying())
	{
		float fPitch = (float) m_xPitchSlider.GetPos() / 100.0f;
		pxVoice->SetPitch(fPitch);
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnNMCustomdrawMindistslider(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMCUSTOMDRAW pNMCD = reinterpret_cast<LPNMCUSTOMDRAW>(pNMHDR);
	// TODO: Add your control notification handler code here
	*pResult = 0;

	CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iCurrentChannel);
	if(pxVoice->Is3DSound())
	{
		int iDist = m_xMinDistSlider.GetPos();
		CSoundSystem::Get().GetVoice(m_iCurrentChannel)->SetMinDistance((float) iDist);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnNMCustomdrawMaxdistslider(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMCUSTOMDRAW pNMCD = reinterpret_cast<LPNMCUSTOMDRAW>(pNMHDR);
	// TODO: Add your control notification handler code here
	*pResult = 0;

	CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iCurrentChannel);
	if(pxVoice->Is3DSound())
	{
		int iDist = m_xMaxDistSlider.GetPos();
		pxVoice->SetMaxDistance((float) iDist);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnNMCustomdrawListenerxslider(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMCUSTOMDRAW pNMCD = reinterpret_cast<LPNMCUSTOMDRAW>(pNMHDR);
	// TODO: Add your control notification handler code here
	*pResult = 0;

	CSliderCtrl* p = (CSliderCtrl*) GetDlgItem(IDC_LISTENERXSLIDER);
	int iVal = p->GetPos();
	m_fListenerX = (float) iVal;
	CSoundSystem::Get().SetListenerPos(m_fListenerX, m_fListenerY, m_fListenerZ, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnNMCustomdrawListeneryslider(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMCUSTOMDRAW pNMCD = reinterpret_cast<LPNMCUSTOMDRAW>(pNMHDR);
	*pResult = 0;

	CSliderCtrl* p = (CSliderCtrl*) GetDlgItem(IDC_LISTENERYSLIDER);
	int iVal = p->GetPos();
	m_fListenerY = (float) iVal;
	CSoundSystem::Get().SetListenerPos(m_fListenerX, m_fListenerY, m_fListenerZ, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnNMCustomdrawListenerzslider(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMCUSTOMDRAW pNMCD = reinterpret_cast<LPNMCUSTOMDRAW>(pNMHDR);
	*pResult = 0;

	CSliderCtrl* p = (CSliderCtrl*) GetDlgItem(IDC_LISTENERZSLIDER);
	int iVal = p->GetPos();
	m_fListenerZ = (float) iVal;
	CSoundSystem::Get().SetListenerPos(m_fListenerX, m_fListenerY, m_fListenerZ, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnNMCustomdrawSoundxslider(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMCUSTOMDRAW pNMCD = reinterpret_cast<LPNMCUSTOMDRAW>(pNMHDR);
	*pResult = 0;

	CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iCurrentChannel);
	if(pxVoice->Is3DSound())
	{
		int iVal = m_xSoundXSlider.GetPos();
		m_fSoundX = (float) iVal;
		if(CSoundSystem::Get().GetVoice(m_iCurrentChannel)->Is3DSound())
		{
			CSoundSystem::Get().GetVoice(m_iCurrentChannel)->SetPosition(m_fSoundX, m_fSoundY, m_fSoundZ);
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnNMCustomdrawSoundyslider(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMCUSTOMDRAW pNMCD = reinterpret_cast<LPNMCUSTOMDRAW>(pNMHDR);
	// TODO: Add your control notification handler code here
	*pResult = 0;
	
	CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iCurrentChannel);
	if(pxVoice->Is3DSound())
	{
		int iVal = m_xSoundYSlider.GetPos();
		m_fSoundY = (float) iVal;
		if(CSoundSystem::Get().GetVoice(m_iCurrentChannel)->Is3DSound())
		{
			CSoundSystem::Get().GetVoice(m_iCurrentChannel)->SetPosition(m_fSoundX, m_fSoundY, m_fSoundZ);
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnNMCustomdrawSoundzslider(NMHDR *pNMHDR, LRESULT *pResult)
{
	LPNMCUSTOMDRAW pNMCD = reinterpret_cast<LPNMCUSTOMDRAW>(pNMHDR);
	*pResult = 0;

	CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iCurrentChannel);
	if(pxVoice->Is3DSound())
	{
		int iVal = m_xSoundZSlider.GetPos();
		m_fSoundZ = (float) iVal;
		if(CSoundSystem::Get().GetVoice(m_iCurrentChannel)->Is3DSound())
		{
			CSoundSystem::Get().GetVoice(m_iCurrentChannel)->SetPosition(m_fSoundX, m_fSoundY, m_fSoundZ);
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnBnClicked3dsound()
{
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnCbnSelchangeFile()
{
	// TODO: Add your control notification handler code here
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnBnClickedBrowsefile()
{
	CString sFile;
	m_xFileComboBox.GetWindowText(sFile);

	OPENFILENAME ofn;
	memset(&ofn, 0, sizeof(OPENFILENAME));
	ofn.lStructSize = sizeof(OPENFILENAME);
	ofn.hwndOwner = AfxGetApp()->GetMainWnd()->m_hWnd;
	ofn.lpstrFile = sFile.GetBuffer(1024);
	ofn.nMaxFile = 1024;

	ofn.lpstrFilter = "All Files (*.*)\0*.*\0Sound Files (wav, ogg)\0*.wav;*.ogg\0";
	ofn.nFilterIndex = 2;

	ofn.lpstrFileTitle = NULL;
	ofn.nMaxFileTitle = 0;
	ofn.lpstrInitialDir = NULL;
	ofn.Flags = OFN_PATHMUSTEXIST | OFN_FILEMUSTEXIST;

	if(GetOpenFileName(&ofn))
	{
		m_xFileComboBox.SetWindowText(sFile);
		int iLen = (int) strlen(sFile.GetBuffer());
		POSITION p = m_asFileHistory.Find(sFile);
		if(iLen > 0  &&  p == NULL)
		{
			m_asFileHistory.AddHead(sFile);
			while(m_asFileHistory.GetCount() > 20)
			{
				m_asFileHistory.RemoveTail();
			}
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnBnClickedPlay()
{
	// Read Settings from UI

	float fVolume = (float) m_xVolumeSlider.GetPos();

	CString sFile;
	m_xFileComboBox.GetWindowText(sFile);
	
	bool b3DSound = m_x3DSoundCheckBox.GetCheck() != 0;
	bool bLoop = m_xLoopCheckBox.GetCheck() != 0;

	// Play!

	CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iCurrentChannel);
	if(!pxVoice->SetFile(sFile, b3DSound))
	{
		AfxMessageBox("Soundbuffer could not be created! Possible Reasons:\na) The file could not be read.\nb) The file is corrupted or in an unknown format.\nc) You tried to create a 3D sound, but the file has more than one channel (it is not mono).", MB_ICONERROR | MB_OK);
		return;
	}

	pxVoice->SetVolume(fVolume);

	if(b3DSound)
	{
		pxVoice->SetMinDistance((float) m_xMinDistSlider.GetPos());
		pxVoice->SetMaxDistance((float) m_xMaxDistSlider.GetPos());
		pxVoice->SetPosition(	(float) m_xSoundXSlider.GetPos(), 
								(float) m_xSoundYSlider.GetPos(), 
								(float) m_xSoundZSlider.GetPos()); 
	}

	pxVoice->Play(bLoop);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnBnClickedStop()
{
	CSoundSystem::Get().GetVoice(m_iCurrentChannel)->Stop();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnBnClickedChannel0()
{
	m_iCurrentChannel = 0;
	if(!CSoundSystem::Get().GetVoice(m_iCurrentChannel)->IsPlaying()) 
	{ 
		ResetGUIDefaults(); 
	}
}

void 
CSoundLibTestDlg::OnBnClickedChannel1()
{
	m_iCurrentChannel = 1;
	if(!CSoundSystem::Get().GetVoice(m_iCurrentChannel)->IsPlaying()) 
	{ 
		ResetGUIDefaults(); 
	}
}

void 
CSoundLibTestDlg::OnBnClickedChannel2()
{
	m_iCurrentChannel = 2;
	if(!CSoundSystem::Get().GetVoice(m_iCurrentChannel)->IsPlaying()) 
	{ 
		ResetGUIDefaults(); 
	}
}

void 
CSoundLibTestDlg::OnBnClickedChannel3()
{
	m_iCurrentChannel = 3;
	if(!CSoundSystem::Get().GetVoice(m_iCurrentChannel)->IsPlaying()) 
	{ 
		ResetGUIDefaults(); 
	}
}

void 
CSoundLibTestDlg::OnBnClickedChannel4()
{
	m_iCurrentChannel = 4;
	if(!CSoundSystem::Get().GetVoice(m_iCurrentChannel)->IsPlaying()) 
	{ 
		ResetGUIDefaults(); 
	}
}

void 
CSoundLibTestDlg::OnBnClickedChannel5()
{
	m_iCurrentChannel = 5;
	if(!CSoundSystem::Get().GetVoice(m_iCurrentChannel)->IsPlaying()) 
	{ 
		ResetGUIDefaults(); 
	}
}

void 
CSoundLibTestDlg::OnBnClickedChannel6()
{
	m_iCurrentChannel = 6;
	if(!CSoundSystem::Get().GetVoice(m_iCurrentChannel)->IsPlaying()) 
	{ 
		ResetGUIDefaults(); 
	}
}

void 
CSoundLibTestDlg::OnBnClickedChannel7()
{
	m_iCurrentChannel = 7;
	if(!CSoundSystem::Get().GetVoice(m_iCurrentChannel)->IsPlaying()) 
	{ 
		ResetGUIDefaults(); 
	}
}
//---------------------------------------------------------------------------------------------------------------------
void
CSoundLibTestDlg::UpdateGUI()
{
	CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iCurrentChannel);

	CButton* pxPlayButton		= (CButton*) GetDlgItem(IDC_PLAY);
	CButton* pxStopButton		= (CButton*) GetDlgItem(IDC_STOP);

	CStatic* pxTimeStatic		= (CStatic*) GetDlgItem(IDC_TIME);
	CStatic* pxTotalTime		= (CStatic*) GetDlgItem(IDC_TIMETOTAL);

	if(pxVoice->IsPlaying())
	{
		m_xFileComboBox.SetWindowText(pxVoice->GetFilename().c_str());

		m_x3DSoundCheckBox.SetCheck(pxVoice->Is3DSound() ? 1 : 0);
		m_x3DSoundCheckBox.EnableWindow(0);
		m_xStreamingCheckBox.SetCheck(pxVoice->IsStreamingBuffer());
		m_xStreamingCheckBox.EnableWindow(0);
		m_xLoopCheckBox.SetCheck(pxVoice->IsLooping());
		m_xLoopCheckBox.EnableWindow(0);

		pxPlayButton->EnableWindow(0);
		pxStopButton->EnableWindow(1);

		float fTime = (float) pxVoice->GetCurrentPlayTimeInSeconds();
		float fTimeLeft = (float) pxVoice->GetRemainingPlayTimeInSeconds();
		CString sTime;
		sTime.Format("%02d:%02d:%03d (remaining %02d:%02d:%03d)", 
			int (fTime / 60.0f), (int) fmodf(fTime, 60.0f), (int) fmodf(fTime*1000.0f, 1000.0f), 
			int (fTimeLeft / 60.0f), (int) fmodf(fTimeLeft, 60.0f), (int) fmodf(fTimeLeft*1000.0f, 1000.0f)); 
		pxTimeStatic->SetWindowText(sTime);
		
		float fTotalTime = (float) pxVoice->GetTotalTimeInSeconds();
		CString sTotalTime;
		sTotalTime.Format("%02d:%02d:%03d", 
			int (fTotalTime / 60.0f), (int) fmodf(fTotalTime, 60.0f), (int) fmodf(fTotalTime*1000.0f, 1000.0f));
		pxTotalTime->SetWindowText(sTotalTime);

		if(!m_bSeekSliderBeingTracked)
		{
			m_xSeekSlider.SetPos((int) (fTime / fTotalTime * 1000));
		}
		m_xSeekSlider.EnableWindow(1);

		m_xVolumeSlider.SetPos((int) pxVoice->GetVolume());
		m_xPitchSlider.SetPos((int) (pxVoice->GetPitch() * 100.0f));

		if(pxVoice->Is3DSound())
		{
			float fX, fY, fZ;
			pxVoice->GetPosition(fX, fY, fZ);
			m_xSoundXSlider.SetPos((int) fX);
			m_xSoundYSlider.SetPos((int) fY);
			m_xSoundZSlider.SetPos((int) fZ);
			m_xMinDistSlider.SetPos((int) pxVoice->GetMinDistance());
			m_xMaxDistSlider.SetPos((int) pxVoice->GetMaxDistance());
		}
	}
	else
	{
		m_x3DSoundCheckBox.EnableWindow(1);
		m_xLoopCheckBox.EnableWindow(1);

		pxPlayButton->EnableWindow(1);
		pxStopButton->EnableWindow(0);
		m_xSeekSlider.EnableWindow(0);
	}


	// 3D-Sliders: nur enabled, wenn 3D enabled ist
	int b3DEnabled = m_x3DSoundCheckBox.GetCheck();
	m_xSoundXSlider.EnableWindow(b3DEnabled);
	m_xSoundYSlider.EnableWindow(b3DEnabled);	
	m_xSoundZSlider.EnableWindow(b3DEnabled);
	m_xMinDistSlider.EnableWindow(b3DEnabled);
	m_xMaxDistSlider.EnableWindow(b3DEnabled);

	// Status-Anzeigen neben den Channel-Buttons
	for(int i=0; i<8; ++i)
	{
		CStatic* pxChannelStatus = (CStatic*) GetDlgItem(m_aiStatusControlIDs[i]);
		HICON hIcon = CSoundSystem::Get().GetVoice(i)->IsPlaying() ? m_hIconPlay : m_hIconStop;
		if(pxChannelStatus->GetIcon() != hIcon)
		{
			pxChannelStatus->SetIcon(hIcon);
		}		
	}


	UpdateNumberDisplaysFromSliders();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::ResetGUIDefaults()
{
	CStatic* pxTimeStatic		= (CStatic*) GetDlgItem(IDC_TIME);
	CStatic* pxTotalTime		= (CStatic*) GetDlgItem(IDC_TIMETOTAL);

	m_xFileComboBox.ResetContent();
	POSITION pos = m_asFileHistory.GetHeadPosition();
	for(int i=0; i<m_asFileHistory.GetCount(); ++i)
	{
		m_xFileComboBox.AddString(m_asFileHistory.GetNext(pos));
	}

	m_x3DSoundCheckBox.SetCheck(0);
	m_xStreamingCheckBox.SetCheck(0);
	m_xLoopCheckBox.SetCheck(0);

	pxTimeStatic->SetWindowText("00:00:000 (remaining 00:00:000)");
	pxTotalTime->SetWindowText("00:00:000");
	m_xSeekSlider.SetPos(0);

	m_xVolumeSlider.SetPos(100);
	m_xPitchSlider.SetPos(100);
	m_xSoundXSlider.SetPos(0);
	m_xSoundYSlider.SetPos(0);
	m_xSoundZSlider.SetPos(0);
	m_xMinDistSlider.SetPos(1);
	m_xMaxDistSlider.SetPos(10000);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::UpdateNumberDisplaysFromSliders()
{
	CString s;

	s.Format("%d", m_xVolumeSlider.GetPos());
	m_xVolumeEdit.SetWindowText(s);

	s.Format("%.2f", (float) m_xPitchSlider.GetPos() / 100.0f);
	m_xPitchEdit.SetWindowText(s);

	s.Format("%d", m_xSoundXSlider.GetPos());
	m_xSoundXEdit.SetWindowText(s);

	s.Format("%d", m_xSoundYSlider.GetPos());
	m_xSoundYEdit.SetWindowText(s);

	s.Format("%d", m_xSoundZSlider.GetPos());
	m_xSoundZEdit.SetWindowText(s);

	CSliderCtrl* pListenerXSlider = (CSliderCtrl*) GetDlgItem(IDC_LISTENERXSLIDER);
	CEdit* pListenerXEdit = (CEdit*) GetDlgItem(IDC_LISTENERXEDIT);
	s.Format("%d", pListenerXSlider->GetPos());
	pListenerXEdit->SetWindowText(s);

	CSliderCtrl* pListenerYSlider = (CSliderCtrl*) GetDlgItem(IDC_LISTENERYSLIDER);
	CEdit* pListenerYEdit = (CEdit*) GetDlgItem(IDC_LISTENERYEDIT);
	s.Format("%d", pListenerYSlider->GetPos());
	pListenerYEdit->SetWindowText(s);

	CSliderCtrl* pListenerZSlider = (CSliderCtrl*) GetDlgItem(IDC_LISTENERZSLIDER);
	CEdit* pListenerZEdit = (CEdit*) GetDlgItem(IDC_LISTENERZEDIT);
	s.Format("%d", pListenerZSlider->GetPos());
	pListenerZEdit->SetWindowText(s);

	s.Format("%d", m_xMinDistSlider.GetPos());
	m_xMinDistEdit.SetWindowText(s);

	s.Format("%d", m_xMaxDistSlider.GetPos());
	m_xMaxDistEdit.SetWindowText(s);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnBnClickedLoop()
{
	// TODO: Add your control notification handler code here
}
//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnHScroll(UINT nSBCode, UINT nPos, CScrollBar* pScrollBar)
{
	if(pScrollBar->m_hWnd == m_xSeekSlider.m_hWnd)
	{
		if(nSBCode == TB_ENDTRACK)
		{
			m_bSeekSliderBeingTracked = false;
			CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iCurrentChannel);
			if(pxVoice->IsPlaying())
			{
				pxVoice->Seek((float) m_xSeekSlider.GetPos() / 1000.0f * pxVoice->GetTotalTimeInSeconds());
			}
		}
		else if(nSBCode == TB_THUMBTRACK)
		{
			m_bSeekSliderBeingTracked = true;
		}
	}
}
//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::LoadFileHistory()
{
	CFile xFile;
	CFileException fileException;
	if(xFile.Open("history.log", CFile::modeRead, &fileException))
	{
		CArchive xArchive(&xFile, CArchive::load);

		TRY
		{
			m_asFileHistory.Serialize(xArchive);
		}
		CATCH_ALL(e)
		{
		}
		END_CATCH_ALL

		xArchive.Close();
		xFile.Close();
	}
}
//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::SaveFileHistory()
{
	CFile xFile;
	CFileException fileException;
	if(xFile.Open("history.log", CFile::modeCreate | CFile::modeReadWrite, &fileException))
	{
		CArchive xArchive(&xFile, CArchive::store);

		TRY
		{
			m_asFileHistory.Serialize(xArchive);
		}
		CATCH_ALL(e)
		{
		}
		END_CATCH_ALL

		xArchive.Close();
		xFile.Close();
	}
}
//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnBnClickedListener0()
{
	// TODO: Add your control notification handler code here
}
//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnBnClickedListener1()
{
	// TODO: Add your control notification handler code here
}
//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnBnClickedListener2()
{
	// TODO: Add your control notification handler code here
}
//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnBnClickedListener3()
{
	// TODO: Add your control notification handler code here
}
//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnCbnSelchangeNumlisteners()
{
	int iNumListeners = m_xNumListeners.GetCurSel() + 1;
	CButton* p;
	p = (CButton*) GetDlgItem(IDC_LISTENER0);
	p->SetCheck(1);

	p = (CButton*) GetDlgItem(IDC_LISTENER1);
	p->EnableWindow(iNumListeners >= 2);
	p->SetCheck(0);

	p = (CButton*) GetDlgItem(IDC_LISTENER2);
	p->EnableWindow(iNumListeners >= 3);
	p->SetCheck(0);

	p = (CButton*) GetDlgItem(IDC_LISTENER3);
	p->EnableWindow(iNumListeners >= 4);
	p->SetCheck(0);

	OnBnClickedListener0();
}
//---------------------------------------------------------------------------------------------------------------------
void 
CSoundLibTestDlg::OnCbnSelchangeGlobalenvironment()
{
	int iEnv = m_GlobalEnvironment.GetCurSel();
	CSoundSystem::Get().SetSoundEnvironment(CSoundSystem::ENV_Generic + iEnv);
}
//---------------------------------------------------------------------------------------------------------------------
void
CSoundLibTestDlg::OnNMListenerNormal(NMHDR *pNMHDR, LRESULT *pResult)
{
	float fX, fY, fZ, fIgnored;
	m_XZListenerNormal.GetVector(fX, fZ);
	m_XYListenerNormal.GetVector(fIgnored, fY);

	CVec3 vDir(fX, fY, fZ);
	vDir.Normalize();

	CString s;
	s.Format("%.2f", vDir.x());
	m_ListenerXNormalEdit.SetWindowText(s);
	s.Format("%.2f", -vDir.y());
	m_ListenerYNormalEdit.SetWindowText(s);
	s.Format("%.2f", vDir.z());
	m_ListenerZNormalEdit.SetWindowText(s);
}
//---------------------------------------------------------------------------------------------------------------------

