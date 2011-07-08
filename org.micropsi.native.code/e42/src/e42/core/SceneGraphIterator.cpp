#include "stdafx.h"

#include "e42/core/SceneGraphIterator.h"

#include "e42/core/D3DXFrame.h"
#include "e42/core/D3DXMeshContainer.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CSceneGraphIterator::CSceneGraphIterator(CD3DXFrame* pxStartFrame)
:	m_pxCurrentMeshContainer(NULL),
	m_pxCurrentFrame(NULL),
	m_pxStartFrame(pxStartFrame)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CSceneGraphIterator::~CSceneGraphIterator()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMeshContainer*
CSceneGraphIterator::GetCurrentMeshContainer() const
{
	return m_pxCurrentMeshContainer;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXFrame*
CSceneGraphIterator::GetCurrentFrame() const
{
	return m_pxCurrentFrame;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMeshContainer*
CSceneGraphIterator::GetNextMeshContainer()
{
	// wenn es einen MeshContainer gibt, dann den nächsten in der Liste nehmen
	if (m_pxCurrentMeshContainer)
	{
		m_pxCurrentMeshContainer = m_pxCurrentMeshContainer->NextMeshContainer();
	}


	// wenn Ende der Liste erreicht ist, dann den nächsten Frame mit MeshContainern suchen (oder das Ende)
	if (m_pxCurrentMeshContainer == NULL)
	{
		do
		{
			GetNextFrame();
		}
		while (m_pxCurrentFrame && m_pxCurrentFrame->GetFirstMeshContainer() == NULL);

		if (m_pxCurrentFrame)
		{
			m_pxCurrentMeshContainer = m_pxCurrentFrame->GetFirstMeshContainer();
		}
	}


	return m_pxCurrentMeshContainer;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXFrame*
CSceneGraphIterator::GetNextFrame()
{
	// prioritäten beim Durchiteriern der Frames:
	//	1. wenn aktueller Frame NULL, dann zum Start gehen
	//	2. zum Child des Frames gehen
	//	3. das Sibling des Parent^x wählen
	//		falls man dabei auf den StartFrame trifft, dann aufhören und CurrentFrame auf NULL setzen
	

	if (m_pxCurrentFrame == NULL)
	{
		// am Anfang den Pointer auf den Start setzen
		m_pxCurrentFrame = m_pxStartFrame;
	}
	else
	{
		if (m_pxCurrentFrame->GetFirstChild())
		{
			// erstmal immer abwärts gehen
			m_pxCurrentFrame = m_pxCurrentFrame->GetFirstChild();
		}
		else
		{
			// solange hochgehen bis wieder ein sibling kommt oder man wieder am start angekommen ist
			while (m_pxCurrentFrame != m_pxStartFrame && m_pxCurrentFrame->GetSibling() == NULL)
			{
				assert(m_pxCurrentFrame->pFrameParent);
				m_pxCurrentFrame = m_pxCurrentFrame->pFrameParent;
			}

			if (m_pxCurrentFrame != m_pxStartFrame)
			{
				// sibling wählen 
				m_pxCurrentFrame = m_pxCurrentFrame->GetSibling();
			}
			else
			{
				// ende
				m_pxCurrentFrame = NULL;
			}
		}
	}

	return m_pxCurrentFrame;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
