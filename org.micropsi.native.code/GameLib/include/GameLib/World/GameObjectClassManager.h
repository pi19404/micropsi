#pragma once
#ifndef GAMELIB_GAMEOBJCLASSMGR_H_INCLUDED
#define GAMELIB_GAMEOBJCLASSMGR_H_INCLUDED

#include <string>
#include <vector>
#include <map>

#include "GameObjectProperty.h"

class CGameObj;
class TiXmlElement;

class CGameObjClassMgr
{
public:
    
	class CClassInfo;
    typedef CGameObj* (__cdecl* CreateGameObjFunction)(const CClassInfo* pClassInfo);
    typedef void (__cdecl* DestroyGameObjFunction)(CGameObj* pElement);

    class CClassInfo
    {
    public:
		CClassInfo() {};
		~CClassInfo();

		CGameObjProperty*		FindProperty(const std::string p_rsPropertyName) const;

		std::string				m_sClassName;						///< Name der Klasse
        CreateGameObjFunction   m_fpCreateGameObj;					///< Funktion zum Erzeugen
        DestroyGameObjFunction  m_fpDestroyGameObj;					///< Funktion zum Löschen
        const CClassInfo*		m_pxParentClass;					///< Zeiger auf Elternklasse; darf 0 sein
		bool					m_bVirtual;							///< virtuelle Klasse?

		std::map<std::string, CGameObjProperty*>  m_mpxProperties;	///< Map mit Properties
    };

	CGameObjClassMgr();
    virtual ~CGameObjClassMgr();

    static CGameObjClassMgr&	Get();
    bool						ClassIsRegistered(const std::string& sClassName);

    typedef std::map<const std::string, CClassInfo*>::iterator GameObjClassIterator;
    void						StartIterateClasses(GameObjClassIterator& iter);
	bool						IterateClasses(GameObjClassIterator& iter, std::string& po_rsName);

    bool						RegisterGameObjClass(	const std::string& sClassName, 
														const std::string& sParentClass,
														CreateGameObjFunction fpCreateGameObj = 0, 
														DestroyGameObjFunction fpDestroyGameObj = 0,
														bool p_bVirtual = false);

	void 						AddPropertyBool(	const std::string& p_rsClassName, 
													const std::string& p_rsName, 
													const std::string& p_rsDescription, 
													bool p_bEditable, 
													bool p_bDefaultValue);

	void 						AddPropertyInt(		const std::string& p_rsClassName, 
													const std::string& p_rsName, 
													const std::string& p_rsDescription, 
													bool p_bEditable, 
													int p_iDefaultValue, int p_iMinValue, int p_iMaxValue);

	void 						AddPropertyFloat(	const std::string& p_rsClassName, 
													const std::string& p_rsName, 
													const std::string& p_rsDescription, 
													bool p_bEditable, 
													float p_fDefaultValue, float p_fMinValue, float p_fMaxValue);
    
	void 						AddPropertyString(	const std::string& p_rsClassName, 
													const std::string& p_rsName, 
													const std::string& p_rsDescription, 
													bool p_bEditable, 
													const std::string& p_rsDefaultValue);

	void 						AddPropertyEnum(	const std::string& p_rsClassName, 
													const std::string& p_rsName, 
													const std::string& p_rsDescription, 
													bool p_bEditable, 
													int	p_iDefaultIndex,
													const std::string& p_rsCommaSeparatedItemList);

	void 						AddPropertyVectorList(const std::string& p_rsClassName, 
													const std::string& p_rsName, 
													const std::string& p_rsDescription, 
													bool p_bEditable);

    CGameObj*					CreateGameObj(const std::string& sClassName);
    void						DestroyGameObj(CGameObj* pGameObj);

	bool						IsVirtualClass(const std::string p_rsName);

	bool						LoadClassDefinitionsXMLFile(const std::string& p_rsFilename);


protected:

	/// erzeugt Properties aus dem XML-File, kann überschrieben werden, um in Anwendungen neue Propertytypen zu unterstützen
	virtual void	AddProperty(CClassInfo* p_pxClassInfo, TiXmlElement* p_pxPropertyElement);

private:

    std::map<const std::string, CClassInfo*>   m_mClassInfos;
    static CGameObjClassMgr* m_spGameObjClassMgr;
	
	void			Clear();
	CClassInfo*		FindClass(const std::string p_rsName);
};

#include "GameObjectClassManager.inl"

#endif // ifdef GAMELIB_GAMEOBJCLASSMGR_H_INCLUDED

