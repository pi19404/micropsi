#include "stdafx.h"

#include <assert.h>

#include "baselib/FileLocator.h"
#include "baselib/utils.h"

using std::string;
using std::map;

//-----------------------------------------------------------------------------------------------------------------------------------------
CFileLocator::CFileLocator()
{
	m_sRoot = "";
}


//-----------------------------------------------------------------------------------------------------------------------------------------
CFileLocator::~CFileLocator()
{
}


//-----------------------------------------------------------------------------------------------------------------------------------------
void
CFileLocator::ReplaceSlashes(string& p_rsPath)
{
    for (unsigned int iPos = 0; iPos < p_rsPath.size(); iPos++)
    {
        if (p_rsPath[iPos] == '\\') p_rsPath[iPos] = '/';
    }
}


//-----------------------------------------------------------------------------------------------------------------------------------------
string      
CFileLocator::CompactPath(std::string p_sFilename)
{
	CFileLocator::ReplaceSlashes(p_sFilename);
    
#ifndef UNIX				// haha
    Utils::StrToLower(p_sFilename);
#endif // UNIX

    size_t pos;


    // Laufwerksbuchstaben vorrübergehend entfernen
    string sDrive;
    if (p_sFilename.length() >= 2 &&
        p_sFilename[1] == ':')
    {
        sDrive = p_sFilename.substr(0, 2);
        p_sFilename.erase(0, 2);
    }


    // alle "/./" durch "/" ersetzen
    while ((pos = p_sFilename.find("/./")) != string::npos)
    {
        p_sFilename.replace(pos, 3, "/");
    }


    // "./" am Anfang löschen (nur am Anfang, da "./" substring von "../" ist
    if (p_sFilename.find("./") == 0)
    {
        p_sFilename.erase(0, 2);
    }


    // Annahme: alle im Pfad enthaltenen Ordnernamen sind entweder "../" oder gehen eine ebene tiefer

    size_t stSearchStart = p_sFilename.find("/", 1);
    do
    {
        // das nächstes "../" suchen
        size_t stUpperDirPos = p_sFilename.find("../", stSearchStart);

        stSearchStart = stUpperDirPos;

        // falls eins existiert
        if (stUpperDirPos != string::npos)
        {
            // die Stelle suchen, an dem der Ordner-Name davor beginnt
            size_t stPathBegin = p_sFilename.find_last_of("/", stUpperDirPos - 2);

            if (stPathBegin == string::npos)
            {
                // falls nichts gefunden wurde, fängt der OrdnerName am StringAnfang an 
                stPathBegin = 0;
            }
            else
            {
                // falls ein / gefunden wurde, dann fängt der Ordnername dahinter an
                stPathBegin += 1;
            }

            if (p_sFilename.substr(stPathBegin, 3) == "../")
            {
                // keine tiefere Ebene; weiter hinten suchen
                stSearchStart += 3;
            }
            else
            {
                // ab Anfang des Ordnernamen bis hinter das "../" alles löschen
                p_sFilename.erase(stPathBegin, (stUpperDirPos + 3) - stPathBegin);

                // nochmal von vorne anfangen
                stSearchStart = p_sFilename.find("/", 1);
            }
        }
    }
    while (stSearchStart != string::npos);

    return sDrive + p_sFilename;
}


//-----------------------------------------------------------------------------------------------------------------------------------------
string
CFileLocator::RemoveFileExtension(const string& p_rsFilename)
{
    int iPos = (int)p_rsFilename.rfind('.');

    if (iPos == string::npos)
    {
        return p_rsFilename;
    }
    else
    {
        return p_rsFilename.substr(0, iPos);
    }
}


//-----------------------------------------------------------------------------------------------------------------------------------------
bool 
CFileLocator::FileExists(const string& p_rsFilename)
{
    if (p_rsFilename.empty())
    {
        return false;
    }

    FILE* pF = fopen(p_rsFilename.c_str(), "rb");       // FIXME: geht das nicht schöner?

    if (pF)
    {
        fclose(pF);
        return true;
    }

    return false;
}


//-----------------------------------------------------------------------------------------------------------------------------------------
string		
CFileLocator::ExtractFilename(string p_sFilename)
{
	CFileLocator::ReplaceSlashes(p_sFilename);

    size_t stLastSlashPos =         p_sFilename.rfind("/");
    size_t stLastGreaterThanPos =   p_sFilename.rfind(">");

    int iLastSlashPos = (stLastSlashPos == string::npos) ? -1 : (int)stLastSlashPos;
    int iLastGreaterThanPos = (stLastGreaterThanPos == string::npos) ? -1 : (int)stLastGreaterThanPos;

    int iFilenameBegin = iLastSlashPos > iLastGreaterThanPos ? iLastSlashPos : iLastGreaterThanPos;

    size_t stFilenameBegin = (iFilenameBegin == -1) ? string::npos : (size_t)iFilenameBegin;

	if (stFilenameBegin != string::npos)
    {
        return p_sFilename.substr(stFilenameBegin + 1, p_sFilename.length() - stFilenameBegin - 1);
    }
    else
    {
        return p_sFilename;
    }
}


//-----------------------------------------------------------------------------------------------------------------------------------------
string		
CFileLocator::ExtractPath(string p_sFilename)
{
	CFileLocator::ReplaceSlashes(p_sFilename);

    size_t stLastSlashPos =         p_sFilename.rfind("/");
    size_t stLastGreaterThanPos =   p_sFilename.rfind(">");

    int iLastSlashPos = (stLastSlashPos == string::npos) ? -1 : (int)stLastSlashPos;
    int iLastGreaterThanPos = (stLastGreaterThanPos == string::npos) ? -1 : (int)stLastGreaterThanPos;

    int iFilenameBegin = iLastSlashPos > iLastGreaterThanPos ? iLastSlashPos : iLastGreaterThanPos;

    size_t stFilenameBegin = (iFilenameBegin == -1) ? string::npos : (size_t)iFilenameBegin;

	if (stFilenameBegin != string::npos)
    {
        return p_sFilename.substr(0, stFilenameBegin + 1);
    }
    else
    {
        return p_sFilename;
    }
}


//-----------------------------------------------------------------------------------------------------------------------------------------
string		
CFileLocator::ConcatPaths(const string& p_rsPath1, const string& p_rsPath2) 
{
	if(p_rsPath1.length() == 0)		{ return p_rsPath2; }
	if(p_rsPath2.length() == 0)		{ return p_rsPath1; }

	int iLen1 = (int) p_rsPath1.length();
	if(p_rsPath1[iLen1-1] == '/'   &&  p_rsPath2[0] == '/')
	{
		return p_rsPath1.substr(0, iLen1-2) + p_rsPath2;
	}
	else if(p_rsPath1[iLen1-1] != '/'   &&  p_rsPath2[0] != '/')
	{
		return p_rsPath1 + "/" + p_rsPath2;
	}
	else
	{
		return p_rsPath1 + p_rsPath2;
	}
}


//-----------------------------------------------------------------------------------------------------------------------------------------
string 
CFileLocator::InsertBeforeFileExtension(string p_sFilename, const string& p_rsInsert)
{
    size_t xPos = p_sFilename.find_last_of("\\/>.");

    if (xPos == string::npos ||
        p_sFilename[xPos] != '.')
    {
        // keine FileExtension gefunden
        return p_sFilename + p_rsInsert;
    }
    else
    {
        return p_sFilename.insert(xPos, p_rsInsert);
    }
}

    
//-----------------------------------------------------------------------------------------------------------------------------------------
string 
CFileLocator::InsertBeforeFilename(string p_sFilename, string p_sInsert)
{
    if (p_sInsert.empty()) 
    {
        return p_sFilename;
    }

    // evtl. einzufügenden String um '/' ergänzen
    const char cLastChar = p_sInsert[p_sInsert.size() - 1];

    if (cLastChar != '\\' && 
        cLastChar != '/')
    {
        p_sInsert += '/';
    }

    // Stelle zum einfügen suchen
    size_t xPos = p_sFilename.find_last_of("\\/>");

    if (xPos == string::npos)
    {
        // steht kein Pfad davor
        return p_sInsert + p_sFilename;
    }
    else
    {
        return p_sFilename.insert(xPos + 1, p_sInsert);
    }
}

    
//-----------------------------------------------------------------------------------------------------------------------------------------
string		
CFileLocator::SubsitutePathMapping(const string& p_rsAlias, const string& p_rsRemainingPath) const
{
	if(!m_mPathMapping.empty())
	{
		map<string, CMapping>::const_iterator i;
		i = m_mPathMapping.find(p_rsAlias);

		if(i != m_mPathMapping.end())
		{
			if(i->second.m_pfnCallBack)
			{
				return i->second.m_pfnCallBack(this, p_rsAlias, p_rsRemainingPath, GetPath(i->second.m_sPath), i->second.m_pCallBackUserData); 
			}
			else
			{
				return ConcatPaths(i->second.m_sPath, p_rsRemainingPath);
			}
		}
	}

	// kein Eintrag --> default ist ""
	return p_rsRemainingPath;
}


//-----------------------------------------------------------------------------------------------------------------------------------------
void			
CFileLocator::SetAlias(const string& p_rsVirtualPath, const string& p_rsPhysicalPath, 
					  CallBackFunction p_pfnCallBack, void* p_pCallBackUserParam)
{
	assert(p_rsVirtualPath.find_first_of(":<>/\\|?*\"") == string::npos);		// ungültiges Zeichen im Alias

	CMapping m;
	m.m_sPath = p_rsPhysicalPath;
	m.m_pfnCallBack = p_pfnCallBack;
	m.m_pCallBackUserData = p_pCallBackUserParam;
	m_mPathMapping[p_rsVirtualPath] = m;
}


//-----------------------------------------------------------------------------------------------------------------------------------------
string		
CFileLocator::GetPath(const string& p_rsPath) const
{
	string sPath = p_rsPath;

	size_t iSplitPos = (int) sPath.find(">");
	while(iSplitPos != string::npos)
	{	
		string sAlias = sPath.substr(0, iSplitPos);
		string sRemainingPath = sPath.substr(iSplitPos +1, sPath.length() - (iSplitPos+1)); 

		sPath = SubsitutePathMapping(sAlias, sRemainingPath);

		iSplitPos = (int) sPath.find(">");
	}

    return CompactPath(sPath);
}


//-----------------------------------------------------------------------------------------------------------------------------------------
string		
CFileLocator::GetRoot() const
{
	return m_sRoot;
}


//-----------------------------------------------------------------------------------------------------------------------------------------
void			
CFileLocator::SetRoot(const string& p_sPath)
{
	m_sRoot = p_sPath;
}


//-----------------------------------------------------------------------------------------------------------------------------------------
