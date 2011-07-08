#ifndef PARAMETERLIST_H_INCLUDED
#define PARAMETERLIST_H_INCLUDED

#include <string>
#include <vector>

class CParameterList
{
public:

	virtual std::string		ToXMLString() const;

	void		Add(const std::string& p_rsString);
	void		Add(int p_iInt);
	void		Add(float p_fFloat);
	void		Add(__int64 p_iInt);

private:

	std::vector<std::string>	m_asParams;
};

#endif // PARAMETERLIST_H_INCLUDED
