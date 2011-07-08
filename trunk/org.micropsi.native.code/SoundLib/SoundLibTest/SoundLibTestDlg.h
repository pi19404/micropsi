// SoundLibTestDlg.h : Headerdatei
//

#pragma once

#include "afxtempl.h"
#include "afxcmn.h"
#include "afxwin.h"
#include "SoundSpaceView.h"
#include "DirectionControl.h"

#define CArray COtherArray			// böser Hack, um Namenskonflikt MFC vs. BaseLib zu lösen

// CSoundLibTestDlg Dialogfeld
class CSoundLibTestDlg : public CDialog
{
// Konstruktion
public:
	CSoundLibTestDlg(CWnd* pParent = NULL);	// Standardkonstruktor

// Dialogfelddaten
	enum { IDD = IDD_SOUNDLIBTEST_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV-Unterstützung


// Implementierung
protected:
	HICON	m_hIcon;
	
	HICON	m_hIconPlay, m_hIconPause, m_hIconStop;		///< Icons für Start, Pause, Stop
	int		m_aiStatusControlIDs[8];

	CList<CString> m_asFileHistory;						///< History für Files

	int		m_iCurrentChannel;							///< aktuell gewählter Soundkanal
	bool	m_bSeekSliderBeingTracked;					///< true, wenn der Seek-Slider gerade gezogen wird (dann: kein Update für diesen Slider)
	float	m_fListenerX, m_fListenerY, m_fListenerZ;	///< Position des Listeners
	float	m_fSoundX, m_fSoundY, m_fSoundZ;			///< Sound X, Y, Z
	
	// Generierte Funktionen für die Meldungstabellen
	virtual BOOL OnInitDialog();
	afx_msg void OnDestroy();
	afx_msg void OnPaint();
	afx_msg void OnTimer(UINT_PTR nIDEvent);
	
	void UpdateGUI();
	void UpdateNumberDisplaysFromSliders();
	void ResetGUIDefaults();

	void LoadFileHistory();
	void SaveFileHistory();

	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()
public:
	afx_msg void OnNMCustomdrawVolumeslider(NMHDR *pNMHDR, LRESULT *pResult);
	afx_msg void OnNMCustomdrawMindistslider(NMHDR *pNMHDR, LRESULT *pResult);
	afx_msg void OnNMCustomdrawMaxdistslider(NMHDR *pNMHDR, LRESULT *pResult);
	afx_msg void OnNMCustomdrawListenerxslider(NMHDR *pNMHDR, LRESULT *pResult);
	afx_msg void OnNMCustomdrawListeneryslider(NMHDR *pNMHDR, LRESULT *pResult);
	afx_msg void OnNMCustomdrawListenerzslider(NMHDR *pNMHDR, LRESULT *pResult);
	afx_msg void OnNMCustomdrawSoundxslider(NMHDR *pNMHDR, LRESULT *pResult);
	afx_msg void OnNMCustomdrawSoundyslider(NMHDR *pNMHDR, LRESULT *pResult);
	afx_msg void OnNMCustomdrawSoundzslider(NMHDR *pNMHDR, LRESULT *pResult);
	afx_msg void OnBnClicked3dsound();
	afx_msg void OnBnClickedBrowsefile();
	afx_msg void OnBnClickedPlay();
	afx_msg void OnBnClickedStop();
	afx_msg void OnBnClickedChannel0();
	afx_msg void OnBnClickedChannel1();
	afx_msg void OnBnClickedChannel2();
	afx_msg void OnBnClickedChannel3();
	afx_msg void OnBnClickedChannel4();
	afx_msg void OnBnClickedChannel5();
	afx_msg void OnBnClickedChannel6();
	afx_msg void OnBnClickedChannel7();
	afx_msg void OnCbnSelchangeFile();
	afx_msg void OnBnClickedLoop();
	afx_msg void OnNMCustomdrawPitchslider(NMHDR *pNMHDR, LRESULT *pResult);
	afx_msg void OnHScroll(UINT nSBCode, UINT nPos, CScrollBar* pScrollBar); 
	afx_msg void OnNMListenerNormal(NMHDR *pNMHDR, LRESULT *pResult);
	CSliderCtrl m_xVolumeSlider;
	CSliderCtrl m_xPitchSlider;
	CSliderCtrl m_xSeekSlider;
	CSliderCtrl m_xSoundXSlider;
	CSliderCtrl m_xSoundYSlider;
	CSliderCtrl m_xSoundZSlider;
	CSliderCtrl m_xMinDistSlider;
	CSliderCtrl m_xMaxDistSlider;
	CComboBox m_xFileComboBox;
	CButton m_x3DSoundCheckBox;
	CButton m_xStreamingCheckBox;
	CButton m_xLoopCheckBox;
	CEdit m_xVolumeEdit;
	CEdit m_xPitchEdit;
	CEdit m_xSoundXEdit;
	CEdit m_xSoundYEdit;
	CEdit m_xSoundZEdit;
	CEdit m_xMinDistEdit;
	CEdit m_xMaxDistEdit;
	CSoundSpaceView m_Viewer;
	CComboBox m_ChannelEnvironment;
	CComboBox m_GlobalEnvironment;
	CDirectionControl m_XZListenerNormal;
	CDirectionControl m_XYListenerNormal;
	CEdit m_ListenerXNormalEdit;
	CEdit m_ListenerYNormalEdit;
	CEdit m_ListenerZNormalEdit;
	CStatic m_HWVoices;
	CStatic m_HW3DVoices;
	CStatic m_EAXVersion;
	CComboBox m_xNumListeners;
	afx_msg void OnBnClickedListener0();
	afx_msg void OnBnClickedListener1();
	afx_msg void OnBnClickedListener2();
	afx_msg void OnBnClickedListener3();
	afx_msg void OnCbnSelchangeNumlisteners();
	afx_msg void OnCbnSelchangeGlobalenvironment();
};
