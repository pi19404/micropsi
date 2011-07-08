//-------------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CKeyboardState::IsKeyDown(int iDIKey) const
{
    return (((*m_pacCurrentKeyState)[iDIKey] & 0x80) == 0x80);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
float
CKeyboardState::GetKeyDownTime(int iDIKey) const
{
    return m_afKeyDownTime[iDIKey];
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
bool
CKeyboardState::KeyDownEvent(int p_iDIKey) const
{
    return	((*m_pacCurrentKeyState)[p_iDIKey] & 0x80) == 0x80  &&  
			((*m_pacLastKeyState)[p_iDIKey] & 0x80) != 0x80;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
bool
CKeyboardState::KeyUpEvent(int p_iDIKey) const
{
    return	((*m_pacCurrentKeyState)[p_iDIKey] & 0x80) != 0x80  &&  
			((*m_pacLastKeyState)[p_iDIKey] & 0x80) == 0x80;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CKeyboardState::ScrollLock() const
{
    return m_bScrollLock;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
