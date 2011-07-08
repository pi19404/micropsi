/**
	Benutzung:

	// Zuerst alle bekannten Parameter hinzufügen, dabei Kommentar, Wertebereich und Defaultwert angeben:

	CConfigfile xConfig;
	m_xConfiguration.AddParameterBool("application/fullscreen", "Determines whether the application should start in fullscreen mode (as opposed to windowed mode)", false);

	// Jetzt Datei laden
	// Parameter, die nicht bekannt sind, werden ignoriert; was in der Datei nicht definiert ist, bekommt den Defaultwert
	// wenn die Datei nicht existiert, ist das auch ok, dann haben alle Parameter ihren Defaultwert

	m_xConfiguration.Load("configuration.xml");

	// Datei noch mal speichern, damit der User eine vollständige Datei hat und alle Parameter darin gültige Werte haben:

	m_xConfiguration.Save("configuration.xml");

	// Parameter abfragen

	bool bFullScreen = m_xConfiguration.GetValueBool("application/fullscreen");
*/

#pragma once
#ifndef BASELIB_CONFIGFILE_H_INCLUDED
#define BASELIB_CONFIGFILE_H_INCLUDED

#include <string>
#include <map>
#include <vector>

#include "baselib/fourcc.h"
#include "baselib/pnt.h"
#include "baselib/size.h"
#include "baselib/color.h"

class TiXmlElement;

class CConfigFile
{
public:

	CConfigFile();
	virtual ~CConfigFile();

	void		Clear();

	bool		Load(const char* p_pcXMLFilename);
	bool		Save(const char* p_pcXMLFilename = 0);

	bool		ReadFromCommandLine(const char* p_pcCommandLine);

	bool		AddParameterString(std::string p_sPath, std::string p_sDescription, std::string p_sDefaultValue, std::string p_sPossibleValues = "");
	bool		AddParameterInt(std::string p_sPath, std::string p_sDescription, int p_iDefaultValue, int p_iMinValue, int p_iMaxValue);
	bool		AddParameterFloat(std::string p_sPath, std::string p_sDescription, float p_fDefaultValue, float p_fMinValue, float p_fMaxValue);
	bool		AddParameterBool(std::string p_sPath, std::string p_sDescription, bool p_bDefaultValue);
	bool		AddParameterPoint(std::string p_sPath, std::string p_sDescription, CPnt p_xDefaultValue, CPnt p_xMinValue, CPnt p_xMaxValue);
	bool		AddParameterSize(std::string p_sPath, std::string p_sDescription, CSize p_xDefaultValue, CSize p_xMinValue, CSize p_xMaxValue);
	bool		AddParameterColor(std::string p_sPath, std::string p_sDescription, CColor p_xDefaultValue, CColor p_xMinValue, CColor p_xMaxValue);

	bool		SetValueInt(std::string p_sPath, int p_iValue);
	bool		SetValueFloat(std::string p_sPath, float p_fValue);
	bool		SetValueString(std::string p_sPath, std::string p_sValue);
	bool		SetValueBool(std::string p_sPath, bool p_bValue);
	bool		SetValuePoint(std::string p_sPath, const CPnt& p_rxValue);
	bool		SetValueSize(std::string p_sPath, const CSize& p_rxValue);
	bool		SetValueColor(std::string p_sPath, const CColor& p_rxValue);

	int			GetValueInt(std::string p_sPath) const;
	float		GetValueFloat(std::string p_sPath) const;
	std::string	GetValueString(std::string p_sPath) const;
	bool		GetValueBool(std::string p_sPath) const;
	CPnt		GetValuePoint(std::string p_sPath) const;
	CSize		GetValueSize(std::string p_sPath) const;
	CColor		GetValueColor(std::string p_sPath) const;


protected: 

	const static CFourCC	ID_INT;
	const static CFourCC	ID_FLOAT;
	const static CFourCC	ID_STRING;
	const static CFourCC	ID_BOOL;
	const static CFourCC	ID_POINT;
	const static CFourCC	ID_SIZE;
	const static CFourCC	ID_COLOR;


	/// Base Class for all Parameter Values
	class CEntry
	{
	public:
		CEntry(CFourCC p_xType) : m_bWriteComment(true), m_xType(p_xType) {}	
		virtual ~CEntry() {};

		CFourCC				m_xType;			///< data type of entry
		bool				m_bWriteComment;	///< write comments for this parameter to xml file?
		std::string			m_sPath;			///< path and name where of parameter (for instance "soundenabled" or "sound/soundenabled")
		std::string			m_sDescription;		///< textual description of parameters; used as comments in xml file
	};

	class CEntryInt : public CEntry
	{
	public:
		CEntryInt() : CEntry(ID_INT) {}			

		int					m_iValue;
		int					m_iDefaultValue;
		int					m_iMinValue;
		int					m_iMaxValue;
	};

	class CEntryFloat : public CEntry
	{
	public:
		CEntryFloat() : CEntry(ID_FLOAT) {}			

		float				m_fValue;
		float				m_fDefaultValue;
		float				m_fMinValue;
		float				m_fMaxValue;
	};

	class CEntryBool : public CEntry
	{
	public:
		CEntryBool() : CEntry(ID_BOOL) {}			

		bool				m_bValue;
		bool				m_bDefaultValue;
	};

	class CEntryString : public CEntry
	{
	public:
		CEntryString() : CEntry(ID_STRING) {}			

		std::string					m_sDefaultValue;
		std::string					m_sValue;
		std::vector<std::string>	m_asPossibleValues;
	};

	class CEntryPoint : public CEntry
	{
	public:
		CEntryPoint() : CEntry(ID_POINT) {}			

		CPnt				m_xValue;
		CPnt				m_xDefaultValue;
		CPnt				m_xMinValue;
		CPnt				m_xMaxValue;
	};

	class CEntrySize : public CEntry
	{
	public:
		CEntrySize() : CEntry(ID_SIZE) {}			

		CSize				m_xValue;
		CSize				m_xDefaultValue;
		CSize				m_xMinValue;
		CSize				m_xMaxValue;
	};

	class CEntryColor : public CEntry
	{
	public:
		CEntryColor() : CEntry(ID_COLOR) {}			

		CColor				m_xValue;
		CColor				m_xDefaultValue;
		CColor				m_xMinValue;
		CColor				m_xMaxValue;
	};

	std::map<std::string, CEntry*>	m_axParameters;						///< contains all parameters

	/// helper functions; retrieves the value for a given key from a command line
	std::string				GetCommandLineValue(const std::string& sKey, const char* pcCommandLine) const;

	/// helper function; used by Load()
	virtual void			ReadValue(TiXmlElement* p_pxXmlElement, const char* p_pcKey, CEntry* p_pxEntry);

	/// helper function; used by Save()
	virtual void			WriteValueAndComment(TiXmlElement* p_pxXmlElement, const char* p_pcKey, CEntry* p_pxEntry);

	/// helper function; used by ReadFromCommandLine()
	virtual void			ReadValueFromString(const std::string& p_rsValue, CEntry* p_pxEntry);

	/// validates the values of all parameters
	void					Validate();

	/// validates a single entry
	virtual void			Validate(CEntry* p_pxEntry);

	/// finds entry with given path and type
	const CEntry*			FindEntry(const std::string& p_sPath, CFourCC p_xType) const;

	/// finds entry with given path and type
	CEntry*					FindEntry(const std::string& p_sPath, CFourCC p_xType);

	/// finds entry with given path
	const CEntry*			FindEntry(const std::string& p_sPath) const;
};


#endif // ifndef BASELIB_CONFIGFILE_H_INCLUDED

