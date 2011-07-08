
#ifndef SPINCONTROLTEXT_H_INCLUDED 
#define SPINCONTROLTEXT_H_INCLUDED

#include "uilib/controls/basicspincontrol.h"
#include "baselib/dynarray.h"

namespace UILib
{


class CSpinControlText : public CBasicSpinControl
{
public:

	static CSpinControlText*	Create();
	
	virtual int		NumItems();
	virtual int		AddItem(const CStr& p_sString);
	virtual CStr	GetItem(int p_iIndex);
	virtual int 	FindItem(const CStr& p_sString);
	virtual int		DeleteItem(int p_iIndex);

	virtual int		GetSelectedItem() const;
	virtual CStr	GetText() const;
	virtual int		Select(int p_iIndex);

	virtual bool	SelectNextItemBeginningWithChar(int p_iChar);

protected:

	CSpinControlText();
	virtual ~CSpinControlText();

	virtual CStr	GetDebugString() const		{ return CStr("CSpinControlText Contents = ") + GetText(); }

	virtual bool	HandleMsg(const CMessage& p_rxMessage);

	virtual void	Up();
	virtual void	Down();

	int				m_iSelectedItem;			///< Index des aktuell angewählten Items aus der Auswahlliste
	CDynArray<CStr>	m_asStrings;				///< Auswahlliste


private:
	CSpinControlText(const CSpinControlText&) {}
	operator=(const CSpinControlText&) {}
};


} //namespace UILib


#endif //SPINCONTROLTEXT_H_INCLUDED

