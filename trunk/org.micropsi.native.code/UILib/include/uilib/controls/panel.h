#ifndef UILIB_PANEL_H_INCLUDED 
#define UILIB_PANEL_H_INCLUDED

#include "uilib/core/window.h"

namespace UILib
{

/**
	Ein Panel ist ein Fenster mit einstellbarer Hintergrundfarbe
*/
class CPanel : public CWindow
{
public:

	static CPanel*	Create();

	void SetColor(const CColor& p_rxColor);
	CColor GetColor() const;

	virtual bool SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);
	virtual bool GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;


protected:

	CPanel();
	virtual ~CPanel();

	virtual void Paint(const CPaintContext& p_rxCtx);
	virtual CStr GetDebugString() const;

	CColor	m_xColor;				

private:
	CPanel(const CPanel&);
	operator=(const CPanel&);
};


} // namespace UILib


#endif // #ifndef UILIB_PANEL_H_INCLUDED 

