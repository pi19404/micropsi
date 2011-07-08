#ifndef BASELIB_COLOR_H_INCLUDED
#define BASELIB_COLOR_H_INCLUDED

#include "baselib/macros.h"


class CColor
{
public:

	union {
		unsigned long m_dwColor;
		struct {
			unsigned char	m_cBlue;
			unsigned char	m_cGreen;
			unsigned char	m_cRed;
			unsigned char	m_cAlpha;
		};
	};

	int			GetAlpha()	const	{ return m_cAlpha; }
	int			GetRed()	const	{ return m_cRed; }
	int			GetGreen()	const	{ return m_cGreen; }
	int			GetBlue()	const	{ return m_cBlue; }
	long		GetAll()	const	{ return m_dwColor; }

	void		SetAlpha(int p_iA)	{ m_cAlpha = (unsigned char) p_iA; }
	void		SetRed(int p_iR)	{ m_cRed   = (unsigned char) p_iR; }
	void		SetGreen(int p_iG)	{ m_cGreen = (unsigned char) p_iG; }
	void		SetBlue(int p_iB)	{ m_cBlue  = (unsigned char) p_iB; }
	void		SetAll(long p_iC)	{ m_dwColor = p_iC; }


	CColor()
	{
		m_dwColor = 0xff000000;
	}


	CColor(unsigned long p_iArg)
	{
		m_dwColor = p_iArg;
	}


	CColor(const CColor& p_krxArg)
	{
		m_dwColor = p_krxArg.m_dwColor;
	}


	CColor(int p_iR, int p_iG, int p_iB)
	{
		m_cRed   = (unsigned char) clamp(p_iR, 0, 255);
		m_cGreen = (unsigned char) clamp(p_iG, 0, 255);
		m_cBlue  = (unsigned char) clamp(p_iB, 0, 255);
		m_cAlpha = 255;
	}


	CColor(int p_iR, int p_iG, int p_iB, int p_iA)
	{
		m_cRed   = (unsigned char) clamp(p_iR, 0, 255);
		m_cGreen = (unsigned char) clamp(p_iG, 0, 255);
		m_cBlue  = (unsigned char) clamp(p_iB, 0, 255);
		m_cAlpha = (unsigned char) clamp(p_iA, 0, 255);
	}


	void SetFromWindowsSystemColor(unsigned long p_iSysColor)
	{
		m_cBlue  = (unsigned char) ((p_iSysColor >> 16) & 0xff);
		m_cGreen = (unsigned char) ((p_iSysColor >> 8)  & 0xff);
		m_cRed   = (unsigned char) ((p_iSysColor)       & 0xff);
		m_cAlpha = 255;
	}


	CColor operator+(const CColor& p_krxArg) const
	{
		return CColor(p_krxArg.m_cRed + m_cRed, 
					  p_krxArg.m_cGreen + m_cGreen, 
					  p_krxArg.m_cBlue + m_cBlue, 
					  p_krxArg.m_cAlpha + m_cAlpha);
	}


	CColor& operator+=(const CColor& p_krxArg)
	{
		m_dwColor = CColor(p_krxArg.m_cRed + m_cRed, 
						  p_krxArg.m_cGreen + m_cGreen, 
						  p_krxArg.m_cBlue + m_cBlue, 
						  p_krxArg.m_cAlpha + m_cAlpha).m_dwColor;
		return *this;
	}


	CColor operator-(const CColor& p_krxArg) const
	{
		return CColor(m_cRed   - p_krxArg.m_cRed, 
			          m_cGreen - p_krxArg.m_cGreen, 
					  m_cBlue  - p_krxArg.m_cBlue, 
					  m_cAlpha - p_krxArg.m_cAlpha);
	}


	CColor& operator-=(const CColor& p_krxArg)
	{
		m_dwColor = CColor(m_cRed   - p_krxArg.m_cRed,
						  m_cGreen - p_krxArg.m_cGreen,
						  m_cBlue  - p_krxArg.m_cBlue,
						  m_cAlpha - p_krxArg.m_cAlpha).m_dwColor;
		return *this;
	}


	// mischt die Farben; Alphakanal des zweiten Operanden ist entscheidend
	CColor operator*(const CColor& p_krxArg) const
	{
		if(p_krxArg.m_cAlpha==255)		{ return p_krxArg; } 

		int iA2 = (int) p_krxArg.m_cAlpha;
		int iA1 = 255 - iA2;
		return CColor( (m_cRed   * iA1 + p_krxArg.m_cRed   * iA2) >>8,
					   (m_cGreen * iA1 + p_krxArg.m_cGreen * iA2) >>8,
					   (m_cBlue  * iA1 + p_krxArg.m_cBlue  * iA2) >>8, 
					   max(m_cAlpha, p_krxArg.m_cAlpha));
	}


	// mischt die Farben; Alphakanal des zweiten Operanden ist entscheidend
	CColor& operator*=(const CColor& p_krxArg)
	{
		int iA2 = (int) p_krxArg.m_cAlpha;
		int iA1 = 255 - iA2;
		m_cRed   = (unsigned char) (((int) m_cRed   * iA1 + (int) p_krxArg.m_cRed   * iA2) >>8);
		m_cGreen = (unsigned char) (((int) m_cGreen * iA1 + (int) p_krxArg.m_cGreen * iA2) >>8);
		m_cBlue  = (unsigned char) (((int) m_cBlue  * iA1 + (int) p_krxArg.m_cBlue  * iA2) >>8); 
		m_cAlpha = max(m_cAlpha, p_krxArg.m_cAlpha);
		return *this;
	}


	CColor operator+(int p_iDelta) const
	{
		return CColor((int) m_cRed + p_iDelta, (int) m_cGreen + p_iDelta, (int) m_cBlue + p_iDelta, m_cAlpha);
	}


	CColor operator-(int p_iDelta) const
	{
		return CColor((int) m_cRed - p_iDelta, (int) m_cGreen - p_iDelta, (int) m_cBlue - p_iDelta, m_cAlpha);
	}


	bool operator==(const CColor& p_krxColor) const		{ return m_dwColor == p_krxColor.m_dwColor; }
	bool operator!=(const CColor& p_krxColor) const		{ return m_dwColor != p_krxColor.m_dwColor; }

	// konvertiert diese Farbe in einen 16-bit Farbwert (4 bpp)
	unsigned short int To4444() const 
	{
		return (unsigned short int) (((m_dwColor & 0xf0)       >> 4)  |
			                         ((m_dwColor & 0xf000)     >> 8)  | 
									 ((m_dwColor & 0xf00000)   >> 12) |
									 ((m_dwColor & 0xf0000000) >> 16));
	}


	void SetFrom4444(unsigned short int p_wColor) 
	{
		unsigned long dwC = (unsigned long) p_wColor;
		m_dwColor= ((dwC & 0xf)    << 0)  | ((dwC & 0xf)    << 4)  |
				   ((dwC & 0xf0)   << 4)  | ((dwC & 0xf0)   << 8)  |
				   ((dwC & 0xf00)  << 8)  | ((dwC & 0xf00)  << 12) |
				   ((dwC & 0xf000) << 12) | ((dwC & 0xf000) << 16);
	}


	static CColor From4444(unsigned short int p_wColor) 
	{
		unsigned long dwC = (unsigned long) p_wColor;
		return CColor(  ((dwC & 0xf)	<< 0)  | ((dwC & 0xf)    << 4)  |
						((dwC & 0xf0)   << 4)  | ((dwC & 0xf0)   << 8)  |
						((dwC & 0xf00)  << 8)  | ((dwC & 0xf00)  << 12) |
						((dwC & 0xf000) << 12) | ((dwC & 0xf000) << 16));
	}

};

#endif // ifndef BASELIB_COLOR_H_INCLUDED


