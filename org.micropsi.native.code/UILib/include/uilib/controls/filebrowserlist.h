
#ifndef UILIB_FILEBROWSERLIST_H_INCLUDED 
#define UILIB_FILEBROWSERLIST_H_INCLUDED

#include <vector>
#include "uilib/controls/listbox.h"

namespace UILib
{

class CFileBrowserList : public CListBox
{
public:

	static CFileBrowserList*	Create();

	/// setzt den aktuellen Pfad
	void SetPath(const CStr& p_rsPath);

	/// liefert den aktuellen Pfad
	CStr	GetPath();

	/// aktualisiert die Liste
	void	Update();

	/// liefert den vollständigen Pfad des aktuell angewählten Eintrages
	CStr GetSelectedItemPath() const;

	/// wechselt ins Oberverzeichnis
	void ChangeToParentDir();

	/// setzt einen Dateifilter (Standard = "*.*")
	void SetFilterRule(const CStr& p_rsDesc, const CStr& p_rsFilter);

	/// liefert true, wenn der übergebene Index ein Ordner ist
	bool IsFolder(unsigned int p_iIndex) const;

	/// liefert true, wenn der übergebene Index eine Datei ist
	bool IsFile(unsigned int p_iIndex) const;

	/// liefert true, wenn der übergebene Index ein Laufwerk ist
	bool IsDrive(unsigned int p_iIndex) const;

	/// wird gerufen, wenn der User auf einer Datei Enter drückt oder eine Datei doppelt anklickt
	virtual bool OnChoseFile();

	/// setzt Callbackfunktion für "State Change"
	void		 SetOnChoseFileCallback(CFunctionPointer1<CFileBrowserList*>& rxCallback);

	/// setzt den Wert eines benannten Attributes
	virtual bool SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);

	/// liefert den Wert eines benannten Attributes
	virtual bool GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;


protected:

	CFileBrowserList();
	~CFileBrowserList();

	virtual CStr	GetDebugString() const;

	virtual bool	OnLButtonDoubleClick(const CPnt& p_rxMousePos, unsigned char p_iModifier);
	virtual bool	OnControlKey(int p_iKey, unsigned char p_iModifier);

	void			ValidatePath();
	void			ChangeDir();
	void			Fill();

	CBitmap*				m_pxFolderBmp;			///< Bitmap für Ordner
	CBitmap*				m_pxDriveFixBmp;		///< Bitmap für feste Laufwerke
	CBitmap*				m_pxDriveCDBmp;			///< Bitmap für CD-Laufwerke
	CBitmap*				m_pxDriveFloppyBmp;		///< Bitmap für Floppies
	CBitmap*				m_pxDriveNetworkBmp;	///< Bitmap für Netzlaufwerke
	CBitmap*				m_pxFileBmp;			///< Bitmap für Dateien
	unsigned int			m_iNumDrives;			///< Anzahl Laufwerke im aktuellen Verzeichnis
	unsigned int			m_iNumDirs;				///< Anzahl Unterverzeichniss im aktuellen Verzeichnis
	unsigned int			m_iNumFiles;			///< Anzahl Dateien im aktuellen Verzeichnis
	CStr					m_sCurrentPath;			///< aktueller Pfad (Verzeichnis)

	CStr					m_sFilter;				///< Filterbeschreibung
	CDynArray<CStr>			m_asFilterExt;			///< Filter: Dateierweiterungen, die angezeigt werden

	CFunctionPointer1<CFileBrowserList*>	m_xOnChoseFileCallback;		///< Callbackfunktion wenn Datei ausgewählt mit Enter oder Doppelklick

private:

	CFileBrowserList(const CFileBrowserList&) {};
	operator=(const CFileBrowserList&) {};
};


static const char* msgFileBrowserListChoseFile = "FBLChFil";
class CFileBrowserListChoseFileMsg : public CMessage
{ public: CFileBrowserListChoseFileMsg(WHDL hWnd) : CMessage(msgFileBrowserListChoseFile, false, true, hWnd)	{} };


} // namespace UILib


#endif // ifndef UILIB_FILEBROWSERLIST_H_INCLUDED

