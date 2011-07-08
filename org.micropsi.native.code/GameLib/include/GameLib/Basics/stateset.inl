//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CStateSet::StartIterateStates(StateIterator& iter) const
{
    iter = m_xStates.begin();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool
CStateSet::IterateStates(StateIterator& iter, std::string& po_rsKey) const
{
    if (m_xStates.empty() ||
        (iter == m_xStates.end()))
    {
        return false;
    }
    else
    {
		po_rsKey = iter->first;
        iter++;
        return true;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CStateSet::Clear()
{
    m_xStates.clear();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
std::string 
CStateSet::GetStateType(const std::string& sKey) const
{
	StateIterator i = m_xStates.find(sKey);
    assert(!m_xStates.empty() && (i != m_xStates.end()));
	return i->second.m_sType;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
std::string 
CStateSet::GetStateValue(const std::string& sKey) const
{
	StateIterator i = m_xStates.find(sKey);
    assert(!m_xStates.empty() && (i != m_xStates.end()));
	return i->second.m_sValue;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CStateSet::SetStateValue(const std::string& sKey, const std::string& sValue)
{
    assert(KeyExists(sKey));
    m_xStates[sKey].m_sValue = sValue;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CStateSet::KeyExists(const std::string& sKey) const
{
	return (!m_xStates.empty() && (m_xStates.find(sKey) != m_xStates.end()));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
int				
CStateSet::GetStateValueInt(const std::string& p_sKey) const
{
	StateIterator i = m_xStates.find(p_sKey);
    assert(!m_xStates.empty() && (i != m_xStates.end()));
	assert(i->second.m_sType == "int");
	return atoi(i->second.m_sValue.c_str());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
float			
CStateSet::GetStateValueFloat(const std::string& p_sKey) const
{
	StateIterator i = m_xStates.find(p_sKey);
    assert(!m_xStates.empty() && (i != m_xStates.end()));
	assert(i->second.m_sType == "float");
	return (float) atof(i->second.m_sValue.c_str());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CStateSet::SetStateValue(const std::string& p_sKey, int p_iValue)
{
    assert(KeyExists(p_sKey));
	assert(GetStateType(p_sKey) == "int");
	char ac[32];
	_snprintf(ac, 32, "%d", p_iValue);
    m_xStates[p_sKey].m_sValue = ac;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CStateSet::SetStateValue(const std::string& p_sKey, float p_fValue)
{
    assert(KeyExists(p_sKey));
	assert(GetStateType(p_sKey) == "float");
	char ac[32];
	_snprintf(ac, 32, "%f", p_fValue);
    m_xStates[p_sKey].m_sValue = ac;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
