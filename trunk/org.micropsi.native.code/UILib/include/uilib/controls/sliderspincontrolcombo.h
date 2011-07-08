#ifndef UILIB_SLIDERSPINCONTROLCOMBO_H_INCLUDED 
#define UILIB_SLIDERSPINCONTROLCOMBO_H_INCLUDED

#include "uilib/controls/panel.h"
#include "uilib/controls/label.h"

namespace UILib
{

class CSlider;
class CSpinControlNumber;

class CSliderSpinControlCombo : public CPanel
{
public:

	static CSliderSpinControlCombo*	Create();

	/// setzt Text des Labels
	void	SetLabelText(const CStr& p_rsText);

	/// bestimmt Textausrichtung - siehe enums in label.h
	void	SetTextAlign(CLabel::HorizontalTextAlignment p_eHTextAlignment, CLabel::VerticalTextAlignment p_eVTextAlignment = CLabel::TA_VCenter);

	/// setzt Minimalwert, Maximalwert und Schrittweiter
	void	SetLimits(float p_fMin, float p_fMax, float p_fStep);

	/// setzt den Wert
	void	SetValue(float p_fValue);

	/// \return Wert als float
	float	GetValue() const;

	/// definiert releative Breiten der Elemente im Gesamtfenster
	void	SetLayout(float p_fLabelWidth, float p_fSliderWidth, float p_fSpinControlWidth);

	/// setzt die Anzahl anzuzeigender Dezimalstellen
	void	SetDecimals(int p_iDecimals);

	/// bestimmt, ob der Hintergrund des Fensters gezeichnet werden muss  
	void	SetBackground(bool p_bBackground = true);


protected:

	CSliderSpinControlCombo();
	virtual ~CSliderSpinControlCombo();

	virtual CStr GetDebugString() const;
	virtual bool HandleMsg(const CMessage& p_rxMessage);
	virtual void Paint(const CPaintContext& p_rxCtx);
	virtual bool OnVisualizationChange();
	virtual bool OnResize();

	void		 DoLayout();
	void		 UpdateSliderPos();

private:
	CSliderSpinControlCombo(const CSliderSpinControlCombo&);
	operator=(const CSliderSpinControlCombo&);

	CLabel*				m_pxLabel;
	CSlider*			m_pxSlider;
	CSpinControlNumber*	m_pxSpinCtrl;

	bool				m_bDrawBackGround;				///< Hintergrund zeichenen?
	float				m_fLabelSpacePercent;			///< width of label control in percent of total widget width
	float				m_fSliderSpacePercent;			///< width of slider control in percent of total widget width
};

#include "sliderspincontrolcombo.inl"

} // namespace UILib


#endif // #ifndef UILIB_SLIDERSPINCONTROLCOMBO_H_INCLUDED 

