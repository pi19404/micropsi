#include "stdafx.h"

#include "GameLib/World/GameObjectClassManager.h"
#include "GameLib/World/GameObject.h"
#include "GameLib/World/GameObjectProperties.h"
#include "e42/core/ModelFactory.h"

#include "tinyxml.h"
#include "GameLib/Utilities/XMLUtils.h"

using std::string;
using std::map;

//---------------------------------------------------------------------------------------------------------------------
CGameObjClassMgr* CGameObjClassMgr::m_spGameObjClassMgr = 0;

//---------------------------------------------------------------------------------------------------------------------
CGameObjClassMgr::CClassInfo::~CClassInfo()
{
	if(m_mpxProperties.size() > 0)
	{
		map<string, CGameObjProperty*>::const_iterator i;
		for(i=m_mpxProperties.begin(); i!=m_mpxProperties.end(); i++)
		{
			i->second->Delete();
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty* 
CGameObjClassMgr::CClassInfo::FindProperty(const std::string p_rsPropertyName) const
{
	if(m_mpxProperties.size() == 0)
	{
		return 0;
	}

	map<string, CGameObjProperty*>::const_iterator i;
	i = m_mpxProperties.find(p_rsPropertyName);
	if(i != m_mpxProperties.end())
	{
		return i->second;
	}
	else
	{
		return 0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
CGameObjClassMgr::CGameObjClassMgr()
{
    assert(m_spGameObjClassMgr == 0);
    m_spGameObjClassMgr = this;

	CClassInfo* pxClassInfo = new CClassInfo();
	m_mClassInfos["gameobj"] = pxClassInfo;

	pxClassInfo->m_sClassName		= "gameobj";
	pxClassInfo->m_pxParentClass	= 0;
	pxClassInfo->m_fpCreateGameObj	= 0;
    pxClassInfo->m_fpDestroyGameObj = 0;
	pxClassInfo->m_bVirtual			= true;
}

//---------------------------------------------------------------------------------------------------------------------
CGameObjClassMgr::~CGameObjClassMgr()
{
	Clear();
	m_spGameObjClassMgr = 0;
}

//---------------------------------------------------------------------------------------------------------------------
CGameObjClassMgr& 
CGameObjClassMgr::Get()
{
    assert(m_spGameObjClassMgr);
    return *m_spGameObjClassMgr;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	löscht alle Informationen über registrierte Klassen
*/
void
CGameObjClassMgr::Clear()
{
	if(!m_mClassInfos.empty())
	{
	    map<const string, CClassInfo*>::iterator i;
		for(i=m_mClassInfos.begin(); i!=m_mClassInfos.end(); i++)
		{
			delete i->second;
		}
		m_mClassInfos.clear();
	}
}

//---------------------------------------------------------------------------------------------------------------------
CGameObjClassMgr::CClassInfo*		
CGameObjClassMgr::FindClass(const std::string p_rsName)
{
    if (m_mClassInfos.empty()) 
    {
        return 0;
    }

    const map<const string, CClassInfo*>::const_iterator iter = m_mClassInfos.find(p_rsName);

    if (iter != m_mClassInfos.end())
    {
        return iter->second;
    }

    return 0;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CGameObjClassMgr::StartIterateClasses(GameObjClassIterator& iter)
{
    iter = m_mClassInfos.begin();
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CGameObjClassMgr::IterateClasses(GameObjClassIterator& iter, std::string& po_rsName)
{
    if ((m_mClassInfos.empty()) ||
        (iter == m_mClassInfos.end()))
    {
        return false;
    }

    po_rsName = iter->first;
	iter++;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CGameObjClassMgr::RegisterGameObjClass(const string& sClassName, const std::string& sParentClass,
    CreateGameObjFunction fpCreateGameObj, DestroyGameObjFunction fpDestroyGameObj, bool p_bVirtual)
{
	if(ClassIsRegistered(sClassName))
	{
	    assert(false);
		return false;
	}

	CClassInfo* pxParentClass = FindClass(sParentClass);
	assert(pxParentClass);
	if(!pxParentClass)
	{
		return false;
	}

	CClassInfo* pxClassInfo = new CClassInfo();
	m_mClassInfos[sClassName] = pxClassInfo;

	pxClassInfo->m_sClassName		= sClassName;
	pxClassInfo->m_pxParentClass	= pxParentClass;
    pxClassInfo->m_fpCreateGameObj	= fpCreateGameObj;
    pxClassInfo->m_fpDestroyGameObj = fpDestroyGameObj;
	pxClassInfo->m_bVirtual			= p_bVirtual;

	return true;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CGameObjClassMgr::AddPropertyBool(	const std::string& p_rsClassName, 
									const string& p_rsName, const std::string& p_rsDescription, 
									bool p_bEditable, bool p_bDefaultValue)
{
	CClassInfo* p = FindClass(p_rsClassName);
	assert(p);
	if(!p)
	{
		return;
	}

	CGameObjPropertyBool* pxProperty = CGameObjPropertyBool::Create(p_rsName);
	pxProperty->m_sDescription	= p_rsDescription;
	pxProperty->m_bEditable		= p_bEditable;
	pxProperty->m_bDefaultValue = p_bDefaultValue;
	pxProperty->m_iIndex		= (int) p->m_mpxProperties.size();
	p->m_mpxProperties[p_rsName]= pxProperty;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CGameObjClassMgr::AddPropertyInt(	const std::string& p_rsClassName, 
									const string& p_rsName, const std::string& p_rsDescription, 
									bool p_bEditable, int p_iDefaultValue, int p_iMinValue, int p_iMaxValue)
{
	CClassInfo* p = FindClass(p_rsClassName);
	assert(p);
	if(!p)
	{
		return;
	}

	CGameObjPropertyInt* pxProperty = CGameObjPropertyInt::Create(p_rsName);
	pxProperty->m_sDescription	= p_rsDescription;
	pxProperty->m_bEditable		= p_bEditable;
	pxProperty->m_iDefaultValue = p_iDefaultValue;
	pxProperty->m_iMinValue		= p_iMinValue;
	pxProperty->m_iMaxValue		= p_iMaxValue;
	pxProperty->m_iIndex		= (int) p->m_mpxProperties.size();
	p->m_mpxProperties[p_rsName]= pxProperty;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CGameObjClassMgr::AddPropertyFloat(	const std::string& p_rsClassName, 
									const string& p_rsName, const std::string& p_rsDescription, 
									bool p_bEditable, float p_fDefaultValue, float p_fMinValue, float p_fMaxValue)
{
	CClassInfo* p = FindClass(p_rsClassName);
	assert(p);
	if(!p)
	{
		return;
	}

	CGameObjPropertyFloat* pxProperty = CGameObjPropertyFloat::Create(p_rsName);
	pxProperty->m_sDescription	= p_rsDescription;
	pxProperty->m_bEditable		= p_bEditable;
	pxProperty->m_fDefaultValue = p_fDefaultValue;
	pxProperty->m_fMinValue		= p_fMinValue;
	pxProperty->m_fMaxValue		= p_fMaxValue;
	pxProperty->m_iIndex		= (int) p->m_mpxProperties.size();
	p->m_mpxProperties[p_rsName]= pxProperty;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CGameObjClassMgr::AddPropertyString(	const std::string& p_rsClassName, 
										const string& p_rsName, const string& p_rsDescription, bool 
										p_bEditable, const string& p_rsDefaultValue)
{
	CClassInfo* p = FindClass(p_rsClassName);
	assert(p);
	if(!p)
	{
		return;
	}

	CGameObjPropertyString* pxProperty = CGameObjPropertyString::Create(p_rsName);
	pxProperty->m_sDescription	= p_rsDescription;
	pxProperty->m_bEditable		= p_bEditable;
	pxProperty->m_sDefaultValue = p_rsDefaultValue;
	pxProperty->m_iIndex		= (int) p->m_mpxProperties.size();
	p->m_mpxProperties[p_rsName]= pxProperty;
}

//---------------------------------------------------------------------------------------------------------------------
void 						
CGameObjClassMgr::AddPropertyEnum(	const std::string& p_rsClassName, 
									const std::string& p_rsName, const std::string& p_rsDescription, 
									bool p_bEditable, int p_iDefaultIndex, const std::string& p_rsCommaSeparatedItemList)
{
	CClassInfo* p = FindClass(p_rsClassName);
	assert(p);
	if(!p)
	{
		return;
	}

	CGameObjPropertyEnum* pxProperty = CGameObjPropertyEnum::Create(p_rsName);
	pxProperty->m_iDefaultIndex = p_iDefaultIndex;
	pxProperty->m_iIndex		= (int) p->m_mpxProperties.size();
	p->m_mpxProperties[p_rsName]= pxProperty;
}

//---------------------------------------------------------------------------------------------------------------------
void 						
CGameObjClassMgr::AddPropertyVectorList(	const std::string& p_rsClassName, 
											const std::string& p_rsName, const std::string& p_rsDescription, bool p_bEditable)
{
	CClassInfo* p = FindClass(p_rsClassName);
	assert(p);
	if(!p)
	{
		return;
	}

	CGameObjPropertyVectorList* pxProperty = CGameObjPropertyVectorList::Create(p_rsName);
	pxProperty->m_iIndex		= (int) p->m_mpxProperties.size();
	p->m_mpxProperties[p_rsName]= pxProperty;
}

//---------------------------------------------------------------------------------------------------------------------
CGameObj* 
CGameObjClassMgr::CreateGameObj(const string& sClassName)
{
	const CClassInfo* pxClassInfo = FindClass(sClassName);
	assert(pxClassInfo);
	if(!pxClassInfo)
	{
		DebugPrint("Error: unknown game object class %s", sClassName.c_str());
		assert(false);
		return 0;
	}

	assert(!pxClassInfo->m_bVirtual);
	if(pxClassInfo->m_bVirtual)
	{
		DebugPrint("Error: game object class %s is virtual, cannot create instance", sClassName.c_str());
		assert(false);
		return 0;
	}
   
	// Hierarchie aufwärts durchsuchen, bis Create-Methode gefunden ist

	CreateGameObjFunction fpCreateGameObj = 0;
	const CClassInfo* pxCppClassInfo = pxClassInfo;
	do
	{
		fpCreateGameObj = pxCppClassInfo->m_fpCreateGameObj;
		if(fpCreateGameObj) break;
		pxCppClassInfo = pxCppClassInfo->m_pxParentClass;
	} while(pxCppClassInfo);

	assert(fpCreateGameObj);

	if(fpCreateGameObj)
	{
	    CGameObj* pGameObj = fpCreateGameObj(pxClassInfo);
	    return pGameObj;
	}
	else
	{
		DebugPrint("Error: no create method found for game object class %s", sClassName.c_str());
		assert(false);
		return 0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CGameObjClassMgr::DestroyGameObj(CGameObj* pGameObj)
{
	const CClassInfo* pxClassInfo = pGameObj->GetClassInfo();
	assert(pxClassInfo);
   
	// Hierarchie aufwärts durchsuchen, bis Create-Methode gefunden ist

	DestroyGameObjFunction fpDestroyGameObj;
	const CClassInfo* pxCppClassInfo = pxClassInfo;
	do
	{
		fpDestroyGameObj = pxCppClassInfo->m_fpDestroyGameObj;
		if(fpDestroyGameObj) break;
		pxCppClassInfo = pxCppClassInfo->m_pxParentClass;
	} while(pxCppClassInfo);

	assert(fpDestroyGameObj);
	if(fpDestroyGameObj)
	{
		fpDestroyGameObj(pGameObj);
	}
}
//---------------------------------------------------------------------------------------------------------------------
bool
CGameObjClassMgr::IsVirtualClass(const string p_rsName)
{
	const CClassInfo* pxClassInfo = FindClass(p_rsName);
	assert(pxClassInfo);
	return pxClassInfo->m_bVirtual;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CGameObjClassMgr::LoadClassDefinitionsXMLFile(const string& p_rsFilename)
{
	bool bSuccess = false;

	TiXmlDocument* pxDoc = new TiXmlDocument(p_rsFilename.c_str());
	if (pxDoc->LoadFile()) 
	{ 
		TiXmlNode* pxRootNode = pxDoc->FirstChild("classdefinitions");
		if(pxRootNode) 
		{
			bSuccess = true;
            TiXmlElement* pxClassNode = pxRootNode->FirstChildElement("class");
			while(pxClassNode)
			{
				string sName	= XMLUtils::GetXMLTagString(pxClassNode, "name", "noname");
				string sParent  = XMLUtils::GetXMLTagString(pxClassNode, "parent", "gameobj");
				bool bVirtual	= XMLUtils::GetXMLTagBool(pxClassNode, "virtual", false);

				bool bSuccess = RegisterGameObjClass(sName, sParent, 0, 0, bVirtual);

				if(bSuccess)
				{
					CClassInfo* pxClassInfo = FindClass(sName);

					// Properties von der Basisklasse erben

					const CClassInfo* pxParentClass = pxClassInfo->m_pxParentClass;
					if(pxParentClass  &&  pxParentClass->m_mpxProperties.size() > 0)
					{
						map<string, CGameObjProperty*>::const_iterator i;
						for(i=pxParentClass->m_mpxProperties.begin(); i!=pxParentClass->m_mpxProperties.end(); i++)
						{
							string sPropName = i->second->m_sName;
							pxClassInfo->m_mpxProperties[sPropName] = i->second->Clone();
						}
					}
					
					// eigene Properties lesen und hinzufügen bzw. überschreiben

		            TiXmlElement* pxPropertyNode = pxClassNode->FirstChildElement("property");
					while(pxPropertyNode)
					{
						AddProperty(pxClassInfo, pxPropertyNode);
						pxPropertyNode = pxPropertyNode->NextSiblingElement("property");
					}
				}

				pxClassNode = pxClassNode->NextSiblingElement("class");
			}
		}
	}

	delete pxDoc;
	return bSuccess;
}

//---------------------------------------------------------------------------------------------------------------------
void
CGameObjClassMgr::AddProperty(CGameObjClassMgr::CClassInfo* p_pxClassInfo, TiXmlElement* p_pxPropertyElement)
{
	string sName = XMLUtils::GetXMLTagString(p_pxPropertyElement, "name", "");
	string sType = XMLUtils::GetXMLTagString(p_pxPropertyElement, "type", "");

	assert(!sName.empty());
	assert(!sType.empty());
	if(sName.empty()  ||  sType.empty())
	{
		return;
	}

	CGameObjProperty* pxProperty = p_pxClassInfo->FindProperty(sName);
	if(pxProperty)
	{
		// Property existiert schon 

		if(pxProperty->GetType() != sType)
		{
			assert(false);		// Property überschrieben, aber mit anderem Typ :(
			return;
		}

		pxProperty->FromXMLElement(p_pxPropertyElement);
	}
	else
	{
		// Property ist neu

		if(sType == CGameObjPropertyBool::GetTypeName())
		{
			pxProperty = CGameObjPropertyBool::Create(sName);
		}
		else if(sType == CGameObjPropertyInt::GetTypeName())
		{
			pxProperty = CGameObjPropertyInt::Create(sName);
		}
		else if(sType == CGameObjPropertyString::GetTypeName())
		{
			pxProperty = CGameObjPropertyString::Create(sName);
		}
		else
		{
			assert(false);		// unbekannter Property-Typ
			return;
		}

		pxProperty->FromXMLElement(p_pxPropertyElement);
		pxProperty->m_iIndex = (int) p_pxClassInfo->m_mpxProperties.size();
		p_pxClassInfo->m_mpxProperties[sName] = pxProperty;
	}
}

//---------------------------------------------------------------------------------------------------------------------
