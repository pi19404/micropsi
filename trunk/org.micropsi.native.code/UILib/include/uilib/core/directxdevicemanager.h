#pragma once
#ifndef UILIB_DIRECTXDEVICEMANAGER_H_INCLUDED
#define UILIB_DIRECTXDEVICEMANAGER_H_INCLUDED

#include <vector>
#include "baselib/handledset.h"

#include "uilib/core/directx9device.h"

namespace UILib
{
	
class CDirectXDeviceMgr
{
public:
	typedef unsigned int TDeviceHandle;

	static CDirectXDeviceMgr&	Get();
	static void					Shut();

	static TDeviceHandle		InvalidHandle();

	TDeviceHandle				CreateDevice(IDirect3DDevice9* p_pxD3DDevice, int p_iWidth, int p_iHeight, int p_iScreenWidth, int p_iScreenHeight);
	void						ReleaseDevice(TDeviceHandle p_hDevice);
	void						ReleaseAllDevices();

	CDirectX9Device*			GetDevice(TDeviceHandle p_hDevice);

	void						Render();

	void						BringDeviceToTop(TDeviceHandle p_hDevice);
	void						BringDeviceToBottom(TDeviceHandle p_hDevice);

	void						SetDeviceVisible(TDeviceHandle p_hDevice, bool p_bVisible);
	bool						IsDeviceVisible(TDeviceHandle p_hDevice) const;

	bool						DeviceAnimationRunning() const;

protected:

	CDirectXDeviceMgr();
	virtual ~CDirectXDeviceMgr();

	static CDirectXDeviceMgr*	ms_pxInst;

	class CDeviceEntry
	{
	public: 
		CDirectX9Device*		m_pxPhysicalDevice;
		bool					m_bVisible;
	};


	std::vector<CDeviceEntry*>	m_apxOrderedDevices;			///< devices in z-order bottom --> top
	CHandledSet<CDeviceEntry*>	m_apxAllDevices;				///< all devices
};

#include "directxdevicemanager.inl"

}

#endif // UILIB_DIRECTXDEVICEMANAGER_H_INCLUDED

