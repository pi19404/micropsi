//-------------------------------------------------------------------------------------------------------------------------------------------
inline
CE42Application& 
CE42Application::Get()
{
    assert(ms_pE42Application);
    return *ms_pE42Application;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
bool
CE42Application::WindowIsActive() const
{
    return (GetActiveWindow() == m_ahndWindows[0]);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
int 
CE42Application::GetWindowWidth() const
{
	return m_xSettings.m_iWindowWidth;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
int 
CE42Application::GetWindowHeight() const
{
    return m_xSettings.m_iWindowHeight;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
void
CE42Application::RequestShutDown()
{
    m_bShutDownRequested = true;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CE42Application::ShutDownRequested() const
{
    return m_bShutDownRequested;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
HWND 
CE42Application::GetWindowHandle(int iHead) const
{
	if (m_ahndWindows.Size() == 0)
	{
		return NULL;
	}
	else
	{
		assert(iHead < (int)m_ahndWindows.Size());
		return m_ahndWindows[iHead];
	}
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
bool
CE42Application::SceneMultiSamplingEnabled() const
{
    return 
		(m_axD3DPresentParameters[0].MultiSampleType == D3DMULTISAMPLE_NONMASKABLE) &&
        (m_axD3DPresentParameters[0].MultiSampleQuality > 0);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
int
CE42Application::GetNumHeads() const
{
	return m_iNumHeads;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
IDirect3DSwapChain9*
CE42Application::GetSwapChain(int iHead) const
{
	if (iHead < (int)m_aspxSwapChains.Size())
	{
		return m_aspxSwapChains[iHead];
	}
	return NULL;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
IDirectInputDevice8A* 
CE42Application::GetMouseDevice() const
{
    return m_pDIDMouse;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
IDirectInputDevice8A* 
CE42Application::GetGamepadDevice(int iIndex) const
{
	if (iIndex < (int)m_aspxDIDGamepads.Size())
	{
		return m_aspxDIDGamepads[iIndex];
	}
	return NULL;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
IDirectInputDevice8A* 
CE42Application::GetKeyboardDevice() const
{
    return m_pDIDKeyboard;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
int
CE42Application::GetNumGamepads() const
{
	return m_aspxDIDGamepads.Size();
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
const CAppSettings& 
CE42Application::GetSettings() const
{
	return m_xSettings;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
