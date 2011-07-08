//---------------------------------------------------------------------------------------------------------------------
inline
bool		
CDSoundBuffer::IsLooping() const
{
	return m_bLooping;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool		
CDSoundBuffer::IsStreamingBuffer() const
{
	return m_bStreamingBuffer;
}
//---------------------------------------------------------------------------------------------------------------------
inline
std::string 
CDSoundBuffer::GetFilename() const								
{ 
	if(m_pxSoundFile)
	{
		return m_pxSoundFile->GetFilename(); 
	}
	else
	{
		return "";
	}
} 
//---------------------------------------------------------------------------------------------------------------------
inline
void        
CDSoundBuffer::ResetPosition()                                 
{ 
	Seek(0); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CDSoundBuffer::SetPriority(float p_fPriority)					
{ 
	m_fPriority = p_fPriority; 
}
//---------------------------------------------------------------------------------------------------------------------
inline	
float		
CDSoundBuffer::GetPriority() const								
{
	return m_fPriority; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
float		
CDSoundBuffer::GetPitch() const								
{ 
	return m_fPitch; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CDSoundBuffer::FadeOut(int p_iTimeInMs)							
{ 
	Fade(0.0f, p_iTimeInMs); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CDSoundBuffer::FadeIn(int p_iTimeInMs, float p_fEndVol)	
{ 
	SetVolume(0.0f);
	Fade(p_fEndVol, p_iTimeInMs); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool		
CDSoundBuffer::IsLocked() const
{
	return m_bLocked;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
CDSoundBuffer::Lock()
{
	m_bLocked = true;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CDSoundBuffer::Unlock()
{
	m_bLocked = false;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CDSoundBuffer::Is3DSound() const
{
	return (m_pxDS83DBuffer != 0);
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool		
CDSoundBuffer::GetPosition(float& po_fX, float& po_fY, float& po_fZ) const
{
	po_fX = m_vPosition.x(); po_fY = m_vPosition.y(); po_fZ = m_vPosition.z(); 
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CDSoundBuffer::IsFading() const
{
	return m_bFading;
}


//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CDSoundBuffer::IsFree() const
{
	return  !m_bLocked  &&  
			(m_pxDS8Buffer == 0  ||  !IsPlaying());
}
//---------------------------------------------------------------------------------------------------------------------
