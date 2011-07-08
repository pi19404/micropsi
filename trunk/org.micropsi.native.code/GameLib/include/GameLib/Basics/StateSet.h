#pragma once

#ifndef STATESET_H_INCLUDED
#define STATESET_H_INCLUDED

#include <string>
#include <map>

class TiXmlElement;

class CStateSet
{
protected:

    class CStateInfo
    {
    public:
        std::string     m_sType;
        std::string     m_sValue;
    };

    std::map<const std::string, CStateInfo> m_xStates;

public:

	CStateSet();
    virtual ~CStateSet();

    // state-kram
	typedef std::map<const std::string, CStateInfo>::const_iterator StateIterator;

    void StartIterateStates(StateIterator& iter) const;
    bool IterateStates(StateIterator& iter, std::string& po_rsKey) const;

    void Clear();

    std::string		GetStateType(const std::string& p_sKey) const;
    std::string		GetStateValue(const std::string& p_sKey) const;
	int				GetStateValueInt(const std::string& p_sKey) const;
	float			GetStateValueFloat(const std::string& p_sKey) const;

	bool KeyExists(const std::string& p_sKey) const;
    void SetStateValue(const std::string& p_sKey, const std::string& sValue);
	void SetStateValue(const std::string& p_sKey, int p_iValue);
	void SetStateValue(const std::string& p_sKey, float p_fValue);

	void AddKey(const std::string& sKey, const std::string& sType, const std::string& sInitialValue);

	void FromXMLElement(const TiXmlElement* p_pXmlElement);
	void ToXMLElement(TiXmlElement* p_pXmlElement) const;
};

#include "StateSet.inl"

#endif // STATESET_H_INCLUDED
