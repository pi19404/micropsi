#pragma once
#ifndef COMPASS_H_INCLUDED
#define COMPASS_H_INCLUDED

namespace UILib
{
	class CDirectX9Device;
	class CLabel;
}

class CCompass
{
public:
	CCompass();
	~CCompass();

	void		Render();

private:

	UILib::CDirectX9Device*	m_pxBGDevice;					///< device f�r Hintergrund
	UILib::CDirectX9Device*	m_pxPointerDevice;				///< device f�r Kompassnadel
	
	UILib::CLabel*			m_pxBGLabel;
	UILib::CLabel*			m_pxPointerLabel;				
};

#include "compass.inl"

#endif // COMPASS_H_INCLUDED
