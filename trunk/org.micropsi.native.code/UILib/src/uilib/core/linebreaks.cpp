#include "stdafx.h"
#include "uilib/core/linebreaks.h"
#include "uilib/core/visualizationfactory.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------

CLineBreaks::CLineBreaks()
{
	m_aiLines.PushEntry(0);		// first line always starts with character 0
}


//---------------------------------------------------------------------------------------------------------------------
bool		
CLineBreaks::IsWhitespace(int p_iChar) const		
{ 
	return p_iChar == ' ' || p_iChar == '\t' || p_iChar == '\r' || p_iChar == '\n'; 
}


//---------------------------------------------------------------------------------------------------------------------
int	
CLineBreaks::FindBeginningOfNextWord(const CStr& p_rsString, int p_iStartIdx) const
{
	int i = p_iStartIdx;
	while (i < p_rsString.GetLength()  &&  !IsWhitespace(p_rsString[i]))
	{
		i++;
	}
	if(i == p_rsString.GetLength())
	{
		return i;
	}
	while (i < p_rsString.GetLength()  &&  IsWhitespace(p_rsString[i]))
	{
		i++;
	}
	return i;
}
//---------------------------------------------------------------------------------------------------------------------
void				
CLineBreaks::Update(const CStr& p_sString)
{
	Update(p_sString, 0, 0, 0, false);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CLineBreaks::Update(const CStr& p_sString, 
					int p_iWidthInPixels, 
					const COutputDevice* p_pxDevice, 
					CFontHandle p_hFont,
					bool p_bWordWrap)
{
	assert(!p_bWordWrap  ||  p_pxDevice);

	unsigned int iLine = 0;
	int iIdx  = 0;

	while(iIdx < p_sString.GetLength())
	{
		int iNextLineStart = iIdx;
		int iTmpIdx = iIdx;
		int iLen;
		
		// zunächst versuchen wir einen Zeilenumbruch zwischen den Wörtern
		if(p_bWordWrap)
		{
			do {
				iTmpIdx = FindBeginningOfNextWord(p_sString, iTmpIdx);
				iLen = p_pxDevice->GetTextWidth(p_hFont, p_sString.Mid(iIdx, iTmpIdx - iIdx));
				if(iLen < p_iWidthInPixels)
				{
					iNextLineStart = iTmpIdx;
				}
			} while(iLen < p_iWidthInPixels  &&  iTmpIdx < p_sString.GetLength());
		}

		// falls das nicht geklappt hat:
		// möglicherweise ist ein Wort so lang, dass es nicht auf die Zeile paßt
		// in diesem Fall müssen wir das Wort irgendwo in der Mitte zerschneiden
		if(iNextLineStart == iIdx)
		{
			if(p_bWordWrap)
			{
				iTmpIdx = iIdx;
				do {
					iTmpIdx++;
					iLen = p_pxDevice->GetTextWidth(p_hFont, p_sString.Mid(iIdx, iTmpIdx - iIdx));
					if(iLen < p_iWidthInPixels)
					{
						iNextLineStart = iTmpIdx;
					}
				} while(iLen < p_iWidthInPixels  &&  iTmpIdx < p_sString.GetLength());
			}		
			else
			{
				iNextLineStart = p_sString.GetLength();
			}
		}

		// prüfe auf harte Zeilenumbrüche, d.h. '\n'-Zeichen, vor dem eben berechneten Zeilenende
		// falls eines gefunden wird, hat es natürlich Vorrang 
		int i;
		for(i=iIdx; i<=iNextLineStart-1; ++i)
		{
			if(p_sString.GetAt(i) == '\n')
			{
				iNextLineStart=i+1;
				break;
			}
		}

		iLine++;
		if(m_aiLines.Size() <= iLine)
		{
			m_aiLines.PushEntry();
		}
		m_aiLines[iLine] = iNextLineStart;

		// Eventuell ist eine Zeile so kurz, dass kein einziges Zeichen hineinpaßt. 
		// Da können wir dann auch nichts machen
		if(p_bWordWrap  &&  iNextLineStart == iIdx)
		{
			break;
		}

		iIdx = iNextLineStart;

	} // while(iIdx < p_sString.GetLength())

	if(iLine == 0)
	{
		iLine++;
	}
	else if(p_sString.GetAt(p_sString.GetLength()-1) == '\n')
	{
		iLine++;
		if(m_aiLines.Size() <= iLine)
		{
			m_aiLines.PushEntry();
		}
		m_aiLines[iLine] = m_aiLines[iLine-1];
	}

	m_aiLines.SetSize(iLine + 1);				// Arraygröße korrigieren
	m_aiLines[iLine] = p_sString.GetLength();	// letzte "Zeile" ist der Terminator

	assert(m_aiLines.Size() >= 2);

/*
	DebugPrint("lines %d", m_aiLines.Size()-1);
	for(int i=0; i<m_aiLines.Size(); ++i)
	{
		DebugPrint("%d", m_aiLines[i]);
	}
*/
}	

//---------------------------------------------------------------------------------------------------------------------

} // namespace UILib

