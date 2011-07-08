
#ifndef UILIB_FILEPICKER_H_INCLUDED 
#define UILIB_FILEPICKER_H_INCLUDED

#include "uilib/controls/filebrowserlist.h"
#include "uilib/controls/button.h"

namespace UILib
{

class CFilePicker : public CWindow
{
public:

	enum PickMode
	{
		PM_PickFiles		= 1 << 0,
		PM_PickFolders		= 1 << 1,
		PM_PickDrives		= 1 << 2,
	};

	enum Result
	{
		RES_FilePicked,				///< Benutzer hat tatsächlich eine Datei ausgewählt
		RES_Canceled				///< Benutzer hat den Vorgang abgebrochen
	};

	/// erzeugt einen neuen Filepicker
	static CFilePicker*	Create();

	/// bestimmt ein alternatives Fenster, dass die OK-Nachricht bekommt (Standard = das Parent)
	void	SetCallbackWindow(WHDL p_xCallback);

	/// liefert die Auswahl als String
	CStr	GetSelection() const;

	/// setzt den Picking Modus - was kann ausgewählt werden
	void	SetPickMode(int p_iMode);

	/// setzt den aktuellen Pfad
	void	SetPath(const CStr& p_rsPath);

	/// setzt einen Dateifilter (Standard = "*.*")
	void	SetFilterRule(const CStr& p_rsDesc, const CStr& p_rsFilter);

	/// wird gerufen, wenn das Ergebnis des Auswahlvorganges vorliegt
	virtual void	OnResult(Result eResult);

	/// setzt Callbackfunktion für "Result"
	void			SetOnResultCallback(CFunctionPointer2<CFilePicker*, Result>& rxCallback);

protected:

	CFilePicker();
	virtual ~CFilePicker();

	virtual bool	HandleMsg(const CMessage& p_rxMessage);
	virtual bool	OnVisualizationChange();

	void			UpdateOKButton();


	CButton*			m_pxOKButton;
	CButton*			m_pxCancelButton;
	CFileBrowserList*	m_pxList;
	CStr				m_sSelection;

	WHDL				m_xCallbackWindow;
	int					m_iPickMode;					///< aktueller Modus; siehe enum PickMode

	CFunctionPointer2<CFilePicker*, Result>	m_xOnResultCallback;	///< Callbackfunktion, wenn das Ergebnis vorliegt

private:

	CFilePicker(const CFilePicker&) {};
	operator=(const CFilePicker&) {};
};

static const char* msgFilePicked = "FilePick";
class CFilePickedMsg : public CMessage
{ public: CFilePickedMsg(WHDL hWnd) : CMessage(msgFilePicked, false, true, hWnd)	{} };


static const char* msgFilePickCanceled = "PickCncl";
class CFilePickCanceledMsg : public CMessage
{ public: CFilePickCanceledMsg(WHDL hWnd) : CMessage(msgFilePickCanceled, false, true, hWnd)	{} };
} // namespace UILib

#endif // ifndef UILIB_FILEPICKER_H_INCLUDED
