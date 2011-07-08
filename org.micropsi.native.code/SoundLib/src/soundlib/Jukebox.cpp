#include "baselib/str.h"
#include "SoundLib/SoundSystem.h"
#include <algorithm>

#include "SoundLib/Jukebox.h"

using std::string;
using std::vector;

namespace SoundLib
{

//---------------------------------------------------------------------------------------------------------------------
CJukebox::CJukebox()
{
	m_bShuffle		= false;							
	m_bCrossFading	= true;
	m_fVolume		= 100.0f;
	m_bEnabled		= true;
	m_iVoice		= -1;
	m_iCurrentSong	= 0;
}

//---------------------------------------------------------------------------------------------------------------------
CJukebox::~CJukebox()
{
	Clear();
}

//---------------------------------------------------------------------------------------------------------------------
void	
CJukebox::Clear()
{
	Stop();

	m_aOrderedSongs.clear();
	m_aRandomSongs.clear();
	m_iCurrentSong = 0;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CJukebox::AddSong(const char* pcNameAndPath)
{
	m_aOrderedSongs.push_back(CSong(pcNameAndPath));

	size_t iRandomPos = rand() % (m_aRandomSongs.size() + 1);
	m_aRandomSongs.insert(m_aRandomSongs.begin() + iRandomPos, CSong(pcNameAndPath));
}

//---------------------------------------------------------------------------------------------------------------------
void	
CJukebox::AddFolder(const char* pcPath, const char* pcSearchPattern)
{
	CStr sPath = pcPath;
	sPath.Replace('/', '\\');
	if(sPath.Right(1) != "\\")
	{
		sPath += "\\";
	}
	WIN32_FIND_DATA FindFileData;
	HANDLE hFind;
    hFind = FindFirstFile((sPath + pcSearchPattern).c_str(), &FindFileData);
	if (hFind != INVALID_HANDLE_VALUE) 
	{
		AddSong((sPath + FindFileData.cFileName).c_str());
		while (FindNextFile(hFind, &FindFileData))
		{
			AddSong((sPath + FindFileData.cFileName).c_str());
		}
		FindClose(hFind);
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CJukebox::Play()
{
	if(!m_bEnabled)
	{
		return false;
	}

	if(IsPlaying())
	{
		return true;
	}

	m_iVoice = CSoundSystem::Get().FindFreeVoice(FLT_MAX);
	if(m_iVoice < 0)
	{
		return false;
	}

	CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iVoice);
	pxVoice->Lock();

	pxVoice->SetFile(GetNextSong().c_str());
	pxVoice->FadeIn(2000, m_fVolume);
	if(!pxVoice->Play())
	{
		pxVoice->Unlock();
		m_iVoice = -1;
		return false;
	}

	return true;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CJukebox::Stop(bool bFadeout)
{
	if(IsPlaying())
	{
		CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iVoice);
		if(bFadeout)
		{
			pxVoice->FadeOut(2000);
		}
		else
		{
			pxVoice->Stop();
		}

		pxVoice->Unlock();
		m_iVoice = -1;
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CJukebox::IsPlaying()
{
	return m_iVoice >= 0;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CJukebox::Skip()
{
	if(IsPlaying())
	{
		CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iVoice);
		if(m_bCrossFading  &&  pxVoice->IsPlaying())
		{
			pxVoice->FadeOut(2000);
			pxVoice->Unlock();
			m_iVoice = -1;

			Play();
		}
		else
		{
			pxVoice->Stop();
			pxVoice->SetFile(GetNextSong().c_str());
			pxVoice->SetVolume(m_fVolume);
			pxVoice->Play();
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void	
CJukebox::SetShuffleMode(bool bShuffle)
{
	m_bShuffle = bShuffle;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CJukebox::SetCrossFading(bool bCrossfading)
{
	m_bCrossFading = bCrossfading;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CJukebox::SetVolume(float fVolume) 
{
	m_fVolume = fVolume;
	if(IsPlaying())
	{
		CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iVoice);
		pxVoice->SetVolume(m_fVolume);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void	
CJukebox::Tick()
{
	if(IsPlaying())
	{
		CDSoundBuffer* pxVoice = CSoundSystem::Get().GetVoice(m_iVoice);
		if(pxVoice->GetRemainingPlayTimeInSeconds() < 2.0)
		{
			Skip();
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void	
CJukebox::Reshuffle()
{
	for(unsigned int i=0; i<m_aRandomSongs.size(); ++i)
	{
		m_aRandomSongs[i].m_iRandomNumber = rand();
	}
	std::sort(m_aRandomSongs.begin(), m_aRandomSongs.end());
}

//---------------------------------------------------------------------------------------------------------------------
std::string
CJukebox::GetNextSong()
{
	assert(m_aOrderedSongs.size() == m_aRandomSongs.size());

	if(m_aOrderedSongs.size() == 0)
	{
		return "";
	}

	m_iCurrentSong++;
	if(m_iCurrentSong >= (int) m_aOrderedSongs.size())
	{
		if(m_bShuffle)
		{
			Reshuffle();
		}
		m_iCurrentSong = 0;
	}

	if(m_bShuffle)
	{
		return m_aRandomSongs[m_iCurrentSong].m_sFileAndPath;
	}
	else
	{
        return m_aOrderedSongs[m_iCurrentSong].m_sFileAndPath;
	}
}

//---------------------------------------------------------------------------------------------------------------------
void	
CJukebox::SetEnabled(bool bEnabled)
{
	if(!bEnabled  &&  IsPlaying())
	{
		Stop();
	}
	m_bEnabled = bEnabled;
}
//---------------------------------------------------------------------------------------------------------------------
bool	
CJukebox::GetEnabled() const
{
	return m_bEnabled;
}
//---------------------------------------------------------------------------------------------------------------------

} // namespace SoundLib
