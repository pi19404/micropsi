#ifndef GAMELIB_CONFIGFILE_H_INCLUDED
#define GAMELIB_CONFIGFILE_H_INCLUDED

#include "BaseLib/configfile.h"
#include "baselib/geometry/CVector.h"

class CExtendedConfigFile : public CConfigFile
{
public:

	CExtendedConfigFile();
	virtual ~CExtendedConfigFile();

	bool		AddParameterVec3(std::string p_sPath, std::string p_sDescription, CVec3 p_vDefaultValue, CVec3 p_vMinValue, CVec3 p_vMaxValue);
	bool		SetValueVec3(std::string p_sPath, const CVec3& p_rvValue);
	CVec3		GetValueVec3(std::string p_sPath) const;

protected: 

	const static CFourCC	ID_VEC3;

	class CEntryVec3 : public CEntry
	{
	public:
		CEntryVec3() : CEntry(ID_VEC3) {}			

		CVec3				m_vValue;
		CVec3				m_vDefaultValue;
		CVec3				m_vMinValue;
		CVec3				m_vMaxValue;
	};

	/// helper function; used by Load()
	virtual void			ReadValue(TiXmlElement* p_pxXmlElement, const char* p_pcKey, CEntry* p_pxEntry);

	/// helper function; used by Save()
	virtual void			WriteValueAndComment(TiXmlElement* p_pxXmlElement, const char* p_pcKey, CEntry* p_pxEntry);

	/// helper function; used by ReadFromCommandLine()
	virtual void			ReadValueFromString(const std::string& p_rsValue, CEntry* p_pxEntry);

	/// validates a single entry
	virtual void			Validate(CEntry* p_pxEntry);
};


#endif // ifndef GAMELIB_CONFIGFILE_H_INCLUDED

