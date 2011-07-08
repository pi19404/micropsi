
#include "stdafx.h"
#include <algorithm>
#include "uilib/controls/filebrowserlist.h" 
#include "uilib/core/windowmanager.h"
#include "uilib/core/virtualkeycodes.h"
#include <io.h>
#include <windows.h>

using std::vector;

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CFileBrowserList::CFileBrowserList()
{
	m_pxFolderBmp		= new CBitmap("std_folder.png");
	m_pxDriveFixBmp		= new CBitmap("std_drive.png");
	m_pxDriveCDBmp		= new CBitmap("std_drivecd.png");
	m_pxDriveFloppyBmp	= new CBitmap("std_drivefloppy.png");
	m_pxDriveNetworkBmp	= new CBitmap("std_drivenetwork.png");
	m_pxFileBmp			= new CBitmap("std_file.png");

	m_sCurrentPath		= "/"; 
	m_sFilter			= "";

	Fill();
}


//---------------------------------------------------------------------------------------------------------------------
CFileBrowserList::~CFileBrowserList() 
{
	delete m_pxFileBmp;
	delete m_pxDriveNetworkBmp;
	delete m_pxDriveFloppyBmp;
	delete m_pxDriveCDBmp;
	delete m_pxDriveFixBmp;
    delete m_pxFolderBmp;
}


//---------------------------------------------------------------------------------------------------------------------
CFileBrowserList*	
CFileBrowserList::Create()										
{ 
	return new CFileBrowserList(); 
}


//---------------------------------------------------------------------------------------------------------------------
/// setzt den aktuellen Pfad
void 
CFileBrowserList::SetPath(const CStr& p_rsPath)			
{ 
	m_sCurrentPath = p_rsPath; 
	ValidatePath(); 
	Fill(); 
}


//---------------------------------------------------------------------------------------------------------------------
/// \return aktueller Pfad
CStr	
CFileBrowserList::GetPath()							
{ 
	return m_sCurrentPath; 
}


//---------------------------------------------------------------------------------------------------------------------
/// aktualisiert die Liste
void	
CFileBrowserList::Update()							
{ 
	Fill(); 
}


//---------------------------------------------------------------------------------------------------------------------
///	\return voller Pfad des aktuell gewählten Eintrages
CStr 
CFileBrowserList::GetSelectedItemPath() const
{
	if(GetSelectedItem() >= 0)
	{
		if((unsigned int) GetSelectedItem() >= m_iNumDirs + m_iNumFiles)
		{
			return GetSelectedItemAsString().Left(2);
		}
		else
		{
			return m_sCurrentPath + GetSelectedItemAsString();
		}
	}
	else
	{
		return "";
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// \wechselt ins Oberverzeichnis
void 
CFileBrowserList::ChangeToParentDir()
{
	m_sCurrentPath.TrimRight('/');
	int i = m_sCurrentPath.FindReverse('/');
	m_sCurrentPath = m_sCurrentPath.Left(i+1);
	Fill();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt einen Dateifilter (nur passende Dateien werden angezeigt)

	\param p_rsDesc	Beschreibungstext
	\param p_rsFilter	Filter im Format: "*.exe; *.cpp; *.h" ...
*/
void 
CFileBrowserList::SetFilterRule(const CStr& p_rsDesc, const CStr& p_rsFilter)
{
	if (p_rsFilter == "")
	{
		m_sFilter = "";
		m_asFilterExt.Clear();
	}
	else
	{
		CStr sTmp = p_rsFilter;
		m_sFilter = p_rsDesc;
		sTmp.Remove('*');
		sTmp.Remove('.');
		sTmp.Split(m_asFilterExt,";");
	}

	Fill();
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CFileBrowserList::OnControlKey(int p_iKey, unsigned char p_iModifier)
{
	if(GetDisabled())
	{
		return true;
	}

	if(p_iKey == VKey_Return) 
	{
		ChangeDir();
		return true;
	}
	else
	{
		return __super::OnControlKey(p_iKey, p_iModifier);
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CFileBrowserList::OnLButtonDoubleClick(const CPnt& p_rxMousePos, unsigned char p_iModifier)
{
	if(GetDisabled())
	{
		return true;
	}

	CPnt pntMouse = p_rxMousePos;
	CWindowMgr::Get().ToClientPos(GetWHDL(), pntMouse);
	Select(pntMouse);
	ChangeDir();

	return true;
}



//---------------------------------------------------------------------------------------------------------------------
/// stellt sicher, dass der Pfad das richtige Format hat
void 
CFileBrowserList::ValidatePath()
{
	m_sCurrentPath.Replace('\\', '/');
	if(m_sCurrentPath.Right(1) != "/")
	{
		m_sCurrentPath += "/";
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// wechselt in das aktuell gewählte Verzeichnis
void 
CFileBrowserList::ChangeDir()
{
	assert(GetSelectedItem() >= 0);

	if(GetSelectedItemAsString() == "..")
	{
		m_sCurrentPath.TrimRight('/');
		int i = m_sCurrentPath.FindReverse('/');
		m_sCurrentPath = m_sCurrentPath.Left(i+1);
		Fill();
	}
	else if(GetSelectedItemAsString() == ".")
	{
		Fill();
	}
	else if(IsDrive(GetSelectedItem()))
	{
		m_sCurrentPath = GetSelectedItemAsString().Left(2) + "/";
		Fill();
	}
	else if(IsFolder(GetSelectedItem()))
	{
		m_sCurrentPath = m_sCurrentPath + GetSelectedItemAsString() + "/";
		Fill();
	}
	else
	{
		OnChoseFile();
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool
CFileBrowserList::IsFolder(unsigned int p_iIndex) const
{
	return  p_iIndex < m_iNumDirs  &&  
			GetSelectedItemAsString() != ".."  &&
		    GetSelectedItemAsString() != ".";
}


//---------------------------------------------------------------------------------------------------------------------
bool
CFileBrowserList::IsFile(unsigned int p_iIndex) const
{
	return	p_iIndex >= m_iNumDirs &&
			p_iIndex < m_iNumFiles + m_iNumDirs;
}


//---------------------------------------------------------------------------------------------------------------------
bool
CFileBrowserList::IsDrive(unsigned int p_iIndex) const
{
	return	p_iIndex >= m_iNumFiles + m_iNumDirs;
}

//---------------------------------------------------------------------------------------------------------------------
/// füllt die Liste mit dem Inhalt des aktuellen Verzeichnisses 
void 
CFileBrowserList::Fill()
{
	Clear();

	CStr sPhysPath = m_sCurrentPath + "*.*";
	vector<CStr> asDirs;
	vector<CStr> asFiles;

	_finddata_t	xFileInfo;
	intptr_t lFile; 
	
	lFile = _findfirst( sPhysPath.c_str(), &xFileInfo );
	if ( lFile != -1 )
	{
		do {
			if (xFileInfo.attrib & _A_SUBDIR)
			{					
				asDirs.push_back( xFileInfo.name );
			}
			else
			{
				// if a filter rule is defined
				if (m_sFilter.GetLength() > 0)
				{
					CStr sTmp = xFileInfo.name;
					int l = sTmp.FindReverse('.');
					sTmp = sTmp.Mid(l + 1);			// sTmp ist jetzt die Erweiterung

					if (m_asFilterExt.Find(sTmp) != -1)
					{
						asFiles.push_back( xFileInfo.name );
					}							
				}
				else
				{
					asFiles.push_back( xFileInfo.name );
				}
			}
		} while( _findnext( lFile, &xFileInfo ) == 0 );
		_findclose( lFile );
	}

	m_iNumDirs = (unsigned int) asDirs.size();

	unsigned int i;
	std::sort(asDirs.begin(), asDirs.end());
	for(i=0; i<asDirs.size(); ++i)
	{
		AddItem(asDirs[i], m_pxFolderBmp);
	}

	std::sort(asFiles.begin(), asFiles.end());
	for(i=0; i<asFiles.size(); ++i)
	{
		AddItem(asFiles[i], m_pxFileBmp);
	}
	m_iNumFiles = (unsigned int) asFiles.size();


	// Laufwerke

	m_iNumDrives = 0;
	if(m_sCurrentPath.GetLength() <= 3)
	{
		DWORD iDrives = ::GetLogicalDrives();
		char c;
		for(c='A'; c<='Z'; ++c)
		{
			if(iDrives & 1)
			{
				CBitmap* pBmp;
				CStr sDriveName = CStr::Create("%c", c) + ":\\";
				unsigned int iType = ::GetDriveType(sDriveName.c_str());

				char cBuffer[255];
				::GetVolumeInformation(sDriveName.c_str(), cBuffer, 255, NULL, NULL, NULL, NULL, NULL);
				CStr sVolumeName = cBuffer;

				switch(iType) 
				{
					case DRIVE_REMOVABLE:	pBmp = m_pxDriveFloppyBmp; break;
					case DRIVE_REMOTE:		pBmp = m_pxDriveNetworkBmp; break;
					case DRIVE_CDROM:		pBmp = m_pxDriveCDBmp; break;
					default:				pBmp = m_pxDriveFixBmp;
				}
				AddItem(CStr::Create("%c", c) + CStr(": ") + sVolumeName, pBmp);
				m_iNumDrives++;
			}
			iDrives >>= 1;
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
inline
void
CFileBrowserList::SetOnChoseFileCallback(CFunctionPointer1<CFileBrowserList*>& rxCallback)
{
	m_xOnChoseFileCallback = rxCallback;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CFileBrowserList::OnChoseFile()
{
	CWindowMgr::Get().PostMsg(CFileBrowserListChoseFileMsg(GetWHDL()), GetParent());
	if(m_xOnChoseFileCallback)
	{
		m_xOnChoseFileCallback(this);
	}
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
CStr
CFileBrowserList::GetDebugString() const		
{ 
	return "CFileBrowserList"; 
}

//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt den Wert eines benannten Attributes. Siehe UIlib Doku für eine vollständige Liste der Attribute.
	\param	p_rsName	Name des Attributes
	\param	p_rsValue	neuer Wert
	\return	true, wenn erfolgreich
*/
bool
CFileBrowserList::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName=="path")
	{
		m_sCurrentPath=p_rsValue;
		ValidatePath();
		Fill();
		return true;
	}
	return __super::SetAttrib(p_rsName,p_rsValue);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	liefert den Wert eines benannten Attributes. Siehe UILib Doku für eine vollständige Liste der Attribute. ues

	\param	p_rsName		Name des Attributes
	\param	po_srValue		Ausgabe-Parameter: String, der den Wert entgegennimmt
	\return true, wenn Wert zurückgeliefert wurde; false, wenn der Attributnamen nicht bekannt ist
*/
bool 
CFileBrowserList::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName=="path"){po_srValue=m_sCurrentPath;return true;}
	return __super::GetAttrib(p_rsName,po_srValue);
}
//---------------------------------------------------------------------------------------------------------------------


} // namespace UILib

