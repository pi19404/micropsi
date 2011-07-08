//---------------------------------------------------------------------------------------------------------------------
inline
UILib::CDirectX9Device*					
CUIScreenStateMachine::GetDesktopDevice(int iDeviceIndex)
{
	return m_axDesktopDevices[iDeviceIndex].pxDesktopDevice;
}
//---------------------------------------------------------------------------------------------------------------------
inline
UILib::CDirectXDeviceMgr::TDeviceHandle	
CUIScreenStateMachine::GetDesktopDeviceHandle(int iDeviceIndex)
{
	return m_axDesktopDevices[iDeviceIndex].hDesktopDevice;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CUIScreenStateMachine::InterfaceIsOpaque()
{
    return m_pxCurrentScreen  &&  m_pxCurrentScreen->IsOpaque();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool
CUIScreenStateMachine::DeviceAnimationRunning() const
{
	return UILib::CDirectXDeviceMgr::Get().DeviceAnimationRunning();
}
//---------------------------------------------------------------------------------------------------------------------
inline
CInputManager*
CUIScreenStateMachine::GetInputManager() const
{
	return m_pxInputManager;
}
//---------------------------------------------------------------------------------------------------------------------
