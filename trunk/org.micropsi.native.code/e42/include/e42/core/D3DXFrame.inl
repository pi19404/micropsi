//-----------------------------------------------------------------------------------------------------------------------------------------
inline
const char*
CD3DXFrame::GetName() const
{
    return this->Name;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CMat4S&
CD3DXFrame::TransformationMatrix()
{
    return *(CMat4S*)&__super::TransformationMatrix;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
const CMat4S&
CD3DXFrame::TransformationMatrix() const
{
    return *(const CMat4S*)&__super::TransformationMatrix;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CD3DXMeshContainer*
CD3DXFrame::GetFirstMeshContainer() const
{
    return (CD3DXMeshContainer*)this->pMeshContainer;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CD3DXFrame::SetFirstMeshContainer(CD3DXMeshContainer* pxMeshContainer)
{
    assert(this->pMeshContainer == NULL || pxMeshContainer == NULL);
    this->pMeshContainer = (D3DXMESHCONTAINER*)pxMeshContainer;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CD3DXFrame*
CD3DXFrame::GetFirstChild() const
{
    return (CD3DXFrame*)this->pFrameFirstChild;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CD3DXFrame* 
CD3DXFrame::GetSibling() const
{
    return (CD3DXFrame*)this->pFrameSibling;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
D3DXFRAME&
CD3DXFrame::D3DXFrame()
{
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
const D3DXFRAME&
CD3DXFrame::D3DXFrame() const
{
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CD3DXFrame::AddChild(CD3DXFrame* pxNewChild)
{
	assert(pxNewChild->pFrameSibling == NULL);
	assert(pxNewChild->pFrameParent == NULL);

    pxNewChild->pFrameSibling = this->pFrameFirstChild;
    this->pFrameFirstChild = pxNewChild;

	pxNewChild->pFrameParent = this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
