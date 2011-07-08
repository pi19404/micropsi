#include "stdafx.h"
#include "uilib/core/visualizationfactory.h"
#include "uilib/core/standardvisualization.h"


using std::map;
using std::pair;

namespace UILib
{

CVisualizationFactory* CVisualizationFactory::ms_pxInstance = 0;


//---------------------------------------------------------------------------------------------------------------------
CVisualizationFactory::CVisualizationFactory()
{
	RegisterVisualization(CFourCC("STND"), CStandardVisualization::Create);
}


//---------------------------------------------------------------------------------------------------------------------
CVisualizationFactory::~CVisualizationFactory()
{
    map<std::pair<CFourCC, CFourCC>, CVisualization*>::iterator cur;
    cur = m_xVizMapping.begin();
    while(cur != m_xVizMapping.end())
    {
        (cur->second)->Destroy();
        cur++;
    }
}


CVisualizationFactory& CVisualizationFactory::Get()
{
    if(ms_pxInstance == 0)
    {
        ms_pxInstance = new CVisualizationFactory();
    }
    return *ms_pxInstance;
}


void CVisualizationFactory::Shut()
{
    if(ms_pxInstance)
    {
        delete ms_pxInstance;
        ms_pxInstance = 0;
    }
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Liefert eine Instanz der geforderten Visualisierung für den geforderten 
	Devicetyp. Die Instanz wird nur dann neu erzeugt, wenn es noch keine 
	passende gibt.
*/
CVisualization*		
CVisualizationFactory::GetVisualization(const COutputDevice* p_pxDevice, const CFourCC& p_rxVisualizationType)
{
	map<pair<CFourCC, CFourCC>, CVisualization*>::iterator cur;
	pair<CFourCC, CFourCC> p;
	p.first  = p_pxDevice->GetType();
	p.second = p_rxVisualizationType;
	cur = m_xVizMapping.find(p);
	if(cur != m_xVizMapping.end())
	{
		// gefunden
		return cur->second;
	}
	else
	{
		// nicht gefunden --> neu erzeugen
		map<CFourCC, CVisualization* (*)(const COutputDevice*)>::iterator cur;
		cur = m_xCreationMapping.find(p_rxVisualizationType);
		assert(cur != m_xCreationMapping.end());						// sonst: unbekannter Typ!
		if(cur == m_xCreationMapping.end())
		{
			return 0;
		}
		CVisualization* pv = (cur->second)(p_pxDevice);
		m_xVizMapping[p] = pv;
		return pv;
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Registriert einen neuen Visualisierungstype; trägt dessen Create()-Funktion in einem Map ein
*/
void				
CVisualizationFactory::RegisterVisualization(	const CFourCC& p_rxVisualizationType, 
												CVisualization* (*createFunction)(const COutputDevice*))
{
	map<CFourCC, CVisualization* (*)(const COutputDevice*)>::iterator cur;
	cur = m_xCreationMapping.find(p_rxVisualizationType);
	assert(cur == m_xCreationMapping.end());						// sonst: Doppelanmeldung mit gleichem FourCC
		
	m_xCreationMapping[p_rxVisualizationType] = createFunction;
}

}

