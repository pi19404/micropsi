#ifndef UILIB_LINEBREAKS_H_INCLUDED
#define UILIB_LINEBREAKS_H_INCLUDED

#include "baselib/rect.h"
#include "baselib/str.h"
#include "baselib/dynarray.h"
#include "font.h"

namespace UILib
{

/**
	\class CLineBreaks
	\brief CLineBreaks berechnet Zeilenumbr�che f�r einen Text.
	
	Diese Klasse berechnet Zeilenumbr�che f�r einen Text.

	Interne Funktion:
	Zentraler Bestandteil dieser Klasse ist das Array m_aiLines. Es enth�lt (Anzahl Zeilen + 1) Eintr�ge. 
	Jeder Eintrag ist der Index der Zeichnes im String, mit dem diese Zeile beginnt. Der Letzte Eintrag des Array zeigt immer
	auf den 0-Charakter am Ende des Strings. Es gilt:

	f�r 0 <= i < m_aiLines.Size():
	m_aiLines[i]	ist das erste Zeichen der Zeile i
	m_aiLines[i+1]	ist das erste Zeichen der i nachfolgenden Zeile
	die Zeile i hat die L�nge m_aiLines[i+1] - m_aiLines[i]
*/

class COutputDevice;
class CVisualization;

class CLineBreaks
{
public: 

	CLineBreaks();

	
	/// aufw�ndiges Update: Umbr�che bei bestimmter Zeilenl�nge, abh�ngig vom Text
	void				Update(	const CStr& p_sString, 
								int p_iWidthInPixels, 
								const COutputDevice* p_pxDevice, 
								CFontHandle p_hFont,
								bool p_bWordWrap = true);

	/// einfaches Update: nur "harte" Zeilenumbr�cke ('\n') werden gesucht
	void				Update(	const CStr& p_sString);

	int					GetNumLines() const;
	int					GetLineStart(int p_iLine) const;
	int					GetLineLength(int p_iLine) const;
	int					GetLineEnd(int p_iLine) const;				
	CStr				GetLine(int p_iLine, const CStr& p_sCompleteText, bool p_bTrimENDLNs = true);

private:

	bool				IsWhitespace(int p_iChar) const;
	int					FindBeginningOfNextWord(const CStr& p_rsString, int p_iStartIdx) const;
	
	CDynArray<int>		m_aiLines;				///< array mit Indizies der Zeilenanf�nge	
};

#include "uilib/core/linebreaks.inl"

} // namespace UILib

#endif  // ifndef UILIB_LINEBREAKS_H_INCLUDED
