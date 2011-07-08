//---------------------------------------------------------------------------------------------------------------------
inline
const CKeyboardState* 
CInputManager::GetKeyboardState() const
{
    return m_pxKeyboardState;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CMouseState*
CInputManager::GetMouseState() const
{
    return m_pxMouseState;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CGamepadState*	
CInputManager::GetGamePadState(int iPad) const
{
	return m_apxGamepadStates[iPad];
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CInputManager::ConsumeEvent(std::string p_sCondition, std::string p_sGroup)
{
	return CheckEvent(p_sCondition, p_sGroup, true);
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CInputManager::HasEvent(std::string p_sCondition, std::string p_sGroup)
{
	return CheckEvent(p_sCondition, p_sGroup, false);
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CInputManager::GetNumGamepads() const
{
	return (int) m_apxGamepadStates.size();
}
//---------------------------------------------------------------------------------------------------------------------
