
#pragma once 
#ifndef UILIB_TOOLTIP_H_INCLUDED 
#define UILIB_TOOLTIP_H_INCLUDED 

#include "uilib/core/window.h"
#include "uilib/controls/label.h"

namespace UILib
{

class CToolTip : public CLabel
{
public:
	static CToolTip*	Create(WHDL p_hOwnerWindow);

	virtual void		AutoSize(bool p_bMayShrink = true);
	virtual CStr		GetDebugString() const;
	
	WHDL				GetOwnerWindow() const;

protected:

	CToolTip(WHDL p_hOwnerWindow);
	virtual ~CToolTip();
	
	virtual bool	HandleMsg(const CMessage& p_rxMessage);
	virtual void	Paint(const CPaintContext& p_rxCtx);
	virtual bool	OnDeviceChange();

private:

	void			UpdatePosition(CPnt p_xMousePos);
	bool			IsOnScreen(CSize p_xParentSize, CPnt p_xWindowPos);

	WHDL			m_hOwnerWindow;								///< Fenster, für das der ToolTip erzeugt wurde
	bool			m_bDeleted;	

	CToolTip(const CToolTip&);
	operator=(const CToolTip&);
};

#include "tooltip.inl"

} //namespace UILib


#endif //UILIB_TOOLTIP_H_INCLUDED

