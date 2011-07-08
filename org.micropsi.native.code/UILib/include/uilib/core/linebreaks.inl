//---------------------------------------------------------------------------------------------------------------------
inline
int					
CLineBreaks::GetNumLines() const							
{ 
	return m_aiLines.Size() -1; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int					
CLineBreaks::GetLineStart(int p_iLine) const				
{ 
	return m_aiLines[p_iLine]; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int					
CLineBreaks::GetLineLength(int p_iLine) const			
{ 
	return m_aiLines[p_iLine+1] - m_aiLines[p_iLine];
}	
//---------------------------------------------------------------------------------------------------------------------
inline
int					
CLineBreaks::GetLineEnd(int p_iLine) const				
{ 
	return m_aiLines[p_iLine+1]; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CStr				
CLineBreaks::GetLine(int p_iLine, const CStr& p_sCompleteText, bool p_bTrimENDLNs)
{
	CStr s = p_sCompleteText.Mid(GetLineStart(p_iLine), GetLineLength(p_iLine));
	if(p_bTrimENDLNs)
	{
		s.TrimRight('\n');
	}
	return s;
}
//---------------------------------------------------------------------------------------------------------------------
