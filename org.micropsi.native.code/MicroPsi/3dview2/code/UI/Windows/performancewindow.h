#pragma once
#ifndef PERFORMANCEWINDOW_H_INCLUDED
#define PERFORMANCEWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"

namespace UILib
{
	class CLabel;
};

class CPerformanceWindow : public UILib::CDialogWindow
{
public:

	static CPerformanceWindow*		Create();

	void	Update();

protected:

	CPerformanceWindow();
	virtual ~CPerformanceWindow();	

	virtual void		DeleteNow();

	UILib::CLabel*		m_pxPos;
	UILib::CLabel*		m_pxFPS;
	UILib::CLabel*		m_pxTerrainTileVisibleCount;
	UILib::CLabel*		m_pxObjectVisibleCount;
	UILib::CLabel*		m_pxObjectVisibleCountReflection;
	UILib::CLabel*		m_pxObjectVisibleCountShadowMap;
};

#endif // PERFORMANCEWINDOW_H_INCLUDED

