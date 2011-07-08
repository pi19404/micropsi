#include "Application/stdinc.h"
#include "ParameterList.h"

#include "baselib/str.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
std::string		
CParameterList::ToXMLString() const
{
	string s = " ";

	for(unsigned int i=0; i<m_asParams.size(); ++i)
	{
		CStr sParam = CStr::Create("param%d=\"", i);
		s += sParam.c_str();
		s += m_asParams[i];
		s += "\" ";
	}

	return s;
}

//---------------------------------------------------------------------------------------------------------------------
void
CParameterList::Add(const std::string& p_rsString)
{
	m_asParams.push_back(p_rsString);
}

//---------------------------------------------------------------------------------------------------------------------
void
CParameterList::Add(int p_iInt)
{
	CStr s = CStr::Create("%d", p_iInt);
	m_asParams.push_back(s.c_str());
}

//---------------------------------------------------------------------------------------------------------------------
void
CParameterList::Add(float p_fFloat)
{
	CStr s = CStr::Create("%f", p_fFloat);
	m_asParams.push_back(s.c_str());
}

//---------------------------------------------------------------------------------------------------------------------
void		
CParameterList::Add(__int64 p_iInt)
{
	CStr s = CStr::Create("%I64d", p_iInt);
	m_asParams.push_back(s.c_str());
}
//---------------------------------------------------------------------------------------------------------------------
