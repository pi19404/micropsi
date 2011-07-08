#ifndef UILIB_VISUALIZATIONFACTORY_H_INCLUDED
#define UILIB_VISUALIZATIONFACTORY_H_INCLUDED

#include <map>
#include "visualization.h"
#include "baselib/fourcc.h"

namespace UILib
{

/**
	Die VisualizationFactory stellt Visualisierungen bereit. Jede Implementation des CVisualization-Interfaces meldet sich 
	einmal bei ihr an. Visualisierungen müssen immer für ein konkretes Device erzeugt werden. Wenn es z.B. 3 verschiedene 
	Visualsierungen und 5 Devicetypen gibt, sind das theoretisch 15 mögliche Kombinationen, d.h. es müssen schlimmstenfalls
	15 Visualisierungen erzeugt werden. 
	Wenn eine Visualisierung für ein konkretes Device gebraucht wird, wird sie erzeugt, falls sie nicht schon existiert. 
*/
class CVisualizationFactory
{
public: 
	static CVisualizationFactory&		Get();
    static void                         Shut();

	void				RegisterVisualization(	const CFourCC& p_rxVisualizationType, 
												CVisualization* (*createFunction)(const COutputDevice*));

	CVisualization*		GetVisualization(const COutputDevice* p_pxDevice, const CFourCC& p_rxVisualizationType);


private:
	CVisualizationFactory();
	~CVisualizationFactory();

	static CVisualizationFactory*		ms_pxInstance;

	/// mappt Visualisierungstypen auf ihre Create-Funktionen
	std::map<CFourCC, CVisualization* (*)(const COutputDevice*)> m_xCreationMapping; 

	/// mappt Paare von DeviceType/Visualisierungs Typ auf erzeugte Visualisierung
	std::map<std::pair<CFourCC, CFourCC>, CVisualization*>	m_xVizMapping;		
};

} // namespace UILib

#endif // ifndef UILIB_VISUALIZATIONFACTORY_H_INCLUDED

