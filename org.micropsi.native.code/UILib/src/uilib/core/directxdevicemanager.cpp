#include "stdafx.h"
#include "uilib/core/directxdevicemanager.h"

using std::vector;

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------

CDirectXDeviceMgr* CDirectXDeviceMgr::ms_pxInst = 0;

//---------------------------------------------------------------------------------------------------------------------
CDirectXDeviceMgr::CDirectXDeviceMgr()
{
}

//---------------------------------------------------------------------------------------------------------------------
CDirectXDeviceMgr::~CDirectXDeviceMgr()
{
	ReleaseAllDevices();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CDirectXDeviceMgr::Shut()
{
    if(ms_pxInst)
    {
        delete ms_pxInst;
        ms_pxInst = 0;
    }
}

//---------------------------------------------------------------------------------------------------------------------
CDirectXDeviceMgr::TDeviceHandle
CDirectXDeviceMgr::CreateDevice(IDirect3DDevice9* p_pxD3DDevice, int p_iWidth, int p_iHeight, int p_iScreenWidth, int p_iScreenHeight)
{
	assert(p_pxD3DDevice);

	CDeviceEntry* p = new CDeviceEntry();
	p->m_pxPhysicalDevice = new CDirectX9Device(p_pxD3DDevice, p_iWidth, p_iHeight, p_iScreenWidth, p_iScreenHeight);
	p->m_bVisible = true;

	m_apxOrderedDevices.push_back(p);
	TDeviceHandle h = m_apxAllDevices.PushEntry(p);
	
	return h;
}

//---------------------------------------------------------------------------------------------------------------------
void
CDirectXDeviceMgr::ReleaseDevice(CDirectXDeviceMgr::TDeviceHandle p_hDevice)
{
	CDeviceEntry* p = m_apxAllDevices.Element(p_hDevice);
	if(p)
	{
		delete p->m_pxPhysicalDevice;
		m_apxAllDevices.DeleteEntry(p_hDevice);

		vector<CDeviceEntry*>::iterator i;
		for(i=m_apxOrderedDevices.begin(); i!=m_apxOrderedDevices.end(); i++)
		{
			if(*i == p)
			{
				m_apxOrderedDevices.erase(i);
				break;
			}
		}
		delete p;
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CDirectXDeviceMgr::ReleaseAllDevices()
{
	for(unsigned int i=0; i<m_apxOrderedDevices.size(); ++i)
	{
		delete m_apxOrderedDevices[i]->m_pxPhysicalDevice;
		delete m_apxOrderedDevices[i];
	}
	m_apxAllDevices.Clear();
	m_apxOrderedDevices.clear();
}

//---------------------------------------------------------------------------------------------------------------------
CDirectX9Device*
CDirectXDeviceMgr::GetDevice(CDirectXDeviceMgr::TDeviceHandle p_hDevice)
{
	CDeviceEntry* p = m_apxAllDevices.Element(p_hDevice);
	if(p)
	{
		return p->m_pxPhysicalDevice;
	}
	else
	{
		return 0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CDirectXDeviceMgr::DeviceAnimationRunning() const
{
	for(unsigned int i=0; i<m_apxOrderedDevices.size(); ++i)
	{
		if(m_apxOrderedDevices[i]->m_pxPhysicalDevice->AnimationRunning())
		{
			return true;
		}
	}
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
void
CDirectXDeviceMgr::Render()
{
	for(unsigned int i=0; i<m_apxOrderedDevices.size(); ++i)
	{
		if(m_apxOrderedDevices[i]->m_bVisible)
		{
			m_apxOrderedDevices[i]->m_pxPhysicalDevice->Render2D();
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CDirectXDeviceMgr::BringDeviceToTop(CDirectXDeviceMgr::TDeviceHandle p_hDevice)
{
	CDeviceEntry* p = m_apxAllDevices.Element(p_hDevice);
	assert(p);
	if(p)
	{
		for(unsigned int i=0; i<m_apxOrderedDevices.size(); ++i)
		{
			if(m_apxOrderedDevices[i] == p)
			{
				for(unsigned int j=i; j<m_apxOrderedDevices.size()-1; ++j)
				{
					m_apxOrderedDevices[j] = m_apxOrderedDevices[j+1];
				}
				m_apxOrderedDevices[m_apxOrderedDevices.size() -1] = p;
				break;
			}
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CDirectXDeviceMgr::BringDeviceToBottom(CDirectXDeviceMgr::TDeviceHandle p_hDevice)
{
	CDeviceEntry* p = m_apxAllDevices.Element(p_hDevice);
	assert(p);
	if(p)
	{
		for(unsigned int i=0; i<m_apxOrderedDevices.size(); ++i)
		{
			if(m_apxOrderedDevices[i] == p)
			{
				for(unsigned int j=i; j>=1; --j)
				{
					m_apxOrderedDevices[j] = m_apxOrderedDevices[j-1];
				}
				m_apxOrderedDevices[0] = p;
				break;
			}
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CDirectXDeviceMgr::SetDeviceVisible(CDirectXDeviceMgr::TDeviceHandle p_hDevice, bool p_bVisible)
{
	CDeviceEntry* p = m_apxAllDevices.Element(p_hDevice);
	assert(p);
	if(p)
	{
		p->m_bVisible = p_bVisible;
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CDirectXDeviceMgr::IsDeviceVisible(CDirectXDeviceMgr::TDeviceHandle p_hDevice) const
{
	CDeviceEntry* p = m_apxAllDevices.Element(p_hDevice);
	assert(p);
	if(p)
	{
		return p->m_bVisible;
	}
	else
	{
		return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------

} // namespace UILib