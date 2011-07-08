#ifndef EVENTDISPLAYPANEL_H_INCLUDED
#define EVENTDISPLAYPANEL_H_INCLUDED

#include "uilib/controls/panel.h"
#include <vector>
#include <string>

namespace UILib
{
	class CLabel;
}

class CEventDisplayPanel : public UILib::CPanel
{
public:
	static	CEventDisplayPanel*	Create();

	void	Map(std::string sName, std::string sCondtion, float fUserValue = 0.0f);
	void	Update();

private:
    CEventDisplayPanel();
    virtual ~CEventDisplayPanel();

	virtual void DeleteNow();

	struct TCondition
	{
		UILib::CLabel*				m_pCondition;		///< Label for Condition
		UILib::CLabel*				m_pUserValue;
	};

	UILib::CLabel*				m_pName;				///< Event Name Label
	std::vector<TCondition>		m_aConditions;			///< Array with Condition Labels and Value Labels

	UILib::CLabel*				m_pTotalCount;			///< Label for total event count
	UILib::CLabel*				m_pLastCount;			///< Label for last ticks event count
	UILib::CLabel*				m_pValue;				///< Label for event value

	std::string					m_sName;				///< Event Name
	int							m_iTotalEvents;			///< TotalEvents

	static const int			ms_iLineHeight = 20;	///< Höhe einer Textzeile

};

#endif EVENTDISPLAYPANEL_H_INCLUDED

