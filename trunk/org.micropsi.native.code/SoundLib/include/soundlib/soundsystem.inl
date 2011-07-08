//---------------------------------------------------------------------------------------------------------------------
inline
__int64						
CSoundSystem::GetTimeInMs()					
{ 
	return m_iCurrentTimeInMs; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool						
CSoundSystem::GetSoundEnabled() const		
{ 
	return m_bSoundEnabled; 
} 
//---------------------------------------------------------------------------------------------------------------------
inline
bool					
CSoundSystem::Get3DSound()				
{ 
	return ms_b3DSound; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CSoundSystem::GetNumListeners() const
{
	return (int) m_aAllListeners.size();
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CSoundSystem::SetListenerPos(float p_fX, float p_fY, float p_fZ, 
							 float p_fFrontX, float p_fFrontY, float p_fFrontZ,
							 float p_fUpX, float p_fUpY, float p_fUpZ)
{
	return SetListenerPos(0, p_fX, p_fY, p_fZ, p_fFrontX, p_fFrontY, p_fFrontZ, p_fUpX, p_fUpY, p_fUpZ);
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CSoundSystem::TCaps&
CSoundSystem::GetCaps() const
{
	return m_xHWCaps;
}
//---------------------------------------------------------------------------------------------------------------------
