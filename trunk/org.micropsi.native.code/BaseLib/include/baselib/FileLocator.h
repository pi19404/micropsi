
#pragma once

#ifndef FILELOCATOR_H_INCLUDED
#define FILELOCATOR_H_INCLUDED

#include <string>
#include <map>

class CFileLocator
{
public:
	CFileLocator();
	~CFileLocator();

	typedef std::string (*CallBackFunction) (const CFileLocator*, std::string, std::string, std::string, void*);

    static std::string      CompactPath(const std::string p_sFilename);                                 ///< entfernt substrings wie "./" oder "AA/../"
	static std::string		RemoveFileExtension(const std::string& p_rsFilename);
	static std::string		ExtractFilename(std::string p_sFilename);
	static std::string		ExtractPath(std::string p_sFilename);
	static std::string		ConcatPaths(const std::string& p_rsPath1, const std::string& p_rsPath2);
    static std::string      InsertBeforeFileExtension(std::string p_sFilename, const std::string& p_rsInsert);
    static std::string      InsertBeforeFilename(std::string p_sFilename, std::string p_sInsert);
    static bool             FileExists(const std::string& p_rsFilename);

	void			SetAlias(const std::string& p_rsVirtualPath, const std::string& p_rsPhysicalPath, 
							CallBackFunction p_pfnCallBack = 0, void* p_pCallBackUserParam = 0);

	std::string		GetPath(const std::string& p_rsPath) const;

    std::string		GetRoot() const;
    void			SetRoot(const std::string& p_sPath);


private:

	class CMapping
	{
	public:
		std::string			m_sPath;				///< Pfad auf den verwiesen wird (darf wieder wildcards enthalten)
		CallBackFunction	m_pfnCallBack;			///< Callback für bestimmte Verzeichnisse
		void*				m_pCallBackUserData;	///< Parameter, der dem Callback immer übergeben wird
	};

    std::string m_sRoot;
	std::map<std::string, CMapping>	m_mPathMapping;

	static void		ReplaceSlashes(std::string& p_rsPath);
	std::string		SubsitutePathMapping(const std::string& p_rsAlias, const std::string& p_rsRemainingPath) const;

};

#endif // FILELOCATOR_H_INCLUDED
