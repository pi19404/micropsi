//---------------------------------------------------------------------------------------------------------------------
inline
CDirectXDeviceMgr& 
CDirectXDeviceMgr::Get()							
{ 
    if(!ms_pxInst)
    {
        ms_pxInst = new CDirectXDeviceMgr();
    }
    return *ms_pxInst; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CDirectXDeviceMgr::TDeviceHandle		
CDirectXDeviceMgr::InvalidHandle()
{
	return CHandledSet<CDeviceEntry*>::InvalidHandle();
}
//---------------------------------------------------------------------------------------------------------------------
