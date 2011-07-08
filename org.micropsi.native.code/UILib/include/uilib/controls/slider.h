#ifndef UILIB_SLIDER_H_INCLUDED
#define UILIB_SLIDER_H_INCLUDED

#include "uilib/core/window.h"

namespace UILib
{

class CSlider : public CWindow
{	
public:
	static CSlider* Create();

	enum SliderStyles
	{
		SL_Horizontal = 0 << 0,			///< horizontaler Slider
		SL_Vertical   = 1 << 0,			///< vertikaler Slider
	};

	/// bestimmt die Größe des Fensters
	virtual void	SetSize(const CSize& p_rxSize);

	/// bestimmt die Größe des Fensters
	void	        SetSize(int p_iWidth, int p_iHeight);

	/// bestimmt den Stil des Sliders (horizontal oder vertikal)
	void			SetStyle(int p_iStyle);

	/// bestimmt Maximalwert des Sliders
	void			SetSliderRange(int p_iRange);

	/// bestimmt die aktuelle Position des Sliders; muss zwischen 0 und GetSliderRange() liegen
    int				SetSliderPos(int p_iSliderPos);

	/// liefert die aktuelle Position des Sliders; liegt zwischen 0 und GetSliderRange() 
	int				GetSliderPos() const;

	/// liefet Maximalwert des Sliders
	int				GetSliderRange() const;

	/// schaltet das Zeichnen des Hintergrundes an oder aus; default ist an
	void			SetBackground(bool p_bBackground);

	/// liefert true, wenn das Zeichnen des Hintergrundes eingeschaltet ist
	bool			GetBackground() const;

	/// zum Überschreiben: wird aufgerufen, wenn sich an der Position des Sliders etwas ändert 
	virtual bool	OnChange();

	/// setzt Callbackfunktion für "Change"
	void			SetOnChangeCallback(CFunctionPointer1<CSlider*>& rxCallback);

protected:

	CSlider();
	virtual ~CSlider();

	/// löscht dieses Fenster sofort (sollte nur der WindowMgr machen)
	virtual void	DeleteNow();

	/// liefert einen String für Debug-Zwecke
	virtual CStr	GetDebugString() const;

	void CalcKnobRect();

	virtual bool HandleMsg(const CMessage& p_rxMessage);
	virtual void Paint(const CPaintContext& p_rxCtx);

	virtual bool OnLButtonDown(const CPnt& p_rxMousePos);
	virtual bool OnLButtonUp(const CPnt& p_rxMousePos);
	virtual bool OnMouseMove(const CPnt& p_rxMousePos);

	virtual bool OnResize();
	virtual bool OnVisualizationChange();

	int			m_iSliderPos;				///< aktuelle Slider-Position
	int			m_iSliderRange;				///< maximale Slider-Position
	int			m_iStyle;					///< Stil
	bool		m_bBackground;				///< Hintergrund zeichnen ja/nein
	bool		m_bSoftDrag;				///< true: erlaubt weiches Ziehen des Sliders mit der Maus (rein optisches Feature)

	bool		m_bDragging;				///< true, wenn der Griff momentan mit der Maus gezogen wird
	CRct		m_xKnobRct;					///< Griff-Rechteck; wird von CalcKnobRect() berechnet
	CPnt		m_xDragPoint;				///< während des Ziehens mit der Maus: relative Postion der Maus zum Griff
	CSize		m_xKnobSize;				///< Größe des Griffs; wird von Visualisierung festgelegt
	int			m_iSliderWidth;				///< Breite des Sliders; wird von Visualisierung festgelegt

	CFunctionPointer1<CSlider*>	m_xOnChangeCallback;	///< Callbackfunktion bei jeder Veränderung

private:
	CSlider(const CSlider&);
	operator=(const CSlider&);
};


static const char* msgSliderChanged = "SliderCh";
class CSliderChangedMsg : public CMessage
{ public: CSliderChangedMsg(WHDL hWnd) : CMessage(msgSliderChanged, false, true, hWnd)	{} };

#include "slider.inl"


} // namespace UILib


#endif	// ifndef UILIB_SLIDER_H_INCLUDED

