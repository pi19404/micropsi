#include "stdafx.h"

#include "e42/core/D3DXFrame.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXFrame::CD3DXFrame()
{
    // geerbte Member initialisieren
    this->Name = NULL;
    this->TransformationMatrix().SetIdentity();

    this->pMeshContainer = NULL;
    this->pFrameSibling = NULL;
    this->pFrameFirstChild = NULL;


    // neue Member initialisieren
    this->matCombinedTransformation.SetIdentity();

	this->xBoundingSphere.Clear();
    
    this->bIsVisible = true;
    
    this->pVisibilitySwitchFrame = NULL;
    this->pFrameParent = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXFrame::~CD3DXFrame()
{
	delete [] Name;

    //assert(this->pMeshContainer == NULL);
    assert(this->pFrameSibling == NULL);
    assert(this->pFrameFirstChild == NULL);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXFrame::SetName(const char* pcName)
{
    if (pcName)
    {
        assert(Name == 0);
        Name = new char[strlen(pcName) + 1];
        strcpy(Name, pcName);
    }
    else
    {
        delete [] Name;
        Name = NULL;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXFrame* 
CD3DXFrame::Create()
{
    return new CD3DXFrame;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXFrame::Destroy(CD3DXFrame* pxFrame)
{
    delete pxFrame;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXFrame::RecursiveDestroy(CD3DXFrame* pxFrame)
{
	if (pxFrame->pMeshContainer)
	{
		CD3DXMeshContainer::RecursiveDestroy(pxFrame->GetFirstMeshContainer());
		pxFrame->pMeshContainer = NULL;
	}

	if (pxFrame->GetFirstChild())
	{
		RecursiveDestroy(pxFrame->GetFirstChild());
		pxFrame->pFrameFirstChild = NULL;
	}

	if (pxFrame->GetSibling())
	{
		RecursiveDestroy(pxFrame->GetSibling());
		pxFrame->pFrameSibling = NULL;
	}

	delete pxFrame;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// setzt die ParentFrameMember aller ChildMeshContainer neu
void
CD3DXFrame::UpdateChildMeshParentPointers()
{
	CD3DXMeshContainer* pxMeshContainer = GetFirstMeshContainer();

	while (pxMeshContainer)
	{
		pxMeshContainer->pFrameParent = this;
		pxMeshContainer = pxMeshContainer->NextMeshContainer();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// setzt die ParentFrameMember aller ChildMeshes neu
void
CD3DXFrame::UpdateChildFrameParentPointers()
{
	CD3DXFrame* pxChildFrame = GetFirstChild();

	while (pxChildFrame)
	{
		pxChildFrame->pFrameParent = this;
		pxChildFrame = pxChildFrame->GetSibling();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXFrame*
CD3DXFrame::CloneFrameHierarchy() const
{
	CD3DXFrame* pxClonedFrame = CD3DXFrame::Create();


	// Basisklassenmember kopieren
    pxClonedFrame->SetName(GetName());
    pxClonedFrame->TransformationMatrix() = this->TransformationMatrix();


	// Subhierarchie klonen (achtung: Pointer wurden mit Basisklasse kopiert)
	if (GetSibling())
	{
		pxClonedFrame->pFrameSibling = GetSibling()->CloneFrameHierarchy();
	}

	if (GetFirstChild())
	{
		pxClonedFrame->pFrameFirstChild = GetFirstChild()->CloneFrameHierarchy();
		pxClonedFrame->UpdateChildFrameParentPointers();
	}

	if (GetFirstMeshContainer())
	{
		pxClonedFrame->pMeshContainer = GetFirstMeshContainer()->CloneMeshContainerList();
		pxClonedFrame->UpdateChildMeshParentPointers();
	}


	// VisibilitySwitchFrame neu zuordnen
	if (pVisibilitySwitchFrame)
	{
		pxClonedFrame->pVisibilitySwitchFrame = 
			pxClonedFrame->FindChild(FindChildIdx(pVisibilitySwitchFrame));
	}


	// Boundingsphere&Visibility kopieren
	pxClonedFrame->matCombinedTransformation = matCombinedTransformation;
	pxClonedFrame->xBoundingSphere = xBoundingSphere;
    pxClonedFrame->bIsVisible = bIsVisible;

	return pxClonedFrame;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXFrame*
CD3DXFrame::FindChild(int iIdx) const
{
	assert(iIdx >= 0);

    CD3DXFrame* pChild = (CD3DXFrame*)this->pFrameFirstChild;

    while (pChild && iIdx > 0)
    {
        iIdx--;
        pChild = (CD3DXFrame*)pChild->pFrameSibling;
    }

	assert(iIdx == 0);

    return pChild;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int
CD3DXFrame::FindChildIdx(const CD3DXFrame* pxFrame) const
{
	int iChildIdx = 0;
	CD3DXFrame* pxChild = GetFirstChild();

	while (pxChild)
	{
		if (pxChild == pxFrame)
		{
			return iChildIdx;
		}

		pxChild = pxChild->GetSibling();
		iChildIdx++;
	}

	return -1;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXFrame::RemoveChild(CD3DXFrame* pxFrame)
{
	if (this->pVisibilitySwitchFrame == pxFrame)
	{
		this->pVisibilitySwitchFrame = NULL;
	}

    if (this->pFrameFirstChild)
    {
        if (this->pFrameFirstChild == pxFrame)
        {
            this->pFrameFirstChild = this->pFrameFirstChild->pFrameSibling;

			pxFrame->pFrameSibling = NULL;
			pxFrame->pFrameParent = NULL;
        }
        else
        {
            ((CD3DXFrame*)this->pFrameFirstChild)->RemoveSibling(pxFrame);
        }
    }
    else
    {
        assert(false);
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXFrame::AddSibling(CD3DXFrame* pxNewFrame)
{
	assert(pxNewFrame->pFrameSibling == NULL);
	assert(pxNewFrame->pFrameParent == NULL);

	pxNewFrame->pFrameSibling = this->pFrameSibling;
    this->pFrameSibling = pxNewFrame;

	pxNewFrame->pFrameParent = this->pFrameParent;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXFrame::RemoveSibling(CD3DXFrame* pxFrame)
{
    if (this->pFrameSibling)
    {
        if (this->pFrameSibling == pxFrame)
        {
            this->pFrameSibling = this->pFrameSibling->pFrameSibling;

			pxFrame->pFrameSibling = NULL;
			pxFrame->pFrameParent = NULL;
        }
        else
        {
            ((CD3DXFrame*)this->pFrameSibling)->RemoveSibling(pxFrame);
        }
    }
    else
    {
        assert(false);
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXFrame::AddMeshContainer(CD3DXMeshContainer* pxNewMeshContainer)
{
	assert(pxNewMeshContainer->NextMeshContainer() == NULL);
	assert(pxNewMeshContainer->pFrameParent == NULL);

	pxNewMeshContainer->NextMeshContainer() = (CD3DXMeshContainer*)this->pMeshContainer;
	this->pMeshContainer = pxNewMeshContainer;

	pxNewMeshContainer->pFrameParent = this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXFrame::RemoveMeshContainer(CD3DXMeshContainer* pxMeshContainer)
{
	if (this->pMeshContainer == pxMeshContainer)
	{
		this->pMeshContainer = pxMeshContainer->NextMeshContainer();

		pxMeshContainer->NextMeshContainer() = NULL;
	}
	else
	{
		CD3DXMeshContainer* pxCurrentMeshContainer = (CD3DXMeshContainer*)this->pMeshContainer;
		while (pxCurrentMeshContainer)
		{
			if (pxCurrentMeshContainer->NextMeshContainer() == pxMeshContainer)
			{
				assert(pxMeshContainer->pFrameParent == this);

				pxCurrentMeshContainer->NextMeshContainer() = pxMeshContainer->NextMeshContainer();

				pxMeshContainer->NextMeshContainer() = NULL;
				pxMeshContainer->pFrameParent = NULL;
				break;
			}

			pxCurrentMeshContainer = pxCurrentMeshContainer->NextMeshContainer();
		}

		assert(pxCurrentMeshContainer != NULL);
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CD3DXMeshContainer*
CD3DXFrame::FindMeshContainer(int iIdx) const
{
	assert(iIdx >= 0);

    CD3DXMeshContainer* pxMeshContainer = (CD3DXMeshContainer*)this->pMeshContainer;

    while (pxMeshContainer && iIdx > 0)
    {
        iIdx--;
        pxMeshContainer = (CD3DXMeshContainer*)pxMeshContainer->pNextMeshContainer;
    }

	assert(iIdx == 0);

    return pxMeshContainer;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
